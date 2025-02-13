package publisher

import com.rabbitmq.client.Connection
import com.typesafe.config.ConfigFactory
import config.RabbitMQConfig
import dto.Employee
import dto.Employee.toJson

import java.time.LocalDate
import scala.util.{Failure, Success, Try, Using}


object Publisher {
  private val config = ConfigFactory.load()
  private val rabbitmqConfig = config.getConfig("rabbitmq")

  private val QUEUE_NAME = rabbitmqConfig.getString("queueName")

  def createEmployee(employee: Employee): Unit = {
    Try(RabbitMQConfig.getConnectionFactory.newConnection()) match{
      case Success(connection: Connection) =>
        Using(connection.createChannel()){ channel =>
          channel.queueDeclare(QUEUE_NAME, false, false, false, null)

          Try(channel.basicPublish("",QUEUE_NAME, null, Employee.toJson(employee).getBytes("UTF-8"))) match{
            case Success(_) => println("data sent to the queue")
            println(Employee.toJson(employee))
            case Failure(exception) => println(s"Error Sending data ${exception.getMessage}")
          }
        }
      case Failure(exception) => println(s"Failed to create connection ${ exception.getMessage}")
    }
  }
}
