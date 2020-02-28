package playground.monix

import cats.effect.{ExitCase, Resource}
import io.nats.streaming.{Message, MessageHandler, StreamingConnection, StreamingConnectionFactory, Subscription, SubscriptionOptions}
import cats.implicits._
import monix.catnap.MVar
import monix.eval.{Coeval, Task}
import monix.execution.{Ack, Cancelable}
import monix.reactive.{Consumer, Observable, Observer, OverflowStrategy}
import monix.reactive.observers.Subscriber
import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Try

object ShowcaseNotes

object ResourceNats extends App {

  import monix.execution.Scheduler.Implicits.global
  import NatsTestSetup._


  val events = NatsObservable.asAutoAck(subscribeToNats)
    .doOnNextAck {
      case (msg, ack) => Task.delay(println(s"-- Got ack ($ack) for ${msg.getSequence}"))
    }
    .doOnNext { msg =>
      Task.delay(println(s"-- Received msg seq: ${msg.getSequence}"))
    }
    .mapEval(longSideEffect)
    .mapEval(longSideEffect)
    .mapEval(longSideEffect)
    .doOnNext { msg =>
      Task.eval(println(s"-- Processed: ${msg.getSequence}"))
    }

  val task = events.consumeWith(deduplicingConsumer)

  task.runSyncUnsafe(100.seconds)

  println("Done.")



  def longSideEffect(msg: Message): Task[Message] = Task {
    Thread.sleep(1000)
    msg
  }

}

object TaskAndFuture extends App {
  import monix.execution.Scheduler.Implicits.global

  val ftr = Future {
    println("Started")
    Thread.sleep(3000)
    println("T1")
    Thread.sleep(3000)
    println("T2")
    Thread.sleep(3000)
    println("T3")
    Thread.sleep(3000)
  }

  Thread.sleep(300)

  val tsk = Task.deferFuture(ftr).doOnCancel(Task(println("Canceled")))

  val cancelable = tsk.runToFuture

  StdIn.readLine()

  cancelable.cancel()

  println("Done.")
}
