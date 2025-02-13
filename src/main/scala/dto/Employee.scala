package dto

import java.time.LocalDate
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.json4s.{Formats, NoTypeHints}

case class Employee(id: Option[Int], name: String, email: String)

object Employee {
  implicit val formats: Formats = Serialization.formats(NoTypeHints)
  def toJson(employee: Employee): String = write(employee)
}
