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
java -cp target/classes:target/dependency/* Main bot

# Run with online multiplayer (2 players)
# Terminal 1 (server):
java -cp target/classes:target/dependency/* Main

# Terminal 2 (client):
java -cp target/classes:target/dependency/* Main online

# Or use the fat JAR:
java -jar target/rivals-of-catan-1.0.0-SNAPSHOT-with-dependencies.jar bot
```

## Folder Structure

The project follows Maven standard directory layout with a well-organized package structure:

```
src/main/java/
├── view/                                // View layer (MVC)
│   ├── IPlayerView.java                 // View interface for player I/O
│   ├── ConsolePlayerView.java           // Console-based view implementation
│   ├── BotPlayerView.java               // Bot player view (no I/O)
│   └── NetworkPlayerView.java           // Network-based view implementation
│
├── model/                               // Model layer - Game domain and entities
│   ├── Card.java                        // Card data and effects
│   ├── Player.java                      // Player state and resource management
│   ├── ResourceType.java                // Resource type constants and mappings
│   ├── EventType.java                   // Event die face constants
│   ├── CardDeckManager.java             // Card deck management
│   ├── effects/                         // Card effect handlers
│   │   ├── ActionCardEffectHandler.java
│   │   ├── CenterCardEffectHandler.java
│   │   ├── ExpansionCardEffectHandler.java
│   │   └── RegionPlacementHelper.java
│   └── interfaces/
│       ├── IPlayer.java                 // Player abstraction for different types
│       └── ICardEffect.java             // Interface for card effects
│
├── controller/                          // Controller layer - Game logic managers
│   ├── GameController.java              // Main game loop controller
│   ├── ProductionManager.java           // Handles production phase
│   ├── ReplenishManager.java            // Manages hand replenishment
│   ├── ExchangeManager.java             // Handles card exchange phase
│   ├── InitializationManager.java       // Sets up initial game state
│   ├── ActionManager.java               // Manages action phase
│   ├── EventResolver.java               // Resolves event die outcomes
│   ├── events/                          // Event card implementations
│   └── interfaces/
│       └── IGameManager.java            // Base interface for all managers
│
├── network/                             // Networking and multiplayer
│   └── OnlinePlayer.java                // Network-enabled player
│
├── util/                                // Reusable utilities
│   ├── DiceRoller.java                  // Dice rolling logic
│   ├── CostParser.java                  // Cost parsing utilities
│   ├── CardLoader.java                  // Card loading from JSON
│   └── PlacementValidator.java          // Card placement validation
│
├── Main.java                            // Application entry point
└── Server.java                          // Server setup and player initialization

src/main/resources/
└── cards.json                           // Card definitions

src/test/java/
└── (Test files)
```

## Architecture

The project follows **MVC architecture** and **SOLID principles**:

### MVC Pattern
- **Model**: Game entities (Card, Player, ResourceType, EventType) - Pure data and business logic
- **View**: I/O abstraction (IPlayerView and implementations) - Handles all user interaction
- **Controller**: Game flow managers (GameController, phase managers) - Coordinates game logic

### SOLID Principles Applied

1. **Single Responsibility Principle (SRP)**
   - Each class has one clear responsibility
   - View classes handle only I/O
   - GameController handles only game loop
   - Each manager handles one game phase

2. **Open/Closed Principle (OCP)**
   - New view types can be added without modifying Player
   - New game phases can be added as new managers
   - Resource types can be extended in ResourceType class

3. **Liskov Substitution Principle (LSP)**
   - All IPlayerView implementations are interchangeable
   - OnlinePlayer extends Player without breaking expectations
   - Bot players can substitute for regular players seamlessly

4. **Interface Segregation Principle (ISP)**
   - IPlayerView provides minimal interface for I/O
   - IGameManager provides minimal interface for phase managers
   - No unnecessary dependencies between components

5. **Dependency Inversion Principle (DIP)**
   - Player depends on IPlayerView interface, not concrete Scanner
   - GameController depends on manager abstractions
   - High-level modules don't depend on low-level details

### Key Interfaces

- **IPlayerView**: Abstraction for player I/O (console, network, bot)
- **IPlayer**: Abstraction for different player types (local, online, bot)
- **IGameManager**: Base for all game phase managers
- **ICardEffect**: Extensible card behavior system

### Design Patterns

- **Strategy Pattern**: Different player view implementations (Console, Network, Bot)
- **Manager Pattern**: Separate managers for each game phase
- **Factory Pattern**: Card creation and initialization
- **Dependency Injection**: Views injected into Player constructor

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
- New view types (GUI, web) can be added without changing model

### Testability
- View layer can be mocked for testing
- Player logic testable without I/O
- Managers can be tested independently
- Clear separation of concerns enables unit testing

### Build System
- Maven handles all dependencies
- Single command to build and package
- Fat JAR includes all dependencies for easy distribution