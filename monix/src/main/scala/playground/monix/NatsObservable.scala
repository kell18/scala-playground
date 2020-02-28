package playground.monix

import cats.effect.{ExitCase, Resource}
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

  def withAutomaticAcks(subscribe: MessageHandler => Subscription): Observable[Message] = Observable.defer {
    val seenSequenceNums = mutable.Set[Long]()

    Observable
      .create[Message](dropOldOnOverflow) { subscriber =>
        val msgHandler = new MessageHandler {
          override def onMessage(msg: Message): Unit =
            // if the message redelivered while being in the queue
            if (!seenSequenceNums.add(msg.getSequence)) {
              println(s"NatsObservable: skip seen msg: ${msg.getSequence}")
            } else {
              subscriber.onNext(msg)
            }
        }
        val subscription = subscribe(msgHandler)
        Cancelable(() => Try(subscription.close()))
      }
      .flatMap(msg =>
        Observable.unit.bracketCase(_ => Observable.delay(msg))(release = (_, exitCase) =>
          Task.evalOnce {
            // Note: different strategies might be used, eg do ack on ExitCase.Error as well
            if (exitCase == ExitCase.Completed) {
              msg.ack()
            }
            seenSequenceNums -= msg.getSequence
          }
        )
      )
  }

  def dropOldOnOverflow[A] = OverflowStrategy.DropOldAndSignal[A](
    100000,
    buffSize =>
      Coeval.raiseError(
        new RuntimeException(s"Buffer is overflow (got $buffSize elements), dropping old ones")
      )
  )
}

object Tests extends App {

  import monix.execution.Scheduler.Implicits.global
  import NatsTestSetup._

  val deduplicingConsumer = Consumer.foldLeft[Set[Long], Message](Set.empty) {
    case (seenMessages, msg) =>
      println(s"Consuming: ${msg.getSequence}")
      if (seenMessages.contains(msg.getSequence)) println(s"Duplicate: ${msg.getSequence}")
      seenMessages + msg.getSequence
  }

  val events = NatsObservable.withAutomaticAcks(subscribeToNats)
  val task = events.consumeWith(deduplicingConsumer)

  task.runSyncUnsafe(100.seconds)

  println("Done.")

}
