
import com.typesafe.config.ConfigFactory
import controllers.EmployeeController
import org.apache.pekko
import pekko.actor.typed.ActorSystem
import pekko.actor.typed.scaladsl.Behaviors
import pekko.http.scaladsl.Http
import scala.util.{Failure, Success, Try}


object Main {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val server = config.getConfig("server")

    val port = server.getInt("port");
    val host = server.getString("host")

    implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "my-system")

    Try(Http().newServerAt(host, port).bind(EmployeeController.route)) match{
      case Success(_) => println(s"Server Running on Port : $port")
      case Failure(exception) => println(s"Error starting server: ${exception.getMessage}")
    }
  }
}