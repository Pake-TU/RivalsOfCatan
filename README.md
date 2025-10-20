# RivalsOfCatan
This is for a school assignment

## Building and Running

This project uses Maven for dependency management and building.

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Building the Project
```bash
# Compile the project
mvn clean compile

# Package into a JAR file
mvn package

# Run tests (when available)
mvn test
```

### Running the Game
```bash
# Run with bot player (default)
java -cp target/classes:target/dependency/* Server bot

# Run with online multiplayer (2 players)
# Terminal 1 (server):
java -cp target/classes:target/dependency/* Server

# Terminal 2 (client):
java -cp target/classes:target/dependency/* Server online

# Or use the fat JAR:
java -jar target/rivals-of-catan-1.0.0-SNAPSHOT-with-dependencies.jar bot
```

## Folder Structure

The project follows Maven standard directory layout with a well-organized package structure:

```
src/main/java/
├── model/                              // Game domain and entities
│   ├── Card.java                       // Card data and effects
│   ├── Player.java                     // Player state and resource management
│   ├── ResourceType.java               // Resource type constants and mappings
│   ├── EventType.java                  // Event die face constants
│   └── interfaces/
│       ├── IPlayer.java                // Player abstraction for different types
│       └── ICardEffect.java            // Interface for card effects
│
├── controller/                         // Game logic managers
│   ├── ProductionManager.java          // Handles production phase
│   ├── ReplenishManager.java           // Manages hand replenishment
│   ├── ExchangeManager.java            // Handles card exchange phase
│   ├── InitializationManager.java      // Sets up initial game state
│   └── interfaces/
│       └── IGameManager.java           // Base interface for all managers
│
├── network/                            // Networking and multiplayer
│   └── OnlinePlayer.java               // Network-enabled player
│
├── util/                               // Reusable utilities
│   ├── DiceRoller.java                 // Dice rolling logic
│   └── CostParser.java                 // Cost parsing utilities
│
└── Server.java                         // Main game coordinator

src/main/resources/
└── cards.json                          // Card definitions

src/test/java/
└── (Test files to be added)
```

## Architecture

The project follows **SOLID principles** and **MVC architecture**:

- **Model**: Game entities (Card, Player, ResourceType, EventType)
- **Controller**: Game phase managers implementing specific logic
- **View**: Console-based I/O through Player classes
- **Interfaces**: Clear contracts for extensibility and testability

### Key Interfaces

- **IPlayer**: Abstraction for different player types (local, online, bot)
- **IGameManager**: Base for all game phase managers
- **ICardEffect**: Extensible card behavior system

### Design Patterns

- **Strategy Pattern**: Different player types implementing IPlayer
- **Manager Pattern**: Separate managers for each game phase
- **Factory Pattern**: Card creation and initialization

## Features

### Resource Validation
- All resource inputs are validated with retry on invalid entry
- Players must enter full resource names (Brick, Grain, Lumber, Wool, Ore, Gold)
- No more silent ignoring of invalid inputs

### Extensibility
- Interfaces allow for easy addition of new player types
- New game phases can be added as new managers
- Card effects can be extended through ICardEffect interface
- Prepared for future eras and expansions

### Build System
- Maven handles all dependencies
- Single command to build and package
- Fat JAR includes all dependencies for easy distribution