import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import scala.collection.mutable
import scala.concurrent.duration._
import scala.collection.mutable.ListBuffer
import scala.util.Random

object Process {
  def apply(id: Int, initialNeighbors: Map[Int, ActorRef[ChandyLamportMessage]],mainActor: ActorRef[ChandyLamportMessage]): Behavior[ChandyLamportMessage] =
    Behaviors.setup { context =>
      Behaviors.withTimers { timers =>
        var snapshotTaken = false // Define snapshotTaken as mutable variable
        var balance: Int = 2000
        var neighbors: Map[Int, ActorRef[ChandyLamportMessage]] = initialNeighbors
        // Using a mutable queue to simulate a non-FIFO processing order.

        val markersReceived = mutable.Set[Int]()
        val inTransitMessages = mutable.Map[Int,ChandyLamportMessage]()
        // Regularly send transaction messages to simulate activity
        timers.startTimerWithFixedDelay("sendBasicMessage",SendMessagesToNeighbors, 3.seconds)

        // Function to initiate a snapshot locally at this process
        def initiateSnapshot(): Unit = {
          if (!snapshotTaken) {
            //            context.log.info(s"Process $id: Initiating local snapshot with balance: $balance")
            snapshotTaken = true
            // Print the snapshot immediately
            context.log.info(s"Snapshot at Process $id - Balance: $balance")
            //Tracking intransit messages
            if (inTransitMessages.nonEmpty) {
              context.log.info(s"In-transit messages at Process $id: ${inTransitMessages.values.mkString(", ")}")
            }
            neighbors.values.foreach(_ ! Marker(id))
            // Send marker to all neighbors
            mainActor ! SnapshotStatus(id, completed = true)
          }
        }


        // Handling incoming messages
        Behaviors.receiveMessage { message =>
          message match{
            //Initiate snapshot on request
            case RequestSnapshotStart =>
              if (!snapshotTaken) {
                initiateSnapshot()
              }
              Behaviors.same
//              Send marker
            case Marker(senderId) =>
              if (!snapshotTaken) {
                //                context.log.info(s"Process $id: Received snapshot signal from Process $senderId with balance $amount")
                initiateSnapshot()
              }
              Behaviors.same
              //Take basic messages and update balance
            case TransactionMessage(amount,messageId) =>
              balance += amount
//              context.log.info(s"Process $id: Updated balance $balance due to message from Process ${from.path.name}")
              //Storing messages to check later for in transit messages
              inTransitMessages(messageId) = message
            //Setting neighbors for a process
            case SetNeighbors(newNeighbors) =>
              neighbors = newNeighbors
//            Sending msg to neighbors
            case SendMessagesToNeighbors =>
              neighbors.values.foreach { neighbor =>
                // Ensure not to send a message to itself
                if (neighbor != context.self) {
                  neighbor ! TransactionMessage(50,Random.nextInt())
                }
              }
              Behaviors.same
            //Resetting flag when global snapshot is completed
            case ResetSnapshot =>
              snapshotTaken=false
            case _=>Behaviors.unhandled
          }
          Behaviors.same
        }
      }
    }

}