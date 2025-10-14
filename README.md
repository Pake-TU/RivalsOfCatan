# RivalsOfCatan
This is for a school assignment

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
