## README

### Codenames by Darcely Peña and Michael Freda
<br>

**Project Description:**

This project implements the popular board game Codenames using a client-server architecture through socket programming. The game is designed for four players, each connecting to a central server. Upon connection, players are divided into two teams, red and blue, with each team consisting of a Spymaster and an Agent.

The game board consists of 25 randomly selected words. Of these, five cards are designated to the red and blue team. One card is selected as the black card, as known as the assassin, which causes immediate disqualification if guessed. The remaining words are neutral and have no impact on the score.

The objective of the game is for each team to correctly guess all five of their assigned words before the opposing team does. Spymasters give one-word clues that relate to one or more words on the board assigned to their team, while Agents interpret these clues to make educated guesses. The presence of the black card adds a layer of strategy, as Spymasters must carefully avoid giving clues that might lead their teammate to select it.

The server handles all of the game's logic, including initializing the board and word assignments, assigning roles and teams, managing turn order, processing hints and guesses, tracking scores, and determining the winner. Each player is managed in a separate thread on the server, allowing for simultaneous and real-time interaction. These threads are responsible for prompting players for input, relaying server messages, and handling all communication between the server and individual clients.
<br>

**How To Play:**

The server and client classes are implemented in the Java programming language and can be run through the command line. Before starting, ensure that you have Java installed on your machine. To begin, compile all Java files using javac, then start the server class followed by the client classes in separate terminal windows. Make sure that the server and all clients are using the same port number.

Once the server is running, it will wait for four players to connect before starting the game. It will display a message each time a new player joins and will announce when the game officially begins. Each client will also receive a notification upon connecting and be informed that the game is waiting for the remaining players. When all four players are connected, the game starts automatically.

The game is played entirely through the command line. Clients will input their hints and guesses directly into the terminal, and these inputs will be sent to the server for processing. The server manages the game state, validates guesses, updates scores, and communicates the current game status back to each player.

