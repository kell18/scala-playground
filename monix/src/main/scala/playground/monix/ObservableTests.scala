package playground.monix

import cats.implicits.catsSyntaxEitherId
import io.nats.client.Message
import monix.reactive.Observable
import scala.concurrent.duration.DurationInt

trait RepeatMerge {
  val events: Observable[Message] = ???

  val initialState: Map[Message, String] = ???

  val every20Sec = Observable.timerRepeated(0.seconds, 20.seconds, ())

  Observable(every20Sec.map(_.asLeft), events.map(_.asRight)).merge
    .scan(initialState) {
      case (state, Left(_))      => state // change Gauge
      case (state, Right(event)) => state + (event -> "something") // update state given new event
    }
}

class ObservableTests {}
