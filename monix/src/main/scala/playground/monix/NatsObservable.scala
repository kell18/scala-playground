package playground.monix

import cats.effect.Resource
import io.nats.streaming.{Message, MessageHandler, StreamingConnection, StreamingConnectionFactory, Subscription, SubscriptionOptions}
import cats.implicits._
import monix.catnap.MVar
import monix.eval.{Coeval, Task}
import monix.execution.Cancelable
import monix.reactive.{Consumer, Observable, Observer, OverflowStrategy}
import monix.reactive.observers.Subscriber
import scala.collection.mutable
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Try

object NatsObservable {

  def dropOldOnOverflow[A] = OverflowStrategy.DropOldAndSignal[A](
    100000,
    buffSize =>
      Coeval.raiseError(
        new RuntimeException(s"Buffer is overflow (got $buffSize elements), dropping old ones")
      )
  )

  // .. test it works as Observable.unit.bracket https://monix.io/blog/2018/11/07/tutorial-bracket.html
  def asAutoAck(
    subscribe: MessageHandler => Subscription,
    overflowStrategy: OverflowStrategy.Synchronous[Message] = dropOldOnOverflow
  ): Observable[Message] =
    asResource(subscribe, dropOldOnOverflow).mapEval(_.use(msg => Task.pure(msg)))

  def asResource(
    subscribe: MessageHandler => Subscription,
    overflowStrategy: OverflowStrategy.Synchronous[Message] = dropOldOnOverflow
  ): Observable[Resource[Task, Message]] = Observable.defer {
    val seqNumsQueue = mutable.Set[Long]()

    Observable
      .create[Message](dropOldOnOverflow) { subscriber =>
        val msgHandler = new MessageHandler {
          override def onMessage(msg: Message): Unit =
            // if the message redelivered while being in the queue
            if (!seqNumsQueue.add(msg.getSequence)) {
              println(s"Skip seen msg: ${msg.getSequence}")
            } else {
              println(s"Push a new msg: ${msg.getSequence}")
              subscriber.onNext(msg)
            }
        }
        val subscription = subscribe(msgHandler)
        Cancelable(() => Try(subscription.close()))
      }
      .filter(msg => seqNumsQueue.contains(msg.getSequence))
      .map(msg =>
        Resource.make(Task.pure(msg))(doneMsg =>
          Task
            .evalOnce { // very important because Task is lazy, without evalOnes it might repeat this thing (in theory)
              doneMsg.ack()
              seqNumsQueue -= doneMsg.getSequence
            }
        )
      )
    // .flatMap(msg => Observable.unit.bracket(_ => Observable.pure(msg))(release = _ => Task(msg.ack())))
  }
}

object TaskExample extends App {

  import monix.execution.Scheduler.Implicits.global

  val task = Task(println("Nothing happens on creation!"))
    .map(_ => 1 + 1)
    .flatMap(_ => computePi)
    .delayExecution(1.second)

  val future = task.runToFuture
  val cancelable = task.runAsync { /* handle results */ }

  cancelable.cancel()

  val newTask = Task.fromFuture(future)
  // Or if you have not yet running future (eg function of a future):
  val lazyTask = Task.deferFuture(future)

  def computePi: Task[Double] = ???

  // If we change our mind...
  cancelable.cancel()
}

object Tests extends App {

  import monix.execution.Scheduler.Implicits.global
  import NatsObservable.asAutoAck

  val natsAckTmOut = 2.seconds

  var options = new SubscriptionOptions.Builder()
    .maxInFlight(5)
    .manualAcks()
    .durableName("local-tests1")
    .deliverAllAvailable()
    .ackWait(java.time.Duration.ofNanos(natsAckTmOut.toNanos))

  val natsConnection = NatsConnectionConfig(
    "nats://172.31.5.186:4222",
    "test-cluster",
    "reactive-sync_dev-albert",
    Some(natsAckTmOut)
  )
  val connection = createConnection(natsConnection)

  val seqNumsQueue = mutable.Set[Long]()
  val consumer = Consumer.foreachTask[Message] { msg =>
    Task {
      println(s"Consuming: ${msg.getSequence}")
      if (!seqNumsQueue.add(msg.getSequence)) println(s"got duplicate: ${msg.getSequence}")
      Thread.sleep(2000)
    }
  }

