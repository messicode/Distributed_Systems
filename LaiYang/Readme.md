# Lai-Yang Global Snapshot Algorithm

This project implements the Lai-Yang algorithm for capturing global state snapshots in a simulated banking distributed system using Akka Typed in Scala. 

## Overview

- The Lai-Yang snapshot algorithm is designed so each component (or process) can record its own state independently while capturing in-transit messages and snapshots. 
- It uses message piggybacking and Control messages to implement this.
- Scalatest dependency is used for testing
- Typesafe configuration dependency is used to manage configuration options in application.conf;
- Logback classic dependency is used for logging

## Features

- **Messages**: Each actor independently sends update messages to its neighbors every 3 seconds that have piggybacked snapshot flags.
- **Snapshot Initiation**: Any process can initiate a snapshot globally.
- **non-FIFO processing**: Basic messages are processed in non-FIFO order by picking messages randomly out of a list buffer.
- **Control messages**: Control messages are sent to neighbors by process after taking its snapshot.
- **In-Transit Message Tracking**: Captures messages that are in-transit at the moment the snapshot is initiated.
- **Dynamic Neighbor Setup**: Processes can dynamically set their neighbors based on external configuration.
- **Network**: The network is created to be undirected and not fully disconnected ie theres a path from every node to any other node.
- **Main**: Entry point, snapshot initiator and flag re-setter.
- 
## Requirements

- Scala 2.13 or higher
- Akka Typed 2.6.14
- SBT (Scala Build Tool)

## Project Structure

```plaintext
project-root/
├── build.sbt # Build configuration
├── src/
│ ├── main/
│ │ └── scala/
│ │ ├── Main.scala # Main actor system setup and entry point
│ │ └── Process.scala # Actor definition for handling process logic
│ ├── test/
│ │ └── scala/
│ │ └── LaiYangTest.scala # Test suite for the algorithm
```
## Getting Started

### Running the Application locally

1. **Clone this repository**: ```git clone https://github.com/messicode/Distributed_Systems.git```

2. **Navigate to the root directory**:
~~~
cd /path/to/root/folder/LaiYang/
~~~
3. **Build the Application**: ``` sbt compile ```
4. **Run the Application**: ```sbt run```
5. **Detailed output**: Output commands (commented) are provided to see other type of messages but by default only snapshot at each process are displayed.
6. **Logging**: If logs are needed in a .txt file or cmd doesn't display all the output (due to buffer limits) please use : ``` sbt run > file_name.txt ``` which will create a file in root directory.
7. **Input variations**: Different input .dot files are already placed in the root folder which can be run by modifying the name in ```val neighborMap = readNeighborMap("neighbors50.dot")``` line. Just replace the file_name with the input files.

## Result

A sample output of the default execution with a distributed system of 50 processes should look like this:
```
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-15] INFO Main$ -- ------Process 0 is initiating Global snapshot---------------------------
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-15] INFO Process$ -- Snapshot at Process 0 - Balance: 1400
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-15] INFO Process$ -- In-transit messages at Process 0: BasicMessage(50,false,Actor[akka://MainSystem/user/Process0#-1189977513],1611919085)
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-15] INFO Process$ -- Snapshot at Process 1 - Balance: 1350
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-15] INFO Process$ -- In-transit messages at Process 1: BasicMessage(50,false,Actor[akka://MainSystem/user/Process1#1374816528],-147024887)
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-9] INFO Process$ -- Snapshot at Process 2 - Balance: 1300
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-9] INFO Process$ -- In-transit messages at Process 2: BasicMessage(50,false,Actor[akka://MainSystem/user/Process2#334153570],522518737)
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-9] INFO Process$ -- Snapshot at Process 5 - Balance: 1300
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-9] INFO Process$ -- In-transit messages at Process 5: BasicMessage(50,false,Actor[akka://MainSystem/user/Process5#597214855],2072585345)
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-9] INFO Process$ -- Snapshot at Process 3 - Balance: 1350
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-9] INFO Process$ -- In-transit messages at Process 3: BasicMessage(50,false,Actor[akka://MainSystem/user/Process3#1532376064],-1795248733)
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-9] INFO Process$ -- Snapshot at Process 4 - Balance: 1300
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-9] INFO Process$ -- In-transit messages at Process 4: BasicMessage(50,false,Actor[akka://MainSystem/user/Process4#-816355357],1641078167)
21:50:29.977 [MainSystem-akka.actor.default-dispatcher-9] INFO Main$ -- All processes have completed their snapshot, ready for next snapshot.
```


## Contributing
Contributions are welcome! Please fork the repository and submit pull requests with any enhancements. Ensure to add unit tests for new features.

## License

This project is licensed under the [MIT License](https://github.com/messicode/Distributed_Systems/blob/master/LICENSE.txt). Feel free to use, modify, and distribute it as per the license terms.

## References

- 'Distributed Algorithms-An Intuitive Approach (2nd edition) by Wan Fokkink' was heavily referred to implement this algorithm.
- [NetGameSim](https://github.com/0x1DOCD00D/NetGameSim) graph simulator by Mark Grechanik was used to generate the input .dot files.
- These simulations [Ref 1](https://github.com/sarangsawant/BankingApplication-Chandy-Lamport-Snapshot) and [Ref2](https://github.com/nrasadi/global-state-snapshot) were referred to understand the actual working of the algorithm in a distributed setting.

## NOTE

- This project was successfully run on Windows 10 using command line
- Every attempt is made to keep this system decentralized