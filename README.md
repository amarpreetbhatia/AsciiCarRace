# ASCII Car Race Game

A simple Java 17 console game where you race a car on an ASCII track for 1 minute, avoiding hurdles to achieve the highest score.

## Game Features

- Race a car represented by 'X' character
- Avoid hurdles represented by '*' character
- Stay within track boundaries represented by '|' characters
- 60-second gameplay
- Score based on distance traveled

## Controls

- Press 'a' to move left
- Press 'd' to move right
- Press Enter after each key press

## How to Build and Run

### Prerequisites
- Java 17 JDK
- Maven

### Build Instructions
```bash
cd AsciiCarRace
mvn clean package
```

### Run Instructions
```bash
java -jar target/ascii-car-race-1.0-SNAPSHOT.jar
```

## Game Design

The game follows the Single Responsibility Principle (SRP) with the following class structure:

- **Main**: Entry point that initializes all components and starts the game
- **GameEngine**: Manages the game loop, updates game state, and handles game logic
- **Car**: Represents the player's car with position and movement methods
- **Track**: Manages the track state including boundaries and hurdles
- **Hurdle**: Represents obstacles that the player must avoid
- **InputHandler**: Processes user input in a separate thread
- **Renderer**: Displays the game state to the console
