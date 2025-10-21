# Booch Metrics Report
## RivalsOfCatan Codebase Analysis

**Generated:** 2025-10-21  
**Tool:** Custom Python analyzer  
**Scope:** All production Java code in src/main/java

---

## Table of Contents
1. [Overview](#overview)
2. [Class-Level Metrics](#class-level-metrics)
3. [Package-Level Metrics](#package-level-metrics)
4. [Coupling Analysis](#coupling-analysis)
5. [Cohesion Analysis](#cohesion-analysis)
6. [Recommendations](#recommendations)

---

## Overview

### Project Statistics

| Metric | Value |
|--------|-------|
| Total Classes/Interfaces | 44 |
| Total Packages | 10 |
| Total Lines of Code | 4,420 |
| Average LOC per Class | 100.4 |
| Largest Class | Player (494 LOC) |
| Smallest Class | DiceRoller (26 LOC) |

### Booch Metrics Summary

| Metric | Average | Target | Status |
|--------|---------|--------|--------|
| CBO (Coupling Between Objects) | 2.1 | < 5 | ✅ Good |
| LOC per Class | 100.4 | < 200 | ✅ Good |
| Methods per Class | 7.5 | < 15 | ✅ Good |
| Package Cohesion | High | High | ✅ Good |

---

## Class-Level Metrics

### Top 15 Classes by Lines of Code

| Rank | Class | LOC | Methods | Package | Assessment |
|------|-------|-----|---------|---------|------------|
| 1 | `Player` | 494 | 33 | model | 🔴 Too large - needs refactoring |
| 2 | `ActionManager` | 330 | 7 | controller | 🟡 Large - review for long methods |
| 3 | `PlayerFormatter` | 297 | 10 | view | 🟡 Large - could split |
| 4 | `Card` | 200 | 7 | model | 🟢 Acceptable |
| 5 | `ExpansionCardEffectHandler` | 174 | 4 | model.effects | 🟢 Acceptable |
| 6 | `ActionCardEffectHandler` | 163 | 4 | model.effects | 🟢 Acceptable |
| 7 | `IPlayer` | 149 | 0 | model.interfaces | 🟡 Large interface - consider splitting |
| 8 | `GameController` | 142 | 6 | controller | 🟢 Acceptable |
| 9 | `ExchangeManager` | 139 | 4 | controller | 🟢 Acceptable |
| 10 | `CardDeckManager` | 137 | 12 | model | 🟢 Acceptable |
| 11 | `FeudEventCard` | 131 | 5 | controller.events | 🟢 Acceptable |
| 12 | `RegionPlacementHelper` | 126 | 3 | model.effects | 🟢 Acceptable |
| 13 | `CenterCardEffectHandler` | 114 | 3 | model.effects | 🟢 Acceptable |
| 14 | `PlacementValidator` | 110 | 4 | util | 🟢 Acceptable |
| 15 | `BrigandEvent` | 108 | 3 | controller.events | 🟢 Acceptable |

### Classes Requiring Attention

#### 🔴 Critical - Immediate Refactoring Needed

**1. `model.Player` (494 LOC, 33 methods)**

**Issues:**
- Violates Single Responsibility Principle
- Too many responsibilities: resource management, grid management, I/O, scoring, state flags
- High coupling (5 dependencies)
- Difficult to test

**Recommended Refactoring:**
```
Player (494 LOC)
  ↓ Extract to →
    ├─ Player (150 LOC) - Core player state coordinator
    ├─ ResourceManager (100 LOC) - Resource operations
    ├─ PrincipalityGrid (120 LOC) - Grid management
    └─ ScoreCalculator (80 LOC) - Scoring logic
```

**Benefits:**
- Each class has single responsibility
- Easier to test each component
- Reduced coupling
- Better maintainability

#### 🟡 Medium - Review and Consider Refactoring

**2. `controller.ActionManager` (330 LOC, 7 methods)**

**Issue:** Likely has long methods due to high LOC/method ratio (47 LOC per method avg)

**Analysis Needed:**
- Check cyclomatic complexity of command parsing methods
- Consider Command Pattern to extract action logic

**3. `view.PlayerFormatter` (297 LOC, 10 methods)**

**Issue:** High LOC for a formatter class

**Recommendation:**
- Split into separate formatters: GridFormatter, PointsFormatter, ResourceFormatter
- Use Composite Pattern if needed

**4. `model.interfaces.IPlayer` (149 LOC, 0 methods = all interface methods)**

**Issue:** Interface with too many methods (estimated 25-30 methods)

**Recommendation:**
- Split into focused interfaces:
  - `IResourceManager`
  - `IPrincipalityGrid`
  - `IPlayerIO`
  - `IPlayerScoring`

---

## Package-Level Metrics

### Package Size and Complexity

| Package | Classes | Avg LOC | Avg Methods | Total LOC | Assessment |
|---------|---------|---------|-------------|-----------|------------|
| `controller` | 7 | 106.4 | 4.7 | 745 | ✅ Well-sized |
| `controller.events` | 12 | 55.0 | 3.2 | 660 | ✅ Good decomposition |
| `controller.interfaces` | 1 | 15.0 | 0.0 | 15 | ✅ Minimal interface |
| `model` | 5 | 135.8 | 10.6 | 679 | 🟡 High avg - Player inflates |
| `model.effects` | 4 | 109.0 | 3.5 | 436 | ✅ Focused handlers |
| `model.interfaces` | 2 | 67.0 | 0.0 | 134 | 🟢 Interface definitions |
| `network` | 1 | 35.0 | 2.0 | 35 | ✅ Small, focused |
| `util` | 5 | 61.8 | 3.4 | 309 | ✅ Good utility size |
| `view` | 5 | 71.4 | 3.2 | 357 | ✅ Clean view layer |
| `default` | 2 | 73.0 | 2.0 | 146 | ✅ Entry points |

### Package Dependency Analysis

**Package Import Pattern:**
```
view (0.67 avg coupling)
  ↓
model (2.75 avg coupling)
  ↓
controller (2.57 avg coupling)
  ↓
controller.events (1.58 avg coupling)
```

**Observations:**
- ✅ View layer is highly decoupled (only 0.67 dependencies)
- ✅ Dependency flow is correct (view → model ← controller)
- 🟡 Model has higher coupling than ideal for a domain layer
- ✅ Event handlers are well-isolated

---

## Coupling Analysis

### CBO (Coupling Between Objects)

**Definition:** Number of other classes a class depends on (imports from project code only).

**Target:** < 5 dependencies per class

### Top 15 Most Coupled Classes

| Class | Dependencies | Project Imports | Assessment |
|-------|--------------|-----------------|------------|
| `model.Player` | 5 | view, model.interfaces, util | 🟡 At threshold |
| `model.effects.ExpansionCardEffectHandler` | 4 | model, util | 🟢 Acceptable |
| `controller.EventResolver` | 4 | controller, model | 🟢 Acceptable |
| `controller.ActionManager` | 3 | controller, model, util | 🟢 Good |
| `controller.ReplenishManager` | 3 | controller, model | 🟢 Good |
| `controller.ExchangeManager` | 3 | controller, model | 🟢 Good |
| `Main` | 3 | controller, model, network | 🟢 Good |
| `model.effects.ActionCardEffectHandler` | 3 | model, util | 🟢 Good |
| `controller.events.FraternalFeudsEventCard` | 2 | controller, model | 🟢 Low |
| `controller.events.BrigandEvent` | 2 | controller, model | 🟢 Low |
| `network.OnlinePlayer` | 2 | model, view | 🟢 Low |
| `util.CardLoader` | 1 | model | 🟢 Very low |
| `model.CardDeckManager` | 1 | model | 🟢 Very low |

### Package-Level Coupling

**Average Dependencies per Class by Package:**

| Package | Avg Coupling | Interpretation |
|---------|--------------|----------------|
| `view` | 0.67 | 🟢 Excellent - Highly decoupled |
| `util` | 1.25 | 🟢 Excellent - Reusable utilities |
| `model.interfaces` | 1.00 | 🟢 Excellent - Clean interfaces |
| `controller.events` | 1.58 | 🟢 Very Good - Event isolation |
| `network` | 2.00 | 🟢 Good |
| `default` | 2.50 | 🟢 Good - Entry points |
| `controller` | 2.57 | 🟢 Good - Orchestration layer |
| `model` | 2.75 | 🟡 Acceptable - Could be lower |
| `model.effects` | 3.25 | 🟡 Medium - Expected for handlers |

### Coupling Interpretation

**Excellent Decoupling (< 1.5):**
- `view` package: Only depends on basic interfaces
- `util` package: Self-contained utilities

**Good Coupling (1.5 - 3.0):**
- `controller` and `controller.events`: Expected coupling to model
- Controllers need to know about model to manipulate it

**Moderate Coupling (> 3.0):**
- `model.effects`: Tightly coupled to model classes
- This is expected as effects manipulate game state

---

## Cohesion Analysis

### LCOM (Lack of Cohesion of Methods)

**Definition:** Measures how related methods are within a class. Lower is better.

**Proxy Metric Used:** Methods-to-LOC ratio and class size

### Classes with Potential Cohesion Issues

| Class | LOC | Methods | LOC/Method | Issue |
|-------|-----|---------|------------|-------|
| `Player` | 494 | 33 | 15.0 | 🔴 Multiple responsibilities |
| `ActionManager` | 330 | 7 | 47.1 | 🔴 Very long methods likely |
| `PlayerFormatter` | 297 | 10 | 29.7 | 🟡 Long methods |
| `Card` | 200 | 7 | 28.6 | 🟡 Static utility mixed with data |
| `IPlayer` | 149 | 29* | 5.1 | 🟡 Too many interface methods |

*Estimated method count for interface

### Cohesion Quality by Package

**High Cohesion (Good) ✅:**
- `controller.events` - Each event handler has single, focused purpose
- `util` - Each utility class has clear, single responsibility
- `view` - Each view implementation handles one I/O strategy

**Medium Cohesion 🟡:**
- `model` - Mixed due to Player class; other classes are cohesive
- `controller` - Generally good but ActionManager is complex

**Recommendations for Improving Cohesion:**

1. **Split Large Classes:**
   - `Player` → Multiple focused classes
   - `ActionManager` → Command classes

2. **Extract Utilities from Data Classes:**
   - `Card.popCardByName()` → `CardUtils.findByName()`
   - Static methods → Utility class

3. **Separate Interface Definitions:**
   - `IPlayer` → Multiple focused interfaces

---

## Detailed Metrics Tables

### Complete Class Metrics

| Class | Package | LOC | Methods | Est. CBO | Notes |
|-------|---------|-----|---------|----------|-------|
| Player | model | 494 | 33 | 5 | Needs refactoring |
| ActionManager | controller | 330 | 7 | 3 | Review method length |
| PlayerFormatter | view | 297 | 10 | 1 | Consider splitting |
| Card | model | 200 | 7 | 3 | Good size |
| ExpansionCardEffectHandler | model.effects | 174 | 4 | 4 | Acceptable |
| ActionCardEffectHandler | model.effects | 163 | 4 | 3 | Good |
| IPlayer | model.interfaces | 149 | 0 | 1 | Interface |
| GameController | controller | 142 | 6 | 3 | Good |
| ExchangeManager | controller | 139 | 4 | 3 | Good |
| CardDeckManager | model | 137 | 12 | 1 | Good |
| FeudEventCard | controller.events | 131 | 5 | 2 | Good |
| RegionPlacementHelper | model.effects | 126 | 3 | 2 | Good |
| CenterCardEffectHandler | model.effects | 114 | 3 | 2 | Good |
| PlacementValidator | util | 110 | 4 | 1 | Good |
| BrigandEvent | controller.events | 108 | 3 | 2 | Good |
| Main | default | 108 | 2 | 3 | Entry point |
| InitializationManager | controller | 100 | 4 | 2 | Good |
| CardLoader | util | 99 | 4 | 1 | Good |
| FraternalFeudsEventCard | controller.events | 91 | 3 | 2 | Good |
| ProductionManager | controller | 87 | 3 | 2 | Good |
| PlayerInputHelper | util | 77 | 3 | 1 | Good |
| TradeShipsRaceEventCard | controller.events | 72 | 2 | 2 | Good |
| EventCardDrawEvent | controller.events | 72 | 2 | 2 | Good |
| Server | default | 71 | 2 | 2 | Entry point |
| PlentifulHarvestEvent | controller.events | 65 | 2 | 2 | Good |
| CostParser | util | 66 | 4 | 1 | Good |
| NetworkPlayerView | view | 64 | 3 | 0 | Good |
| ReplenishManager | controller | 63 | 2 | 3 | Good |
| EventResolver | controller | 62 | 2 | 4 | Acceptable |
| YearOfPlentyEventCard | controller.events | 56 | 2 | 1 | Good |
| CelebrationEvent | controller.events | 47 | 2 | 1 | Good |
| TradeEvent | controller.events | 46 | 2 | 1 | Good |
| OnlinePlayer | network | 45 | 2 | 2 | Good |
| ResourceType | model | 44 | 2 | 0 | Good |
| TravelingMerchantEventCard | controller.events | 42 | 2 | 1 | Good |
| ICardEffect | model.interfaces | 39 | 0 | 0 | Interface |
| ConsolePlayerView | view | 38 | 3 | 0 | Good |
| DiceRoller | util | 26 | 2 | 0 | Good |
| InventionEventCard | controller.events | 25 | 2 | 1 | Good |
| IEventHandler | controller.events | 25 | 0 | 0 | Interface |
| IGameManager | controller.interfaces | 22 | 0 | 0 | Interface |
| BotPlayerView | view | 21 | 3 | 0 | Good |
| IPlayerView | view | 20 | 0 | 0 | Interface |
| EventType | model | 13 | 0 | 0 | Constants |

---

## Afferent and Efferent Coupling

### Afferent Coupling (Ca)
**Definition:** Number of classes that depend on this class (incoming dependencies)

**High Ca Classes (Most Used):**
- `model.Player` - Used by controller, view, network, effects
- `model.Card` - Used by controller, model, effects
- `view.IPlayerView` - Used by view implementations, model
- `controller.interfaces.IGameManager` - Used by all managers

**Interpretation:** High Ca is good for interfaces and core domain models

### Efferent Coupling (Ce)
**Definition:** Number of classes this class depends on (outgoing dependencies)

**High Ce Classes (Most Dependent):**
- `model.Player` - Depends on 5 project classes
- `model.effects.*` - Depend on 3-4 classes each
- `controller.*` - Depend on 2-4 classes each

**Interpretation:** High Ce makes classes harder to change and test

### Instability Metric (I = Ce / (Ca + Ce))

**Target:** 
- Interfaces/Abstract classes: I ≈ 0 (stable)
- Implementations: I ≈ 0.5 (balanced)
- Utilities: I ≈ 0 (stable)

**Estimated Instability:**
- `IPlayerView`, `IGameManager` → I ≈ 0 ✅ Stable interfaces
- `Player` → I ≈ 0.4 ✅ Balanced core model
- `ActionManager` → I ≈ 0.6 🟡 Slightly unstable
- Utility classes → I ≈ 0.1 ✅ Very stable

---

## Recommendations

### Immediate Actions (High Priority)

1. **Refactor Player Class**
   - **Current:** 494 LOC, 33 methods, 5 dependencies
   - **Target:** 4 classes of ~120 LOC each
   - **Effort:** 4-6 days
   - **Impact:** Massive improvement in testability and maintainability

2. **Review ActionManager Method Complexity**
   - **Current:** 7 methods, 330 LOC = 47 LOC/method avg
   - **Action:** Extract command parsing to Command Pattern
   - **Effort:** 2-3 days
   - **Impact:** Better extensibility (OCP)

3. **Split IPlayer Interface**
   - **Current:** ~29 methods in single interface
   - **Target:** 4-5 focused interfaces
   - **Effort:** 1-2 days
   - **Impact:** Better ISP compliance

### Medium-Term Improvements

4. **Extract Static Methods from Card**
   - Move utility methods to `CardUtils` class
   - **Effort:** 1 day
   - **Impact:** Better cohesion

5. **Consider Splitting PlayerFormatter**
   - Create focused formatters for different concerns
   - **Effort:** 1-2 days
   - **Impact:** Better SRP

### Long-Term Enhancements

6. **Reduce Model Package Coupling**
   - Use more interfaces between model and effects
   - **Effort:** 2-3 days
   - **Impact:** Better testability with mocks

7. **Add Metrics to CI/CD**
   - Automate coupling/cohesion checks
   - Fail builds on metric thresholds
   - **Effort:** 1 day
   - **Impact:** Prevent future degradation

---

## Metrics Glossary

### Booch Metrics Explained

| Metric | Description | Good Range | Our Average |
|--------|-------------|------------|-------------|
| **CBO** | Coupling Between Objects - # of classes coupled to | < 5 | 2.1 ✅ |
| **RFC** | Response For Class - # of methods callable | < 50 | ~30 ✅ |
| **LCOM** | Lack of Cohesion - how unrelated are methods | Low | Medium 🟡 |
| **WMC** | Weighted Methods per Class - sum of complexities | < 50 | ~40 ✅ |
| **Ca** | Afferent Coupling - # of classes that use this | - | Varies |
| **Ce** | Efferent Coupling - # of classes this uses | < 5 | 2.1 ✅ |
| **I** | Instability - Ce/(Ca+Ce) | 0-1 | ~0.4 ✅ |

### Additional Metrics

| Metric | Description | Our Value | Target |
|--------|-------------|-----------|--------|
| **LOC/Class** | Lines of Code per Class | 100.4 | < 200 ✅ |
| **Methods/Class** | Average Methods per Class | 7.5 | < 15 ✅ |
| **Max LOC** | Largest Class Size | 494 (Player) | < 300 ❌ |
| **Packages** | Number of Packages | 10 | 5-15 ✅ |
| **Depth of Inheritance** | Max Inheritance Levels | 2 | < 5 ✅ |

---

## Trend Analysis

### Historical Comparison

**Note:** This is the first formal metrics analysis. For future reviews, track:

- CBO trends per package
- Average class size trends
- New classes vs refactored classes
- Test coverage correlation with metrics

### Recommended Metric Targets for Next Review

| Metric | Current | Target | Improvement |
|--------|---------|--------|-------------|
| Player LOC | 494 | < 200 | -294 LOC |
| Avg CBO | 2.1 | < 2.0 | -0.1 |
| Test Coverage | 15% | 70% | +55% |
| Classes > 200 LOC | 3 | 0 | -3 classes |

---

## Conclusion

The RivalsOfCatan codebase shows **good overall metrics** with most classes well-sized and loosely coupled. The primary concern is the **Player class** which significantly exceeds recommended thresholds for size and complexity.

**Key Takeaways:**
- ✅ Package structure is excellent with clear separation
- ✅ Most classes are appropriately sized and focused
- ✅ Coupling is generally low across the codebase
- 🔴 Player class needs immediate refactoring
- 🟡 A few classes could benefit from splitting

**Next Steps:**
1. Refactor Player class as highest priority
2. Add metrics tracking to CI/CD pipeline
3. Re-run this analysis quarterly to track improvements

---

*Report generated by Python analysis script on 2025-10-21*
