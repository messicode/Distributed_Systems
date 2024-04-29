# Chandy-Lamport Global Snapshot Algorithm

This project implements the Chandy-Lamport algorithm for capturing global state snapshots in a simulated banking distributed system using Akka Typed in Scala. 

## Overview

- The Chandy-Lamport snapshot algorithm is designed so each component (or process) can record its own state independently while capturing in-transit messages and snapshots. 
- Scalatest dependency is used for testing 
- Typesafe configuration dependency is used to manage configuration options in application.conf; 
- Logback classic dependency is used for logging

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

## Platform
Although thorough testing of the project was done on Windows 10, it should run fine on other operating systems as well.

## Project Structure
````plaintext
project-root/
├── build.sbt # Build configuration
├── src/
│ ├── main/
│ │ └── scala/
│ │ ├── Main.scala # Main actor system setup and entry point
│ │ └── Process.scala # Actor definition for handling process logic
│ ├── test/
│ │ └── scala/
│ │ └── ChandyLamportTest.scala # Test suite for the algorithm
````


## Getting Started

### Running the Application locally

1. **Clone this repository**: ```git clone https://github.com/messicode/Distributed_Systems.git```

2. **Navigate to the root directory**:
~~~
cd /path/to/root/folder/ChandyLamport/
~~~
3. **Build the Application**: ``` sbt compile ```
4. **Run the Application**: ```sbt run```
5. **Detailed output**: Output commands (commented) are provided to see other type of messages but by default only snapshot at each process are displayed. 
6. **Logging**: If logs are needed in a .txt file or cmd doesn't display all the output (due to buffer limits) please use : ``` sbt run > file_name.txt ``` which will create a file in root directory.
7. **Input variations**: Different input .dot files are already placed in the root folder which can be run by modifying the name in ```val neighborMap = readNeighborMap("neighbors50.dot")``` line. Just replace the file_name with the input files.

### Testing

**Unit Tests**: Execute the following command to run the predefined test suite: ```sbt test```


## Result

