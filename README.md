# Ping Pong Game

A classic Ping Pong game built with JavaFX.

## Features

- **Two Game Modes:**
  - 1 Player vs AI
  - 2 Player local multiplayer
  
- **Gameplay Features:**
  - Rally counter that tracks consecutive hits
  - Speed increases every 10 rallies
  - Visual feedback with paddle flashing on impact
  - Score tracking (first to 5 wins)
  - Smooth collision detection

## Controls

### Player 1 (Left Paddle)
- `W` - Move up
- `S` - Move down

### Player 2 (Right Paddle)
- `UP Arrow` - Move up
- `DOWN Arrow` - Move down

### General
- `SPACE` - Start/restart round

## How to Run

1. Make sure you have Java and JavaFX installed
2. Compile the project:
   ```bash
   javac -d bin src/pingPong/*.java
   ```
3. Run the game:
   ```bash
   java -cp bin pingPong.PingPong
   ```

## Project Structure

```
├── src/
│   └── pingPong/
│       ├── PingPong.java       # Main application class
│       ├── GameManager.java    # Game logic and state management
│       ├── Ball.java           # Ball physics and rendering
│       ├── Paddle.java         # Paddle movement and rendering
│       └── GameElement.java    # Interface for game objects
└── bin/                        # Compiled classes
```

## Requirements

- Java 8 or higher
- JavaFX SDK

## Author

Zaid Jilla
