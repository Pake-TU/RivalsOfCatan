# Architecture and Refactoring Diagrams

This document contains visual diagrams showing the current architecture and recommended refactorings.

## Current Architecture (MVC Pattern)

```
┌─────────────────────────────────────────────────────────────┐
│                         VIEW LAYER                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Console      │  │    Bot       │  │  Network     │      │
│  │ PlayerView   │  │ PlayerView   │  │ PlayerView   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                  │                  │              │
│         └──────────────────┴──────────────────┘              │
│                            │                                 │
│                   ┌────────▼────────┐                       │
│                   │  IPlayerView    │ (Interface)           │
│                   └────────┬────────┘                       │
└────────────────────────────┼──────────────────────────────┘
                             │
┌────────────────────────────┼──────────────────────────────┐
│                    MODEL LAYER                              │
│                   ┌────────▼────────┐                       │
│             ┌────▶│     Player      │◀────┐                │
│             │     │   (494 LOC)     │     │                │
│             │     │  33 methods     │     │                │
│             │     └─────────────────┘     │                │
│             │                              │                │
│  ┌──────────┴────────┐         ┌─────────┴────────┐       │
│  │      Card         │         │  CardDeckManager │       │
│  │    (200 LOC)      │         │    (137 LOC)     │       │
│  └───────────────────┘         └──────────────────┘       │
│                                                             │
│  ┌──────────────────────────────────────────────┐         │
│  │            Model Effects                      │         │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  │         │
│  │  │ Action   │  │ Center   │  │Expansion │  │         │
│  │  │ Handler  │  │ Handler  │  │ Handler  │  │         │
│  │  └──────────┘  └──────────┘  └──────────┘  │         │
│  └──────────────────────────────────────────────┘         │
└─────────────────────────────────────────────────────────────┘
                             │
┌────────────────────────────┼──────────────────────────────┐
│                  CONTROLLER LAYER                           │
│                   ┌────────▼────────┐                       │
│                   │ GameController  │                       │
│                   │   (142 LOC)     │                       │
│                   └────────┬────────┘                       │
│                            │                                 │
│        ┌───────────────────┼───────────────────┐           │
│        │                   │                   │            │
│  ┌─────▼─────┐  ┌─────────▼────┐  ┌──────────▼───┐       │
│  │Production │  │  Replenish   │  │   Exchange   │       │
│  │ Manager   │  │   Manager    │  │   Manager    │       │
│  └───────────┘  └──────────────┘  └──────────────┘       │
│                                                             │
│  ┌─────────────────────────────────────────────┐          │
│  │         Event Handlers (12 classes)          │          │
│  │  BrigandEvent, TradeEvent, Celebration...    │          │
│  └─────────────────────────────────────────────┘          │
│                                                             │
│  ┌─────────────────────────────────────────────┐          │
│  │       ActionManager (330 LOC)                │          │
│  │  Handles TRADE3, TRADE2, LTS, PLAY...       │          │
│  └─────────────────────────────────────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

## Package Dependency Graph

```
┌─────────────────────────────────────────────────────────────┐
│                    Package Dependencies                      │
│                     (Coupling Analysis)                      │
└─────────────────────────────────────────────────────────────┘

 view (0.67 avg coupling) ───────┐
                                  │
                                  ▼
 model (2.75 avg coupling) ◀──────┼─────┐
    │                             │     │
    │                             │     │
    ├─ model.effects (3.25) ─────┘     │
    └─ model.interfaces (1.00)         │
                                        │
 controller (2.57) ─────────────────────┤
    │                                   │
    ├─ controller.events (1.58)        │
    └─ controller.interfaces (0.22)    │
                                        │
 util (1.25) ───────────────────────────┘

 network (2.00) ─────────────────────────▶ model + view

Legend:
 ──────▶  Direct dependency
 ◀──────  Reverse dependency
 (X.XX)   Average coupling score
