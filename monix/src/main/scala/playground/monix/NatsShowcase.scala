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
import scala.concurrent.Await
import scala.io.StdIn
import scala.util.Try
import scala.concurrent.duration._

object NatsSource {

  def simple() = Observable.create[Message](OverflowStrategy.DropOld) { subscriber =>
    val msgHandler = new MessageHandler {
      override def onMessage(msg: Message): Unit = subscriber.onNext(msg)
    }

  }
}

object NatsShowcase extends App {

  import monix.execution.Scheduler.Implicits.global

  val simpleOne = Observable
    .fromIterable(List(1, 2, 3))
    .map(_ % 2 == 0)
    .zipWithIndex
    .flatMap { case (isDiv, index) => if (isDiv) Observable.fromIterable(1 to index) else Observable.empty }

  val simpleConsumer = Consumer.foreach[Long](println)

  val task = simpleOne.consumeWith(simpleConsumer)

  Await.result(task.runToFuture, 3.seconds)

  // .. some theme to the showcase like Nats-like messages of call compose external calls from Future and Akka streams

}
