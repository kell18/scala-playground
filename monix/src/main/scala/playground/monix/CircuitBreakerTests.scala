package playground.monix

import monix.catnap.CircuitBreaker
import monix.eval._
import scala.concurrent.duration._

object CircuitBreakerTests {

  import monix.execution.Scheduler.Implicits.global

  val circuitBreaker: Task[CircuitBreaker[Task]] =
    CircuitBreaker[Task].of(
      maxFailures = 5,
      resetTimeout = 10.seconds
    )

  val problematic = Task {
    val nr = util.Random.nextInt()
    if (nr % 2 == 0) nr else
      throw new RuntimeException("dummy")
  }

  val r = circuitBreaker.flatMap(_.protect(problematic))

}
