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

  def asAutoAck(subscribe: MessageHandler => Subscription): Observable[Message] =
    Observable.defer {
      val seqNumsQueue = mutable.Set[Long]()

      Observable
        .create[Message](dropOldOnOverflow) { subscriber =>
          val msgHandler = new MessageHandler {
            override def onMessage(msg: Message): Unit =
            // if the message redelivered while being in the queue
              if (!seqNumsQueue.add(msg.getSequence)) {
                println(s"Observable: skip seen msg: ${msg.getSequence}")
              } else {
                println(s"Observable: push new msg: ${msg.getSequence}")
                subscriber.onNext(msg)
              }
          }
          val subscription = subscribe(msgHandler)
          Cancelable(() => Try(subscription.close()))
        }
        .flatMap(msg =>
          Observable.unit.bracketCase(_ => Observable.delay(msg))(release = (_, exitCase) =>
            Task.evalOnce {
              seqNumsQueue -= msg.getSequence
              exitCase match {
                case _ => msg.ack()
              }
              println(s"Observable: acknowledging ${msg.getSequence}")
            }
          )
        )
      /*.map(msg =>
          Resource.makeCase(Observable.delay(msg)) { case (doneMsg, reason) =>
            // important to use evalOnce because Task is lazy, otherwise it might repeat this thing (in theory)
            Observable.evalOnce {
              reason match {
                case ExitCase.Completed => doneMsg.ack()
                case _ => // do not ack in case of Failure/Cancellation
              }
              seqNumsQueue -= doneMsg.getSequence
              println(s"Observable: acknowledging ${doneMsg.getSequence}")
            }
          }
        )*/
    }
}

object Tests extends App {

  import monix.execution.Scheduler.Implicits.global
  import NatsObservable.asAutoAck
  import NatsTestSetup._

  val deduplicingConsumer = Consumer.foldLeft[Set[Long], Message](Set.empty) {
    case (seenMessages, msg) =>
      println(s"Consuming: ${msg.getSequence}")
      if (seenMessages.contains(msg.getSequence)) println(s"Duplicate: ${msg.getSequence}")
      seenMessages + msg.getSequence
  }

  val events = asAutoAck(subscribeToNats)
  val task = events.consumeWith(deduplicingConsumer)

  task.runSyncUnsafe(100.seconds)

  println("Done.")

}