A sample output of the default execution with a distributed system of 50 processes should look like this:
```
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Main$ - ------Process 39 is initiating Global snapshot---------------------------
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 39 - Balance: 2150
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 39: TransactionMessage(50,-1562451470), TransactionMessage(50,1654212901), TransactionMessage(50,1895103418)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 38 - Balance: 2450
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 38: TransactionMessage(50,-1444389191), TransactionMessage(50,-1362190084), TransactionMessage(50,1368131854), TransactionMessage(50,-2135914186), TransactionMessage(50,623333231), TransactionMessage(50,1479909342), TransactionMessage(50,648677383), TransactionMessage(50,-902262890), TransactionMessage(50,1843899704)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 22 - Balance: 2300
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 22: TransactionMessage(50,1940328263), TransactionMessage(50,123241849), TransactionMessage(50,-57196253), TransactionMessage(50,1518822530), TransactionMessage(50,2078379529), TransactionMessage(50,-221506544)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 42 - Balance: 2300
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - Snapshot at Process 35 - Balance: 2600
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 42: TransactionMessage(50,-352420738), TransactionMessage(50,-1747489018), TransactionMessage(50,-847445688), TransactionMessage(50,-954398197), TransactionMessage(50,1945782129), TransactionMessage(50,-1296556314)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - In-transit messages at Process 35: TransactionMessage(50,-229003877), TransactionMessage(50,-984760213), TransactionMessage(50,-1216465610), TransactionMessage(50,-2096787924), TransactionMessage(50,-1417488311), TransactionMessage(50,-303609015), TransactionMessage(50,-1706999215), TransactionMessage(50,478360147), TransactionMessage(50,84762776), TransactionMessage(50,153968859), TransactionMessage(50,-1710485648), TransactionMessage(50,-27302271)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 41 - Balance: 2300
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-11] INFO  Process$ - Snapshot at Process 52 - Balance: 2150
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - Snapshot at Process 44 - Balance: 2300
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - In-transit messages at Process 44: TransactionMessage(50,-304769340), TransactionMessage(50,-274563277), TransactionMessage(50,1052631436), TransactionMessage(50,-2055736719), TransactionMessage(50,-1480823324), TransactionMessage(50,1928415850)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-9] INFO  Process$ - Snapshot at Process 15 - Balance: 2300
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 41: TransactionMessage(50,96113819), TransactionMessage(50,-1965919200), TransactionMessage(50,-881067410), TransactionMessage(50,-704732187), TransactionMessage(50,-472751879), TransactionMessage(50,-1239442687)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-11] INFO  Process$ - In-transit messages at Process 52: TransactionMessage(50,-397034531), TransactionMessage(50,2074564543), TransactionMessage(50,622181627)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - Snapshot at Process 47 - Balance: 2450
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - In-transit messages at Process 47: TransactionMessage(50,-1038626269), TransactionMessage(50,-893769810), TransactionMessage(50,-578483277), TransactionMessage(50,-892081925), TransactionMessage(50,-1132352312), TransactionMessage(50,-1661127481), TransactionMessage(50,-896443652), TransactionMessage(50,-2138487946), TransactionMessage(50,-918395875)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - Snapshot at Process 27 - Balance: 2300
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-9] INFO  Process$ - In-transit messages at Process 15: TransactionMessage(50,1203586889), TransactionMessage(50,-1552423262), TransactionMessage(50,-1989407222), TransactionMessage(50,-2069197645), TransactionMessage(50,-310912450), TransactionMessage(50,1916813790)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 11 - Balance: 2300
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 11: TransactionMessage(50,-2140103596), TransactionMessage(50,956418101), TransactionMessage(50,-1114232621), TransactionMessage(50,2114440172), TransactionMessage(50,-2084158678), TransactionMessage(50,-1802281346)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - In-transit messages at Process 27: TransactionMessage(50,1408667221), TransactionMessage(50,-1592802475), TransactionMessage(50,85201534), TransactionMessage(50,670136827), TransactionMessage(50,1934001740), TransactionMessage(50,-168625107)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-5] INFO  Process$ - Snapshot at Process 53 - Balance: 2150
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - Snapshot at Process 34 - Balance: 2150
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-9] INFO  Process$ - Snapshot at Process 17 - Balance: 2450
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-9] INFO  Process$ - In-transit messages at Process 17: TransactionMessage(50,-520875675), TransactionMessage(50,5576966), TransactionMessage(50,-1240245337), TransactionMessage(50,-458563476), TransactionMessage(50,163792171), TransactionMessage(50,-1841847778), TransactionMessage(50,-1726629218), TransactionMessage(50,2137477018), TransactionMessage(50,2139727748)
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 6 - Balance: 2450
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-5] INFO  Process$ - In-transit messages at Process 53: TransactionMessage(50,-1288041186), TransactionMessage(50,-372958048), TransactionMessage(50,-120609192)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-15] INFO  Process$ - Snapshot at Process 2 - Balance: 2150
04:08:41.351 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - In-transit messages at Process 34: TransactionMessage(50,-742640754), TransactionMessage(50,988898264), TransactionMessage(50,1367182900)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-11] INFO  Process$ - Snapshot at Process 43 - Balance: 2150
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-11] INFO  Process$ - In-transit messages at Process 43: TransactionMessage(50,-1475918073), TransactionMessage(50,212538067), TransactionMessage(50,518086905)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 6: TransactionMessage(50,728371802), TransactionMessage(50,-133106799), TransactionMessage(50,1133628674), TransactionMessage(50,796260016), TransactionMessage(50,-1627534470), TransactionMessage(50,1287721675), TransactionMessage(50,-2080648521), TransactionMessage(50,1264348368), TransactionMessage(50,-1147584874)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-15] INFO  Process$ - In-transit messages at Process 2: TransactionMessage(50,2110446640), TransactionMessage(50,-1678360022), TransactionMessage(50,-1596342972)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-10] INFO  Process$ - Snapshot at Process 51 - Balance: 2150
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-8] INFO  Process$ - Snapshot at Process 13 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-8] INFO  Process$ - In-transit messages at Process 13: TransactionMessage(50,-1843371659), TransactionMessage(50,-73848977), TransactionMessage(50,1227108483), TransactionMessage(50,759265995), TransactionMessage(50,-2004765299), TransactionMessage(50,-1299611452)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-10] INFO  Process$ - In-transit messages at Process 51: TransactionMessage(50,1875923442), TransactionMessage(50,-988659628), TransactionMessage(50,-1013810210)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-8] INFO  Process$ - Snapshot at Process 36 - Balance: 2450
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-8] INFO  Process$ - In-transit messages at Process 36: TransactionMessage(50,-1767502137), TransactionMessage(50,-79415194), TransactionMessage(50,450067888), TransactionMessage(50,1131850597), TransactionMessage(50,-543062631), TransactionMessage(50,1529024265), TransactionMessage(50,249687981), TransactionMessage(50,1246470885), TransactionMessage(50,2022475555)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-8] INFO  Process$ - Snapshot at Process 49 - Balance: 2450
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-8] INFO  Process$ - In-transit messages at Process 49: TransactionMessage(50,-1706807740), TransactionMessage(50,2095404501), TransactionMessage(50,-767476416), TransactionMessage(50,1262020185), TransactionMessage(50,-1787919492), TransactionMessage(50,-1703775119), TransactionMessage(50,1725987861), TransactionMessage(50,1331459307), TransactionMessage(50,-588838)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-8] INFO  Process$ - Snapshot at Process 50 - Balance: 2150
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-12] INFO  Process$ - Snapshot at Process 16 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-12] INFO  Process$ - In-transit messages at Process 16: TransactionMessage(50,-2025384473), TransactionMessage(50,294839379), TransactionMessage(50,512324241), TransactionMessage(50,635242580), TransactionMessage(50,1765023821), TransactionMessage(50,-1154645242)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-12] INFO  Process$ - Snapshot at Process 31 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-8] INFO  Process$ - In-transit messages at Process 50: TransactionMessage(50,-162468928), TransactionMessage(50,2098351718), TransactionMessage(50,-1102265894)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 29 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-12] INFO  Process$ - In-transit messages at Process 31: TransactionMessage(50,-677004874), TransactionMessage(50,1602657221), TransactionMessage(50,-1380723178), TransactionMessage(50,476165448), TransactionMessage(50,378037794), TransactionMessage(50,1580795781)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-12] INFO  Process$ - Snapshot at Process 23 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-12] INFO  Process$ - In-transit messages at Process 23: TransactionMessage(50,-772069462), TransactionMessage(50,-256074023), TransactionMessage(50,578591690), TransactionMessage(50,-548886160), TransactionMessage(50,-751549490), TransactionMessage(50,1836332410)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-12] INFO  Process$ - Snapshot at Process 3 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-12] INFO  Process$ - In-transit messages at Process 3: TransactionMessage(50,-1084112795), TransactionMessage(50,-1411026864), TransactionMessage(50,-1045054707), TransactionMessage(50,-534908752), TransactionMessage(50,597973113), TransactionMessage(50,-2006955218)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-12] INFO  Process$ - Snapshot at Process 26 - Balance: 2150
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 29: TransactionMessage(50,-1654685906), TransactionMessage(50,43081863), TransactionMessage(50,1213775566), TransactionMessage(50,-1630361062), TransactionMessage(50,1065608428), TransactionMessage(50,1447135310)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-12] INFO  Process$ - In-transit messages at Process 26: TransactionMessage(50,235398854), TransactionMessage(50,-496119729), TransactionMessage(50,551858912)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 18 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 18: TransactionMessage(50,-1504707267), TransactionMessage(50,2118659194), TransactionMessage(50,1501746119), TransactionMessage(50,827093674), TransactionMessage(50,747908925), TransactionMessage(50,-745116600)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 10 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 10: TransactionMessage(50,-2120055311), TransactionMessage(50,-1681585555), TransactionMessage(50,1889385541), TransactionMessage(50,1320006967), TransactionMessage(50,57732349), TransactionMessage(50,-371677754)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 46 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 46: TransactionMessage(50,47777609), TransactionMessage(50,-460374290), TransactionMessage(50,351616416), TransactionMessage(50,-2134897785), TransactionMessage(50,-1558431962), TransactionMessage(50,-1454644844)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 5 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 5: TransactionMessage(50,-1539111245), TransactionMessage(50,-1447947834), TransactionMessage(50,-718392198), TransactionMessage(50,1547775798), TransactionMessage(50,559341420), TransactionMessage(50,1832810148)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 4 - Balance: 2300
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 4: TransactionMessage(50,1642001870), TransactionMessage(50,1879690029), TransactionMessage(50,2058136484), TransactionMessage(50,-1243963293), TransactionMessage(50,-905251933), TransactionMessage(50,431566788)
04:08:41.354 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 32 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 32: TransactionMessage(50,-595124699), TransactionMessage(50,-1259693671), TransactionMessage(50,1094736916), TransactionMessage(50,-1376086542), TransactionMessage(50,1175352770), TransactionMessage(50,-945991854)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 8 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 8: TransactionMessage(50,525687685), TransactionMessage(50,-607444582), TransactionMessage(50,2028832284), TransactionMessage(50,1441767229), TransactionMessage(50,-1093428480), TransactionMessage(50,1127208924)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 19 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 19: TransactionMessage(50,-2007674828), TransactionMessage(50,953213345), TransactionMessage(50,-20707034), TransactionMessage(50,-1908764562), TransactionMessage(50,-170180670), TransactionMessage(50,-313993691)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 40 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 40: TransactionMessage(50,-265106009), TransactionMessage(50,358279775), TransactionMessage(50,133331861), TransactionMessage(50,-1225202304), TransactionMessage(50,1725454624), TransactionMessage(50,-249671773)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 30 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 30: TransactionMessage(50,1857373381), TransactionMessage(50,1189854489), TransactionMessage(50,1850081860), TransactionMessage(50,-509704311), TransactionMessage(50,38599047), TransactionMessage(50,1351656655)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 48 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 48: TransactionMessage(50,-1966771049), TransactionMessage(50,-1348663537), TransactionMessage(50,-597859105), TransactionMessage(50,1961172439), TransactionMessage(50,-495631009), TransactionMessage(50,-960656890)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 12 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 12: TransactionMessage(50,1939788524), TransactionMessage(50,406353917), TransactionMessage(50,-1062625593), TransactionMessage(50,1895648579), TransactionMessage(50,-1959260937), TransactionMessage(50,-1631734510)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 45 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 45: TransactionMessage(50,757766425), TransactionMessage(50,1108694806), TransactionMessage(50,152710), TransactionMessage(50,-2144763037), TransactionMessage(50,-1115836032), TransactionMessage(50,433391048)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 0 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 0: TransactionMessage(50,998720933), TransactionMessage(50,1153228655), TransactionMessage(50,-685435119), TransactionMessage(50,436347733), TransactionMessage(50,1030458275), TransactionMessage(50,1350973723)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - Snapshot at Process 21 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-13] INFO  Process$ - In-transit messages at Process 21: TransactionMessage(50,-1991666343), TransactionMessage(50,-847374006), TransactionMessage(50,212331219), TransactionMessage(50,878089072), TransactionMessage(50,-1503644262), TransactionMessage(50,1240104613)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - Snapshot at Process 20 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - In-transit messages at Process 20: TransactionMessage(50,1303065507), TransactionMessage(50,1757257669), TransactionMessage(50,-967078592), TransactionMessage(50,-1398136585), TransactionMessage(50,-1876082078), TransactionMessage(50,926089085)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - Snapshot at Process 37 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-14] INFO  Process$ - In-transit messages at Process 37: TransactionMessage(50,437176158), TransactionMessage(50,828180447), TransactionMessage(50,-1590158948), TransactionMessage(50,-1499096708), TransactionMessage(50,196521740), TransactionMessage(50,887461870)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-15] INFO  Process$ - Snapshot at Process 7 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-15] INFO  Process$ - In-transit messages at Process 7: TransactionMessage(50,-1958366860), TransactionMessage(50,-1792299175), TransactionMessage(50,309038494), TransactionMessage(50,830575097), TransactionMessage(50,624541447), TransactionMessage(50,-128207468)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-15] INFO  Process$ - Snapshot at Process 1 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-15] INFO  Process$ - In-transit messages at Process 1: TransactionMessage(50,1132425933), TransactionMessage(50,-1172315188), TransactionMessage(50,-422599221), TransactionMessage(50,-766061040), TransactionMessage(50,-1207374752), TransactionMessage(50,1913944697)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-15] INFO  Process$ - Snapshot at Process 14 - Balance: 2300
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-15] INFO  Process$ - In-transit messages at Process 14: TransactionMessage(50,628959033), TransactionMessage(50,154873086), TransactionMessage(50,871519111), TransactionMessage(50,950657564), TransactionMessage(50,-1858931476), TransactionMessage(50,727521250)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-15] INFO  Process$ - Snapshot at Process 25 - Balance: 2150
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-15] INFO  Process$ - In-transit messages at Process 25: TransactionMessage(50,419426261), TransactionMessage(50,523830327), TransactionMessage(50,1260120052)
04:08:41.355 [MainSystem-akka.actor.default-dispatcher-15] INFO  Main$ - All processes have completed their snapshot, ready for next snapshot.

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

