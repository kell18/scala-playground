package playground.effects

import cats.effect.IO

trait First_BasicAnalogy {

  // Imperative "OOP" style
  def a(in: A, out: B): Unit = ??? // mutate B inside; throw exception
  def b(in: B, out: C): Unit = ??? // send requests; possibly mutate C

  def f(a: A): Either[Error, B]
  def g(b: B): IO[C]

  val a: A
  val c = f(a).map(g)

}
