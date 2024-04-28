import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.collection.mutable
import scala.concurrent.duration._
import scala.collection.mutable.ListBuffer
import scala.util.Random
// Define a Process object as an actor
object Process {
  def apply(id: Int, initialNeighbors: Map[Int, ActorRef[Message]],mainActor: ActorRef[Message]): Behavior[Message] =
    Behaviors.setup { context =>
      Behaviors.withTimers { timers =>
        var snapshotTaken = false // Track if a snapshot has been taken by this process
        var balance: Int = 1000
        var neighbors: Map[Int, ActorRef[Message]] = initialNeighbors
        // Using a mutable queue to simulate a non-FIFO processing order.
        val messagePool = ListBuffer[Message]()//Buffer to store basic msgs
        val controlMsgPool=mutable.Queue[Message]()//Pool to store control msgs
        val markersReceived = mutable.Set[Int]()
        var preSnapMsgCount = 0//Count of messages that were pre snapshot
        val inTransitMessages = mutable.Map[Int,Message]()//Track intransit messages at this process
        val snapshotStatus = mutable.Map(neighbors.keys.map(_ -> false).toSeq: _*)
        // Start a timer to send basic messages periodically to simulate process operations
        //Basic message contains snapshotTaken to inform neighbors
        timers.startTimerWithFixedDelay("sendBasicMessage", SendMessagesToNeighbors, 3.seconds)



        // Initiates the snapshot for this process
        def initiateSnapshot(): Unit = {
          if (!snapshotTaken) {
//            context.log.info(s"Process $id: Initiating local snapshot with balance: $balance")
            snapshotTaken = true
            // Print the snapshot immediately
            context.log.info(s"Snapshot at Process $id - Balance: $balance")
            // Displaying in transit msgs at this process
            if (inTransitMessages.nonEmpty) {
              context.log.info(s"In-transit messages at Process $id: ${inTransitMessages.values.mkString(", ")}")
            }
            // Notify all neighbors to start their snapshot
            neighbors.values.foreach(_ ! ControlMessage(id, preSnapMsgCount, snapshot = true))
//            Notifying main when actor has completed snapshot
            mainActor ! SnapshotStatus(id, completed = true)
          }
        }
//        Processing control Msgs separately
        def processControlMessage(message: Message): Unit = message match {
          case ControlMessage(senderId, preSnapMsgCount, snapshot) =>
//            context.log.info(s"Process $id received snapshot marker from process $senderId with count: $count")
            if (snapshot && !snapshotTaken)
            {
              initiateSnapshot()
            }
            markersReceived += senderId
            snapshotStatus(senderId) = true // Mark sender as complete


//            val updatedStatus = status.updated(id, snapshotTaken)
//            if (updatedStatus.values.forall(identity)) { // Check if all are true
//              if (senderId == id) {
//                context.log.info(s"All processes have completed their snapshot as initiated by Process $id.")
//              } else {
//                neighbors.values.foreach(_ ! ControlMessage(senderId, balance, snapshot=true))
//              }
//            } else {
//              neighbors.values.foreach(neighbor => neighbor ! ControlMessage(senderId, balance, snapshot=true))
//            }
            // Check if all markers are received
//            if (markersReceived.size == neighbors.size) {
//              context.log.info("----------------------Global snapshot completed.---------------------")
//              neighbors.values.foreach(_ ! RelayResetSnapshotState(context.self, context.self))
//            }
          case _ => // Handle other potential control-related messages
        }

        //Processing Basic messages and tracking in transit msgs
        def processMessage(message: Message): Unit = message match {
          case BasicMessage(amount, tag,from,messageId) =>
            balance += amount
            from ! AcknowledgeMessage(messageId)//totrack intransit messages at sender
            if (tag && !snapshotTaken) {
//              context.log.info(s"Process $id triggers snapshot due to tagged message from another process.")
              initiateSnapshot()
            }
            Behaviors.same
            //If a message is acknowledged its removed from in transit
          case AcknowledgeMessage(id) =>
            inTransitMessages -= id
            Behaviors.same

          case _ => Behaviors.unhandled
        }


        // Process Basic msgs randomly to enforce non-FIFO behaviour
        def processMessages(): Behavior[Message] = {
//          context.log.info(s"$id in processmsg")
          while(controlMsgPool.nonEmpty)
            {
              val controlMsg=controlMsgPool.dequeue()
              processControlMessage(controlMsg)
            }
            //messagePool for basic messages
          val rand = new Random
          while (messagePool.nonEmpty) {
            val idx = rand.nextInt(messagePool.size)
            val msg = messagePool.remove(idx)
            processMessage(msg)
          }
          Behaviors.same
        }


        // Define a behavior to handle incoming messages
        Behaviors.receiveMessage { message =>
          message match{
            case RequestSnapshotStart =>
            if (!snapshotTaken) {
                initiateSnapshot()
              }
              Behaviors.same
              //Processing control msg and taking snaps only when sender has taken snap and current has not
            case ControlMessage(senderId, preSnapMsgCount, snapshot) =>
//              context.log.info(s"$snapshot && $snapshotTaken")
              if (snapshot && !snapshotTaken) {
//                context.log.info(s"Process $id: Received snapshot signal from Process $senderId with balance $amount")
                initiateSnapshot()
              }
              Behaviors.same
              //Processing Basic messages
            case BasicMessage(amount, tag, from, messageId) =>
              balance += amount
              context.log.info(s"Process $id: Updated balance $balance due to message from Process ${from.path.name}")
              if (tag && !snapshotTaken) {
                initiateSnapshot()
              }
              inTransitMessages(messageId) = message
              messagePool += message
              //resetting flags when global snapshot is complete
            case CompleteSnapshot =>
              snapshotTaken = false
              inTransitMessages.clear()
              markersReceived.clear()
              preSnapMsgCount = 0
              Behaviors.same
              //Setting neighbors
            case SetNeighbors(newNeighbors) =>
              neighbors = newNeighbors
//            Sending msg to neighbors
            case SendMessagesToNeighbors =>
              neighbors.values.foreach { neighbor =>
                // Ensure not to send a message to itself
                if (neighbor != context.self) {
                  neighbor ! BasicMessage(50, snapshotTaken, context.self, Random.nextInt())
                }
              }
              Behaviors.same
              //Resetting everything for next snap
            case ResetSnapshot =>
              snapshotTaken=false
              context.self ! CompleteSnapshot
            case _=>Behaviors.unhandled
          }
          processMessages()
          Behaviors.same
        }
      }
    }

}