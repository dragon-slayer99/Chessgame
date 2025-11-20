
# ♟️ Chess Game

Welcome to the **Chess Game** project! This is a two-player chess game written in Java, featuring fundamental chess mechanics like move validation, check detection, and special rules implementation. While still a work in progress, it offers a functional chessboard with an enjoyable playing experience for two players.

---

## 🎯 Features

- **Core Gameplay:**
  - Two-player chess with standard rules.
  - Legal move detection for all pieces.
  - Turn indicator to track whose move it is.
  - Displays when a king is in check.
- **Advanced Chess Rules:**
  - Checkmate and stalemate detection.
  - Special moves: castling, en passant, and pawn promotion.
- **Next Move Highlighting:** Suggests valid moves for selected pieces.

---

## 🚧 Known Issues

- **Game State Persistence:** The game does not save progress if the window is closed.
- **Single-player Mode:** Currently supports only two players; no AI opponent is available.
- **Bugs:** Some features may not work as intended. Contributions to debug are welcome!

---

## 🛠️ Technologies Used

- **Programming Language:** Java
- **Libraries/Frameworks:** JavaFX for the graphical interface
- core java for game logic


---

## 📖 How to Play

 1. Launch the game.
 
 2. Use your mouse to move the pieces.
     
    -**Instructions:**   
      - Green color square indicates that piece can move to that square.
      - Red color indicates that the move might result in chesk so the game won't allow the move.
      - If the king is in check you must move the king out of the check since the game won't allow any other piece to be moved.
      - The game indicates the turns of the player by displaying "white's turn" or "black's turn".



---
## 🚀 Installation

### Prerequisites
- Install [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-downloads.html) version 11 or later.
- Ensure `java` and `javac` are added to your system's PATH.

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/dragon-slayer99/Chessgame.git
   ```
2. Navigate to the project directory:
   ```bash
   cd Chessgame/Chess
   ```
3. Compile the project:
   ```bash
   javac -d out *.java
   ```
4. Run the game:
   ```bash
   java -cp out Main
   ```

---

## 🤝 Contributing

This project is in active development and contributions are encouraged! Here’s how you can help:

1. Fork the repository.
2. Create a branch for your feature:
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Describe your changes"
   ```
4. Push your changes:
   ```bash
   git push origin feature-name
   ```
5. Create a Pull Request.

---

## 📝 License

MIT LICENSED

---

## 💬 Contact

For questions, suggestions, or bug reports, feel free to reach out:

- **GitHub:** [dragon-slayer99](https://github.com/dragon-slayer99)
- **Email:** guudurumahesh@gmail.com
---

Let me know if you'd like to refine this further!
