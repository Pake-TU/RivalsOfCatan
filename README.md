# RivalsOfCatan
This is for a school assignment - a digital implementation of the Rivals of Catan card game.

## Building the Project

This project uses Maven for dependency management and building. Make sure you have Maven installed (version 3.6+ recommended).

### Compile the project
```bash
mvn clean compile
```

### Package as JAR
```bash
mvn clean package
```

This will create two JAR files in the `target/` directory:
- `rivals-of-catan-1.0-SNAPSHOT.jar` - Basic JAR
- `rivals-of-catan-1.0-SNAPSHOT-with-dependencies.jar` - Fat JAR with all dependencies included

## Running the Game

### Local game (console)
```bash
java -jar target/rivals-of-catan-1.0-SNAPSHOT-with-dependencies.jar
```

### With bot opponent
```bash
java -jar target/rivals-of-catan-1.0-SNAPSHOT-with-dependencies.jar bot
```

### Online multiplayer (server)
```bash
java -jar target/rivals-of-catan-1.0-SNAPSHOT-with-dependencies.jar
```

### Online multiplayer (client)
```bash
java -jar target/rivals-of-catan-1.0-SNAPSHOT-with-dependencies.jar online
```

## Current Project Structure

The project follows Maven standard directory layout:

```
rivals-of-catan/
├── pom.xml                             // Maven configuration
├── src/
│   ├── main/
│   │   ├── java/                       // Java source files
│   │   │   ├── Card.java               // Card model and effects
│   │   │   ├── Player.java             // Player implementation
│   │   │   ├── OnlinePlayer.java       // Network player implementation
│   │   │   ├── Server.java             // Game server and logic
│   │   │   ├── IPlayer.java            // Player interface
│   │   │   ├── IPlayerIO.java          // I/O abstraction interface
│   │   │   ├── IResourceManager.java   // Resource management interface
│   │   │   └── ICardEffect.java        // Card effect interface
│   │   └── resources/
│   │       └── cards.json              // Card definitions
│   └── test/
│       └── java/                       // Test files (to be added)
└── target/                             // Build output (generated)
```

## Planned Future Structure

This structure was discussed with a classmate, Simon Pergel, as a target for future refactoring:

```
src/
├── model/                              // Handles game domain and its entities, players hand etc
│   ├── Card.java
│   ├── Deck.java
│   ├── Player.java
│   ├── Principality.java
│   ├── Resource.java
│   ├── EventCard.java
│   ├── BasicCard.java
│   ├── CenterCard.java
│   └── VictoryPointTracker.java
│
├── controller/                         // Handles the turns, events, validating rules, contains interfaces
│   ├── GameController.java
│   ├── TurnManager.java
│   ├── DeckManager.java
│   ├── EventManager.java
│   ├── CardFactory.java
│   ├── RuleValidator.java
│   ├── interfaces/
│   │   ├── IGameController.java
│   │   ├── IDeckProvider.java
│   │   ├── IEventHandler.java
│   │   └── IRuleValidator.java
│   │
│   └── actions/
│       ├── CardEffect.java             // Interface for all effects
|       ├── BrigandEffect.java          // Implements Brigand attack behavior
|       ├── TradeEffect.java            // Implements trade behavior
|       ├── CelebrationEffect.java      // Implements celebration behavior
|       ├── HarvestEffect.java          // Implements harvest behavior
|       └── EventCardEffect.java        // Base for event-card-driven effects
│
├── network/                            // Handles creating a server, connecting to a server etc
│   ├── Server.java                     // Logic for creating the server, listening to a port and inject connected 
|   |                                   // players to the GameController
│   ├── ServerHandler.java              // Manages connected players, forward messages handles disconnects etc.
│   ├── ClientConnection.java           // Handles the connection between server and player
│   ├── OnlinePlayer.java               // Player logic of an online player, converts CLI -> network msg -> gameplay action
│   ├── NetworkService.java             
│   └── interfaces/
│       ├── INetworkHandler.java
│       ├── IConnection.java
│       └── IMessageProtocol.java
│
├── io/
│   ├── interfaces/
│   │   ├── IPlayerIO.java              // Generic input/output abstraction
│   │   ├── IInputService.java
│   │   └── IOutputService.java
│   │
│   ├── ConsoleInput.java
│   ├── ConsoleOutput.java
│   ├── SocketInput.java
│   ├── SocketOutput.java
│   └── MockIO.java                     // For JUnit testing
│
├── util/
│   ├── Dice.java
│   ├── Logger.java
│   ├── Randomizer.java
│   └── GameConfig.java
|
└── tests/
    └── To be added
```

## Recent Improvements

- **Input Validation**: Resource selection now validates input and re-prompts on invalid entries (fixes Merchant Caravan issue)
- **Maven Build System**: Project now uses Maven for dependency management and building
- **Interfaces**: Added interfaces (IPlayer, IPlayerIO, IResourceManager, ICardEffect) for better modularity and extensibility
- **Resource Loading**: Cards are now loaded from classpath, supporting both JAR execution and development