```

## Problem: Player Class God Object

```
┌───────────────────────────────────────────────────────┐
│              Current: Player (494 LOC)                 │
│                                                        │
│  Responsibilities (SRP Violations):                   │
│  ┌─────────────────────────────────────────────┐     │
│  │ 1. Resource Management                       │     │
│  │    - gainResource(), removeResource()        │     │
│  │    - Map<String, Integer> resources          │     │
│  │                                               │     │
│  │ 2. Grid Management                           │     │
│  │    - getCard(), setCard(), expandGrid()      │     │
│  │    - List<List<Card>> principality           │     │
│  │                                               │     │
│  │ 3. Card Hand Management                      │     │
│  │    - drawCard(), playCard()                  │     │
│  │    - List<Card> hand                         │     │
│  │                                               │     │
│  │ 4. Scoring Logic                             │     │
│  │    - currentScoreAgainst()                   │     │
│  │    - calculateAdvantageTokens()              │     │
│  │                                               │     │
│  │ 5. I/O Delegation                            │     │
│  │    - sendMessage(), receiveMessage()         │     │
│  │    - IPlayerView view                        │     │
│  │                                               │     │
│  │ 6. Game State Flags                          │     │
│  │    - Set<String> flags                       │     │
│  │    - Various boolean flags                   │     │
│  └─────────────────────────────────────────────┘     │
│                                                        │
│  Issues:                                              │
│  ❌ Hard to test (33 methods)                        │
│  ❌ High coupling (5 dependencies)                   │
│  ❌ Hard to modify (changes affect many concerns)    │
│  ❌ Hard to understand (too much to know)            │
└───────────────────────────────────────────────────────┘
```

## Recommended Refactoring: Extract Responsibilities

```
┌─────────────────────────────────────────────────────────────┐
│                  REFACTORED DESIGN                           │
└─────────────────────────────────────────────────────────────┘

         ┌─────────────────────────────────┐
         │   Player (Coordinator)          │
         │   ~150 LOC, 10 methods          │
         │                                 │
         │  Collaborates with:             │
         └─────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
         ▼               ▼               ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│  Resource   │  │Principality │  │   Score     │
│  Manager    │  │    Grid     │  │ Calculator  │
│             │  │             │  │             │
│ ~100 LOC    │  │  ~120 LOC   │  │   ~80 LOC   │
│             │  │             │  │             │
│ Methods:    │  │ Methods:    │  │ Methods:    │
│ • gain()    │  │ • getCard() │  │ • calcVP()  │
│ • spend()   │  │ • setCard() │  │ • calcAdv() │
│ • getCount()│  │ • expand()  │  │ • totalScore│
│ • has()     │  │ • print()   │  │             │
└─────────────┘  └─────────────┘  └─────────────┘

Benefits:
✅ Each class has single responsibility
✅ Easy to test independently
✅ Low coupling (1-2 dependencies each)
✅ Easy to modify (changes localized)
✅ Easy to understand (focused purpose)

Reduced Metrics:
• Avg LOC/class: 112.5 (was 494)
• Avg methods: 6.5 (was 33)
• Max coupling: 2 (was 5)
```

## Recommended: Command Pattern for Actions

```
┌─────────────────────────────────────────────────────────────┐
│           Current: ActionManager (330 LOC)                   │
│                                                              │
│  if (cmd.startsWith("TRADE3")) {                            │
│      // 20 lines of code                                    │
│  } else if (cmd.startsWith("TRADE2")) {                     │
│      // 25 lines of code                                    │
│  } else if (cmd.startsWith("LTS")) {                        │
│      // 40 lines of code                                    │
│  } else if (cmd.startsWith("PLAY")) {                       │
│      // 100+ lines of code                                  │
│  } ...                                                       │
│                                                              │
│  Issues:                                                     │
│  ❌ Violates Open/Closed Principle                          │
│  ❌ Long methods (47 LOC per method avg)                    │
│  ❌ Hard to add new commands                                │
└─────────────────────────────────────────────────────────────┘

                          ↓ REFACTOR TO ↓

┌─────────────────────────────────────────────────────────────┐
│              Command Pattern Design                          │
└─────────────────────────────────────────────────────────────┘

        ┌────────────────────────────┐
        │     ICommand Interface     │
        │  - canExecute()            │
        │  - execute()               │
        │  - getDescription()        │
        └────────────────────────────┘
                     △
        ┌────────────┼────────────┐
        │            │            │
┌───────▼──────┐ ┌──▼────────┐ ┌▼────────────┐
│Trade3Command │ │LTSCommand │ │PlayCommand  │
│  ~30 LOC     │ │  ~40 LOC  │ │   ~60 LOC   │
└──────────────┘ └───────────┘ └─────────────┘

┌────────────────────────────────────────┐
│   CommandRegistry (50 LOC)             │
│                                        │
│  register("TRADE3", new Trade3Cmd())  │
│  register("LTS", new LTSCommand())    │
│  ...                                   │
│                                        │
│  execute(cmdString, player, opponent) │
└────────────────────────────────────────┘

┌────────────────────────────────────────┐
│   ActionManager (80 LOC)               │
│                                        │
│  List available commands               │
│  Get user input                        │
│  Delegate to CommandRegistry           │
└────────────────────────────────────────┘

