package playground.effects

import cats.effect.IO

trait First_BasicAnalogy {

  // Imperative "OOP" style
  def a(in: A, out: B): Unit = ??? // mutate B inside; throw exception
  def b(in: B, out: C): Unit = ??? // send requests; possibly mutate C

  // FP style
  def f(a: A): Either[Error, B]
  def g(b: B): IO[C]

  // Composition
  val a: A
  val results = f(a).map(g)


  // ------- Benefits:
  // - Great to compose - like a lego blocks while sockets are matching you can plug in anything
  // - Safe to change - you can extract/inline/rename anything because it has no internal state
  // - Easier to reason about the code in isolation
  // - Compatible with big FP ecosystem

  // But it also not that simple. And here is where FP libraries comes handy
}
