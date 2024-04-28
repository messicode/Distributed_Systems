import akka.actor.typed.ActorSystem
import akka.actor.typed.ActorRef

import scala.concurrent.{Await, ExecutionContext}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._
import akka.actor.typed.Behavior

import scala.collection.mutable
import scala.io.Source
import scala.util.matching.Regex
import scala.util.{Failure, Random, Success, Try}

sealed trait ChandyLamportMessage
case object InitiateGlobalSnapshot extends ChandyLamportMessage
case class SetNeighbors(neighbors: Map[Int, ActorRef[ChandyLamportMessage]]) extends ChandyLamportMessage
case class Marker(from: Int) extends ChandyLamportMessage
case class TransactionMessage(amount: Int,messageId: Int) extends ChandyLamportMessage
case object RequestSnapshotStart extends ChandyLamportMessage
case class SnapshotStatus(processId: Int, completed: Boolean) extends ChandyLamportMessage
case object ResetSnapshot extends ChandyLamportMessage
case object SendMessagesToNeighbors extends ChandyLamportMessage

object Main {
  def main(args: Array[String]): Unit = {
    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
    //Starting actor system
    val system = ActorSystem(MainBehavior(), "MainSystem")
    system ! InitiateGlobalSnapshot
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
  private def MainBehavior()(implicit ec: ExecutionContext): Behavior[ChandyLamportMessage] = Behaviors.setup { context =>
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

  //checking if snapshot is incomplete
    var snapshotInProgress = false


    Behaviors.receiveMessage {
      case InitiateGlobalSnapshot =>
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
          context.self ! InitiateGlobalSnapshot

        }
        Behaviors.same

      case _ => Behaviors.unhandled
    }
  }

}