  val observalbe = asAutoAck(msgHandler => connection.subscribe("pe-matcher-staging.au", msgHandler, options.build()))
  val task = observalbe.consumeWith(consumer)

  task.runSyncUnsafe(100.seconds)

  // StdIn.readLine()

  final case class NatsConnectionConfig(
    uri: String,
    clusterId: String,
    clientId: String,
    pubAckTimeout: Option[Duration] = None
  )

  def createConnection(cfg: NatsConnectionConfig): StreamingConnection = {
    val connectionFactory = new StreamingConnectionFactory(cfg.clusterId, cfg.clientId)
    connectionFactory.setNatsUrl(cfg.uri)
    cfg.pubAckTimeout.foreach(d => connectionFactory.setAckTimeout(java.time.Duration.ofMillis(d.toMillis)))
    connectionFactory.createConnection()
  }

  def observable1(connection: StreamingConnection) = new Observable[Message] {
    override def unsafeSubscribeFn(subscriber: Subscriber[Message]) = new Cancelable {
      val msgHandler = new MessageHandler {
        def onMessage(msg: Message): Unit = subscriber.onNext(msg)
      }

      val subscription = connection.subscribe("subj", msgHandler)

      override def cancel(): Unit = Try(subscription.close())
    }
  }
}

/*def observableMut1(conn: StreamingConnection, subject: String): Observable[Message] = Observable.defer {

    Observable
      .create[Message](OverflowStrategy.Unbounded) { subscriber =>
        val msgHandler = new MessageHandler {
          override def onMessage(msg: Message): Unit = {
            subscriber.onNext(msg)
          }
        }
        val subscription = conn.subscribe(subject, msgHandler, options.build())
        Cancelable(() => Try(subscription.close()))
      }
      .collect()
      .flatS(new mutable.ListMap[Long, Message]()) {
        case (messages, nextMsg)/* if messages.contains(nextMsg.getSequence)*/ =>
          Observable.fromIterable(Seq(1, 2, 3))
      }
      .flatMap(msg => Observable.unit.bracket(_ => Observable.pure(msg))(release = _ => Task(msg.ack())))
  }*/

/*def observableRef(conn: StreamingConnection, subject: String) = {
  Observable
    .create[Message](OverflowStrategy.Unbounded) { subscriber =>
      val msgHandler = new MessageHandler {
        override def onMessage(msg: Message): Unit = {
          subscriber.onNext(msg)
        }
      }
      val subscription = conn.subscribe(subject, msgHandler, options.build())
      Cancelable(() => Try(subscription.close()))
    }
  .scanEvalF(MVar.empty[Task, mutable.ListMap[Long, Message]]()) {
    case (messages, nextMsg) =>
      messages
        .take
        .flatMap { msgs =>
          msgs + (nextMsg.getSequence -> nextMsg)
        }
      println(s"Redelivery of ${nextMsg.getSequence}")
      println(s"size: ${messages.size}")
      messages
    case (messages, nextMsg) =>
      println(s"adding size: ${messages.size}")
      messages + (nextMsg.getSequence -> nextMsg)
  }
    .flatMap(messages => Observable.fromIterable(messages.values))
}*/

/*.mapAccumulate[Set[Long], Option[Message]](Set.empty[Long]) {
  case (seqNumbers, msg) if seqNumbers.contains(msg.getSequence) => seqNumbers -> None
  case (seqNumbers, msg) => (seqNumbers + msg.getSequence) -> Option(msg) // .. cleanup
}*/
// .collect { case Some(msg) => msg }
// .groupBy(_.getSequence)(???)
/*.map { msg =>
  if (msg.isRedelivered) println(s"Gor redelivery for ${msg.getSequence}")
  msg
}*/
// .distinctUntilChangedByKey(_.getSequence)

/*.foldLeft(Map.empty[Long, Message]) {
  case (queue, nextMsg) if nextMsg.isRedelivered => /** log or smth */
  case (queue, nextMsg) => queue + (nextMsg.getSequence -> nextMsg)
}*/
