package playground.effects

import cats.implicits._
import cats.effect.{IO, Resource}
import cats.effect.implicits._
import cats.Monad
import playground.sql.{City, CityId, MetroSystemWithCity}
import scala.io.Source
import scala.language.higherKinds

trait TypeClasses {

  /******* Canonical type-classes example ********/

  // ----- Notes:
  // We want to have this, but without tight coupling:
  // Dog.encode
  // 1 - we need type-class definition
  // 2 - TC syntax
  // 3 - TC instances (possibly derived)
  // --- Benefits:
  // - Less coupling
  // - Any external class could have an instance
  // - Ability to build ecosystem around it easily (more later in the examples)

  /// OOP
  case class Cat(name: String) {
    def encode: String = name
  }

  /// FP
  trait Encoder[T] {
    def apply(s: T): String
  }
  // Will be improved in Dotty
  implicit class EncoderSyntax[T](t: T) {
    def encode(implicit encode: Encoder[T]) = encode(t)
  }

  // This could be part of the library you don't own
  case class Dog(name: String)

  object DogInstances {
    // This could be provided by third party lib or derived by macro
    implicit val dogEncoder = new Encoder[Dog] {
      override def apply(d: Dog) = d.name
    }
  }

  import DogInstances.dogEncoder
  val dog = Dog("Bob")
  val s = dog.encode

  // it's like a mixin on steroids



}
trait TypeClassesExamples {

  /******* Traverse/Sequence - More practical TC ********/

  def f(a: A): Either[Error, B]

  val inputs: List[A]
  val wrongResult: List[Either[Error, B]] = inputs.map(f)

  // "first error, or the whole list of successful results"
  val traversed: Either[Error, List[B]] = inputs.traverse(f)
  // this is fail-fast, if you want to accumulate errors there is Validated Applicative in replacement for Either
  // Applicative is weaker version of monad without FlatMap, read details in the docs

  // Same but without function arg
  val sequenced: Either[Error, List[B]] = wrongResult.sequence



  /******* Lenses (quicklens) ********/
  /// Not exactly a type-class but very similar and important to have sometimes

  case class Nested(a: A, b: B)
  case class Complex(n: Nested)

  val c = Complex(Nested(A(), B()))

  import com.softwaremill.quicklens._
  c.modify(_.n.a).setTo(A())


  /******* Resource ie try/finally on steroids ********/
  // -------

  def createFileSource: IO[Source]
  def releaseFileSource(s: Source): IO[Unit] = IO(s.close())

  val io1 = createFileSource.bracket(_ => IO("Success"))(releaseFileSource)
  io1.map(???).flatMap(???) // And releaseFileSource will be called afterwards no matter what


  /// Data type for the same purpose

  // And it's a monad!
  val resource = Resource.make(createFileSource)(releaseFileSource).map(_.getLines)
  val io = resource.use { lines =>
    // And here we need to return the same monad as well .. Explain the monad
    IO(println(s"Read lines: ${lines.mkString}"))
  }


  /******* Monads/Functors/Applicatives ********/
  // ------
  // Now we can define a monad __TC__:
  //  "Monad is an essence of imperative programming:
  //      do first thing and if it succeeded - do the second thing (flat map)"

  /// -- Tests example with Monad interface
  // ! But its debatable question if we need that / probably only in libs

  class Program(cityService: CityService) {

    def computeMetroPopularity(cityName: String): IO[Float] = for {
      city <- cityService.cityByName(cityName)
      metro <- cityService.cityMetroById(city.id)
    } yield metro.dailyRidership / city.population.toFloat

  }

  trait CityService {
    def cityByName(name: String): IO[City]
    def cityMetroById(id: CityId): IO[MetroSystemWithCity]
  }

}
