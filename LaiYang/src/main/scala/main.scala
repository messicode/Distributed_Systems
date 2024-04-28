import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import scala.io.Source
import scala.util.matching.Regex

// Define messages
sealed trait Message
case class BasicMessage(amount: Int, tag: Boolean,from: ActorRef[Message],messageId: Int) extends Message
case class ControlMessage(senderId: Int, count: Int, snapshot: Boolean) extends Message
case object InitiateSnapshot extends Message
case class SetNeighbors(neighbors: Map[Int, ActorRef[Message]]) extends Message
case class UpdateBalance(amount: Int) extends Message
case object ResetSnapshot extends Message
case class AcknowledgeMessage(id: Int) extends Message
case class SnapshotCompletion(processId: Int, allComplete: Boolean) extends Message
case object RequestSnapshotStart extends Message
case object GrantSnapshotStart extends Message
case object CompleteSnapshot extends Message
case class StartSnapshot(mainActor: ActorRef[Message]) extends Message
case class SnapshotStatus(processId: Int, completed: Boolean) extends Message
case object SendMessagesToNeighbors extends Message
// Define actor behavior
object Main {
  def main(args: Array[String]): Unit = {
    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
    //Starting actor system
    val system = ActorSystem(MainBehavior(), "MainSystem")
    system ! InitiateSnapshot
    Thread.sleep(10000) // Let the system run for a while
    system.terminate()
  }


  //Read input graph from a .dot file placed in the root folder
  def readNeighborMap(filename: String): Map[Int, List[Int]] = {
    val edgePattern: Regex = """"(\d+)" -> "(\d+)" \["weight"=".*"\]""".r

    Try(Source.fromFile(filename, "UTF-8")) match {
      case Success(source) =>
        try {
          val edges = source.getLines().map {
            case edgePattern(from, to) =>
              //println(s"Match found: from $from to $to")
              Some((from.toInt, to.toInt))
            case line =>
              println(s"No match for line: '$line'")
              None
          }.toList.flatten

          val undirectedEdges = edges ++ edges.map { case (a, b) => (b, a) }
          undirectedEdges.groupBy(_._1).view.mapValues(_.map(_._2).distinct).toMap
        } finally {
          source.close()
        }
      case Failure(exception) =>
        println(s"Failed to read from the file: $filename due to ${exception.getMessage}")
        Map.empty
    }
  }

  //Main behavior of the actor system
  private def MainBehavior()(implicit ec: ExecutionContext): Behavior[Message] = Behaviors.setup { context =>
    val neighborMap = readNeighborMap("neighbors50.dot")
    //Spawning process actors
    val processes = neighborMap.keys.map { id =>
      id -> context.spawn(Process(id, Map.empty,context.self), s"Process$id")
    }.toMap
    //To track processes taking snapshots
    val snapshotMap = mutable.Map[Int, Boolean]()
    //Setting neighbors
    processes.foreach {
      case (id, processActor) =>
      val neighbors = neighborMap.getOrElse(id, List.empty).map(nId => nId -> processes(nId)).toMap
//        context.log.info(s"$id with neigh: $neighbors")
      processActor ! SetNeighbors(neighbors)
    }


//    val processes = (1 to 3).map { id =>
//      context.spawn(Process(id, Map.empty), s"Process$id")
//    }

//
//    processes.foreach { process =>
//      val neighbors = processes.filterNot(_ == process).map(proc => proc.path.name.filter(_.isDigit).toInt -> proc).toMap
//      process ! SetNeighbors(neighbors)
//    }

//    val processRefs = processes.map(process => process.path.name.replaceAll("\\D", "").toInt -> process).toMap
//
//    val neighborMap: Map[Int, List[Int]] = Map(
//      1 -> List(2),   // Process 1 is neighbors with Process 2
//      2 -> List(1, 3),// Process 2 is neighbors with Process 1 and 3
//      3 -> List(2)    // Process 3 is neighbors with Process 2
//    )

    // Assign neighbors based on predefined map
//    processes.foreach { process =>
//      val id = process.path.name.replaceAll("\\D", "").toInt
//      val neighbors = neighborMap.getOrElse(id, List.empty).map(nId => nId -> processRefs(nId)).toMap
//      process ! SetNeighbors(neighbors)
//    }
    //checking if snapshot is incomplete
    var snapshotInProgress = false


    Behaviors.receiveMessage {
      case InitiateSnapshot =>
        if (!snapshotInProgress) {
          snapshotInProgress = true
          //picking random process to initiate snapshot
          val snapshotInitiator = scala.util.Random.shuffle(processes.values.toList).head
          val initiatorId = snapshotInitiator.path.name.replaceAll("\\D", "")
          context.log.info(s"------Process $initiatorId is initiating Global snapshot---------------------------")

          snapshotInitiator ! RequestSnapshotStart
        }
        Behaviors.same
      case SnapshotStatus(processId, completed) =>
        snapshotMap(processId)= completed
        // Check if all processes have completed their snapshot
        if (snapshotMap.size == processes.size && snapshotMap.values.forall(_ == true)) {
          processes.values.foreach(_ ! ResetSnapshot)
          context.log.info("All processes have completed their snapshot, ready for next snapshot.")
          //Reset snapshot tracking for next snapshot
          snapshotMap.clear()
          snapshotInProgress = false
          //Start with the next snapshot
          //A hold (sleep) can be placed here to have significant observable changes in the system
          context.self ! InitiateSnapshot

        }
        Behaviors.same
      case CompleteSnapshot =>
        snapshotInProgress = false
        context.log.info("Global snapshot completed, ready for next snapshot.")
        Behaviors.same

      case _ => Behaviors.unhandled
    }
  }

}