Benefits:
✅ Open/Closed Principle - add commands without modifying manager
✅ Small focused classes (~40 LOC each)
✅ Easy to test each command independently
✅ Easy to add new commands (just implement interface)
```

## Test Coverage Visualization

```
┌─────────────────────────────────────────────────────────────┐
│                    Current Test Coverage                     │
│                        (~15% overall)                        │
└─────────────────────────────────────────────────────────────┘

Package              Classes    Tests    Coverage
─────────────────────────────────────────────────────
model                   5         2       40%  ████
  Card                             ✅
  AdvantageToken                   ✅
  Player                           ❌
  CardDeckManager                  ❌
  ResourceType                     ❌

model.effects           4         1       25%  ██
  CenterCardHandler                ✅
  ActionHandler                    ❌
  ExpansionHandler                 ❌
  RegionHelper                     ❌

controller              7         0        0%
  GameController                   ❌
  ActionManager                    ❌
  ProductionManager                ❌
  ReplenishManager                 ❌
  ExchangeManager                  ❌
  InitializationMgr                ❌
  EventResolver                    ❌

controller.events      12         1        8%  █
  EventCardDrawEvent               ✅
  All others (11)                  ❌

util                    5         2       40%  ████
  CostParser                       ✅
  PlacementValidator               ✅
  DiceRoller                       ❌
  CardLoader                       ❌
  PlayerInputHelper                ❌

view                    5         0        0%
  All views                        ❌

network                 1         0        0%
  OnlinePlayer                     ❌

Legend: ✅ Has tests  ❌ No tests  █ Coverage bar

PRIORITY: Add tests for ❌ classes, especially:
  1. Player (core model)
  2. GameController (game flow)
  3. ActionManager (complex logic)
```

## Recommended Test Structure

```
┌─────────────────────────────────────────────────────────────┐
│                   Target: >70% Coverage                      │
└─────────────────────────────────────────────────────────────┘

test/
├── model/
│   ├── PlayerTest.java               ← NEW (Priority 1)
│   │   ✓ testResourceManagement
│   │   ✓ testGridOperations
│   │   ✓ testScoring
│   │   ✓ testCardPlacement
│   │
│   ├── CardTest.java                 ← NEW
│   ├── CardDeckManagerTest.java      ← NEW
│   └── ResourceTypeTest.java
│
├── controller/
│   ├── GameControllerTest.java      ← NEW (Priority 2)
│   │   ✓ testGameLoop
│   │   ✓ testWinCondition
│   │   ✓ testPhaseSequence
│   │
│   ├── ActionManagerTest.java       ← NEW (Priority 3)
│   │   ✓ testTrade3Command
│   │   ✓ testLTSCommand
│   │   ✓ testPlayCommand
│   │
│   ├── ProductionManagerTest.java   ← NEW
│   └── ... (other managers)
│
├── view/
│   ├── MockPlayerViewTest.java      ← NEW
│   └── PlayerFormatterTest.java     ← NEW
│
└── integration/
    ├── FullGameFlowTest.java        ← NEW
    └── MultiPlayerTest.java         ← NEW

Estimated effort: 3-5 days to reach 70% coverage
```

## Coupling Reduction Strategy

```
Current High Coupling:
┌──────────┐      ┌──────────┐
│  Player  │─────▶│ Card     │
│          │      │          │
│  5 deps  │─────▶│ effects  │
│          │      │          │
└──────────┘      └──────────┘
     │
     └─────────▶ view, util, interfaces

Recommended: Interface-based Decoupling
┌──────────┐      ┌──────────────┐      ┌──────────┐
│  Player  │─────▶│ ICardEffect  │◀─────│ Card     │
│          │      │  (interface) │      │ effects  │
│  3 deps  │      └──────────────┘      │          │
└──────────┘                            └──────────┘
     │
     └─────────▶ IResourceMgr, IGrid

Benefits:
✅ Player coupling: 5 → 3
✅ Easy to mock for testing
✅ Effects can change without affecting Player
```

---

## Summary

These diagrams show:
1. **Current Architecture** - Clean MVC but with some bloated classes
2. **Coupling Analysis** - Generally good, model layer slightly high
3. **Player Refactoring** - Extract 3-4 focused classes from 494-LOC Player
4. **Command Pattern** - Refactor ActionManager to improve OCP
5. **Test Coverage** - Current gaps and recommended additions
6. **Coupling Reduction** - Use interfaces to decouple model from effects

**Main Takeaway:** The architecture is fundamentally sound (MVC + SOLID), but a few key refactorings would significantly improve maintainability and testability.
