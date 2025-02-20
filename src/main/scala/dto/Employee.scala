package dto

import org.json4s.jackson.Serialization
import org.json4s.{Formats, NoTypeHints}

case class Employee(id: Option[Int], name: String, email: String, address: String, salary: Double)

object Employee {
  implicit val formats: Formats = Serialization.formats(NoTypeHints)
}
