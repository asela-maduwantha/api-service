package publisher

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.{ DefaultConsumer, Envelope}
import com.typesafe.config.ConfigFactory
import config.RabbitMQConfig
import java.util.UUID
import java.util.concurrent.{ArrayBlockingQueue, TimeUnit}
import scala.util.{Failure, Success, Using}

object Publisher {
  private val config = ConfigFactory.load()
  private val rabbitmqConfig = config.getConfig("rabbitmq")
  private val QUEUE_NAME = rabbitmqConfig.getString("queueName")

  def sendRequest(message: String): Option[String] = {
    val result = Using.Manager { use =>
      val connection = use(RabbitMQConfig.getConnectionFactory.newConnection())
      val channel = use(connection.createChannel())

      val replyQueueName = channel.queueDeclare().getQueue
      val correlationId  = UUID.randomUUID().toString

      val props: BasicProperties = new BasicProperties.Builder()
        .correlationId(correlationId)
        .replyTo(replyQueueName)
        .build()

      channel.basicPublish("", QUEUE_NAME, props, message.getBytes("UTF-8"))
      println(s"Request Sent to the queue: $message")

      val responseQueue = new ArrayBlockingQueue[String](1)

      val consumer = new DefaultConsumer(channel) {
        override def handleDelivery(
                                     consumerTag: String,
                                     envelope: Envelope,
                                     properties: BasicProperties,
                                     body: Array[Byte]
                                   ): Unit = {
          if (properties.getCorrelationId == correlationId) {
            responseQueue.offer(new String(body, "UTF-8"))
          }
        }
      }
      channel.basicConsume(replyQueueName, true, consumer)

      Option(responseQueue.poll(5, TimeUnit.SECONDS))
    }

    result match {
      case Success(responseOpt) =>
        responseOpt match {
          case Some(response) => Some(response)
          case None =>
            println("RPC timed out.")
            None
        }
      case Failure(exception) =>
        println(s"RPC error: ${exception.getMessage}")
        None
    }
  }
}
