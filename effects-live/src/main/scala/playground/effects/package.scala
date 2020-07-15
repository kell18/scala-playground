package playground

package object effects {
  case class A()
  case class B()
  case class C()

  case class Error() extends Throwable
}
