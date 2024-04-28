# Chandy-Lamport Global Snapshot Algorithm

This project implements the Chandy-Lamport algorithm for capturing global state snapshots in a simulated banking distributed system using Akka Typed in Scala. 

## Overview

The Chandy-Lamport snapshot algorithm is designed so each component (or process) can record its own state independently while capturing in-transit messages and snapshots. 

## Features

- **Messages**: Each actor independently sends update messages to its neighbors every 3 seconds.
- **Snapshot Initiation**: Any process can initiate a snapshot globally.
- **FIFO processing**: Messages are processed by default in FIFO order.
- **Markers**: Markers are sent to neighbors by process after taking its snapshot.
- **In-Transit Message Tracking**: Captures messages that are in transit at the moment the snapshot is initiated till all markers are received from neighbors.
- **Dynamic Neighbor Setup**: Processes can dynamically set their neighbors based on external configuration.
- **Network**: The network is created to be undirected and not fully disconnected ie theres a path from every node to any other node.
- **Main**: Entry point, snapshot initiator and flag re-setter.

## Requirements

- Scala 2.13 or higher
- Akka Typed 2.6.14
- SBT (Scala Build Tool)

## Project Structure

project-root/
├── build.sbt # Build configuration
├── src/
│ ├── main/
│ │ └── scala/
│ │ ├── Main.scala # Main actor system setup and entry point
│ │ └── Process.scala # Actor definition for handling process logic
│ ├── test/
│ │ └── scala/
│ │ └── ChandyLamportSpec.scala # Test suite for the algorithm

## Running

- Clone this project 
- Open command line and go to the root directory.
- Type sbt run to see only snapshots
- Output commands (commented) are provided to see other type of messages but by default only snapshot at each process are displayed.
- If output needed in a txt file or cmd doesnt display all the output due to buffer limits please use : ' sbt run > output.txt ' which will create a file in root directory.


## NOTE

- This project was successfully run on windows 10 using command line
- Every attempt is made to keep this system decentralized
- 4 different input .dot files are already placed in the root folder which can be run by modifying the name in val neighborMap = readNeighborMap("neighbors50.dot") line.