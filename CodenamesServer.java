package HW4;

import java.io.* ;
import java.net.* ;
import java.util.* ;
import java.util.Random;
import java.util.random.RandomGenerator;

import java.util.Timer;
import java.util.TimerTask;

public final class CodenamesServer {
	
	private static List<PlayerRequest> players = new ArrayList<>();
	private static List<String> gameCards = new ArrayList<>();
	private static List<String> redCards = new ArrayList<>();
	private static List<String> correctGuessRed = new ArrayList<>();
	private static List<String> blueCards = new ArrayList<>();
	private static List<String> correctGuessBlue = new ArrayList<>();
	private static List<String> correctBlueCards = new ArrayList<>();
	private static String blackCard;
	private static String disqualifiedTeam;
	private static String turn;
	private static String hint;
	private static String winner;
	private static boolean gameRunning;
	private static boolean maxPlayers;
	private static volatile boolean guessDone;
	private static volatile boolean hintDone;
	private static volatile boolean turnInProcess;
	private static int redCorrect;
	private static int blueCorrect;
	private static int targetWords;
	
	public static void main(String argv[]) throws Exception {
		
		// Set the port number.
		int port = 6789;
		
		// Establish the listen socket
		ServerSocket socket = new ServerSocket(port);
		
		System.out.println("Waiting for four players to join...");
		
		while (players.size() < 4) {
			
			// Listen for a TCP connection request.
			Socket connection = socket.accept();
		
			// Construct an object to process the player request message.
			PlayerRequest request = new PlayerRequest(connection);
			
			players.add(request);
			
			System.out.println("Player " + players.size() + " has joined the room.");
		
			// Create a new thread to process the request.
			Thread thread = new Thread(request);
			thread.start();
		}
		
		System.out.println("All four players have joined the room. The game will start now!");
		
		assignTeams();
		assignRoles();
        setUpBoard();
        turn = "Red";
		hint = "";
		targetWords = 0;
		hintDone = false;
		guessDone = false;
        redCorrect=0;
        blueCorrect=0;
        winner=null;
        gameRunning = true;
	}

	private static void assignTeams() {
		players.get(0).setTeam("Red");
        players.get(1).setTeam("Blue");
        players.get(2).setTeam("Red");
        players.get(3).setTeam("Blue");
    }
	
	private static void assignRoles() {
		players.get(0).setRole("Spymaster");
        players.get(1).setRole("Spymaster");
        players.get(2).setRole("Agent");
        players.get(3).setRole("Agent");
    }
	
	private static void setUpBoard() {
		
		Random rand = new Random();
		
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(
	            "Africa", "Agent", "Air", "Alien", "Alps", "Amazon", "Ambulance", "America", "Angel", "Antarctica", 
	            "Apple", "Arm", "Atlantis", "Australia", "Aztec", "Back", "Ball", "Band", "Bank", "Bar", "Bark", "Bat", 
	            "Battery", "Beach", "Bear", "Beat", "Bed", "Beijing", "Bell", "Belt", "Berlin", "Bermuda", "Berry", 
	            "Bill", "Block", "Board", "Bolt", "Bomb", "Bond", "Boom", "Boot", "Bottle", "Bow", "Box", "Bridge", 
	            "Brush", "Buck", "Buffalo", "Bug", "Bugle", "Button", "Calf", "Canada", "Cap", "Capital", "Car", 
	            "Card", "Carrot", "Casino", "Cast", "Cat", "Cell", "Centaur", "Center", "Chair", "Change", "Charge", 
	            "Check", "Chest", "Chick", "China", "Chocolate", "Church", "Circle", "Cliff", "Cloak", "Club", "Code", 
	            "Cold", "Comic", "Compound", "Concert", "Conductor", "Contract", "Cook", "Copper", "Cotton", "Court", 
	            "Cover", "Crane", "Crash", "Cricket", "Cross", "Crown", "Cycle", "Czech", "Dance", "Date", "Day", 
	            "Death", "Deck", "Degree", "Diamond", "Dice", "Dinosaur", "Disease", "Doctor", "Dog", "Draft", 
	            "Dragon", "Dress", "Drill", "Drop", "Duck", "Dwarf", "Eagle", "Egypt", "Embassy", "Engine", "England", 
	            "Europe", "Eye", "Face", "Fair", "Fall", "Fan", "Fence", "Field", "Fighter", "Figure", "File", "Film", 
	            "Fire", "Fish", "Flute", "Fly", "Foot", "Force", "Forest", "Fork", "France", "Game", "Gas", "Genius", 
	            "Germany", "Ghost", "Giant", "Glass", "Glove", "Gold", "Grace", "Grass", "Greece", "Green", "Ground", 
	            "Ham", "Hand", "Hawk", "Head", "Heart", "Helicopter", "Himalayas", "Hole", "Hollywood", "Honey", 
	            "Hood", "Hook", "Horn", "Horse", "Horseshoe", "Hospital", "Hotel", "Ice", "Ice cream", "India", 
	            "Iron", "Ivory", "Jack", "Jam", "Jet", "Jupiter", "Kangaroo", "Ketchup", "Key", "Kid", "King", "Kiwi", 
	            "Knife", "Knight", "Lab", "Lap", "Laser", "Lawyer", "Lead", "Lemon", "Leprechaun", "Life", "Light", 
	            "Limousine", "Line", "Link", "Lion", "Litter", "Loch ness", "Lock", "Log", "London", "Luck", "Mail", 
	            "Mammoth", "Maple", "Marble", "March", "Mass", "Match", "Mercury", "Mexico", "Microscope", 
	            "Millionaire", "Mine", "Mint", "Missile", "Model", "Mole", "Moon", "Moscow", "Mount", "Mouse", 
	            "Mouth", "Mug", "Nail", "Needle", "Net", "New york", "Night", "Ninja", "Note", "Novel", "Nurse", 
	            "Nut", "Octopus", "Oil", "Olive", "Olympus", "Opera", "Orange", "Organ", "Palm", "Pan", "Pants", 
	            "Paper", "Parachute", "Park", "Part", "Pass", "Paste", "Penguin", "Phoenix", "Piano", "Pie", "Pilot", 
	            "Pin", "Pipe", "Pirate", "Pistol", "Pit", "Pitch", "Plane", "Plastic", "Plate", "Platypus", "Play", 
	            "Plot", "Point", "Poison", "Pole", "Police", "Pool", "Port", "Post", "Pound", "Press", "Princess", 
	            "Pumpkin", "Pupil", "Pyramid", "Queen", "Rabbit", "Racket", "Ray", "Revolution", "Ring", "Robin", 
	            "Robot", "Rock", "Rome", "Root", "Rose", "Roulette", "Round", "Row", "Ruler", "Satellite", "Saturn", 
	            "Scale", "School", "Scientist", "Scorpion", "Screen", "Scuba diver", "Seal", "Server", "Shadow", 
	            "Shakespeare", "Shark", "Ship", "Shoe", "Shop", "Shot", "Sink", "Skyscraper", "Slip", "Slug", 
	            "Smuggler", "Snow", "Snowman", "Sock", "Soldier", "Soul", "Sound", "Space", "Spell", "Spider", 
	            "Spike", "Spine", "Spot", "Spring", "Spy", "Square", "Stadium", "Staff", "Star", "State", "Stick", 
	            "Stock", "Straw", "Stream", "Strike", "String", "Sub", "Suit", "Superhero", "Swing", "Switch", "Table", 
	            "Tablet", "Tag", "Tail", "Tap", "Teacher", "Telescope", "Temple", "Theater", "Thief", "Thumb", "Tick", 
	            "Tie", "Time", "Tokyo", "Tooth", "Torch", "Tower", "Track", "Train", "Triangle", "Trip", "Trunk", 
	            "Tube", "Turkey", "Undertaker", "Unicorn", "Vacuum", "Van", "Vet", "Wake", "Wall", "War", "Washer", 
	            "Washington", "Watch", "Water", "Wave", "Web", "Well", "Whale", "Whip", "Wind", "Witch", "Worm", 
	            "Yard"
	        ));
		
		// randomly assign 25 words for the game
		for (int i=0; i<25; i++) {
			boolean wordAdded = false;
			while (wordAdded == false) {
				String randomWord = wordList.get(rand.nextInt(wordList.size()));
				if(!gameCards.contains(randomWord)){
					gameCards.add(randomWord);
					wordAdded = true;
				}
			}
		}
		
		// randomly assign 5 red cards
		for (int i=0; i<5; i++){
			boolean wordAdded = false;
			while (wordAdded == false) {
				String redWord = gameCards.get(rand.nextInt(gameCards.size()));
				if(!redCards.contains(redWord)){
					redCards.add(redWord);
					wordAdded = true;
				}
			}
		}
		
		// randomly assign 5 blue cards
		for (int i=0; i<5; i++){
			boolean wordAdded = false;
			while (wordAdded == false) {
				String blueWord = gameCards.get(rand.nextInt(gameCards.size()));
				if(!redCards.contains(blueWord) && !blueCards.contains(blueWord)){
					blueCards.add(blueWord);
					wordAdded = true;
				}
			}
		}
		
		// randomly assign black card
		boolean wordAdded = false;
		while (wordAdded == false) {
			String blackWord = gameCards.get(rand.nextInt(gameCards.size()));
			if(!redCards.contains(blackWord) && !blueCards.contains(blackWord)){
				blackCard = blackWord;
				wordAdded = true;
			}
		}
	}
	
	public static List<String> getGameCards() {
		return gameCards;
	}

	public static List<String> getRedCards() {
		return redCards;
	}

	public static List<String> getBlueCards() {
		return blueCards;
	}
	
	public static List<String> getCorrectGuessRed() {
		return correctGuessRed;
	}

	public static List<String> getCorrectGuessBlue() {
		return correctGuessBlue;
	}
	
	public static void addCorrectGuessRed(String str) {
		correctGuessRed.add(str);
	}

	public static void addCorrectGuessBlue(String str) {
		correctGuessBlue.add(str);
	}

	public static String getBlackCard() {
		return blackCard;
	}

	public static boolean isGameRunning() {
		return gameRunning;
	}

	public static void setGameRunning(boolean gameRunning) {
		CodenamesServer.gameRunning = gameRunning;
	}

	public static int getRedCorrect() {
		return redCorrect;
	}

	public static void setRedCorrect(int redCorrect) {
		CodenamesServer.redCorrect = redCorrect;
	}

	public static int getBlueCorrect() {
		return blueCorrect;
	}

	public static void setBlueCorrect(int blueCorrect) {
		CodenamesServer.blueCorrect = blueCorrect;
	}

	public static List<PlayerRequest> getPlayers() {
		return players;
	}
	
	public static String getTurn() {
		return turn;
	}
	
	public static void setTurn(String str) {
		turn = str;
	}
	
	public static void setHint(String givenHint) {
		hint = givenHint;
	}
	
	public static String getHint() {
		return hint;
	}
	
	public static int getTargetWords() {
		return targetWords;
	}

	public static void setTargetWords(int num) {
		targetWords = num;
	}
	
	public static boolean getGuessDone() {
		return guessDone;
	}

	public static void setGuessDone(boolean decision) {
		guessDone = decision;
	}
	
	public static boolean getHintDone() {
		return hintDone;
	}

	public static void setHintDone(boolean decision) {
		hintDone = decision;
	}
	
	public static boolean getTurnInProcess() {
		return turnInProcess;
	}

	public static void setTurnInProcess(boolean decision) {
		turnInProcess = decision;
	}
	
	public static String getDisqualifiedTeam() {
		return disqualifiedTeam;
	}

	public static void setDisqualifiedTeam(String team) {
		CodenamesServer.disqualifiedTeam = team;
		
		if (disqualifiedTeam == "Red") {
			winner = "Blue";
		} else {
			winner = "Red";
		}
	}

	public static void incrementRedCorrect() {
		redCorrect++;
	}
	
	public static void incrementBlueCorrect() {
		blueCorrect++;
	}
	
	public static void changeTurn() {
		
		if(turn.equals("Red")) {
			turn = "Blue";
		} else {
			turn = "Red";
		}
		
		hint = "";
		targetWords = 0;
		hintDone = false;
		guessDone = false;
	}
	
	public static void setWinner(String str) {
		winner=str;
	}
	
	public static String getWinner() {
		return winner;
	}
	
	public static void endGame() {
		gameRunning = false;
	}
	
}

final class PlayerRequest implements Runnable {
	
	Socket socket;
	String role;
	String team;
	final static String CRLF = "\r\n";
	
	// Constructor
	public PlayerRequest(Socket socket) throws Exception {
		this.socket = socket;
	}
	
	// Implement the run() method of the Runnable interface.
	public void run() {
		try {
			runPlayerGame();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void runPlayerGame() throws Exception {
		
		InputStream is = socket.getInputStream();;
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));;
		
		os.writeBytes("Waiting for other players to join..." + CRLF);
		
		while (CodenamesServer.isGameRunning() == false) {
			Thread.sleep(5000);
		}
		
		os.writeBytes("The game has started!"+CRLF);
		os.writeBytes("Your team is:" + CRLF);
		os.writeBytes(team + CRLF);
		os.writeBytes("Your role is:" + CRLF);
		os.writeBytes(role + CRLF);
		
		
		// if role is spy master,  print game cards, red cards, blue cards, and black card
		if (role.equals("Spymaster")){
			os.writeBytes("Game Cards:");
			for (int i=0; i<25; i++) {
				os.writeBytes(CodenamesServer.getGameCards().get(i) + CRLF);
			}
			os.writeBytes("Red Cards:");
			for (int i=0; i<5; i++) {
				os.writeBytes(CodenamesServer.getRedCards().get(i) + CRLF);
			}
			os.writeBytes("Blue Cards:");
			for (int i=0; i<5; i++) {
				os.writeBytes(CodenamesServer.getBlueCards().get(i) + CRLF);
			}
			os.writeBytes("Black Card:" + CodenamesServer.getBlackCard()+CRLF);
		}
		
		// if role is agent, print only the game cards
		if (role.equals("Agent")) {
			os.writeBytes("Game Cards:");
			for (int i=0; i<25; i++) {
				os.writeBytes(CodenamesServer.getGameCards().get(i) + CRLF);
			}
		}
		
		// while the game is running, meaning no one has won
		while (CodenamesServer.isGameRunning() == true) {
			
			// print turn
			os.writeBytes("Turn:" + CRLF);
			os.writeBytes(CodenamesServer.getTurn() + CRLF);
			
			// set turn flag to true
			CodenamesServer.setTurnInProcess(true);
			
			// if it is the player's turn
			if (CodenamesServer.getTurn() == team) {

				// prompt the spy master to provide a hint and the # of words correlated with it
				if(role.equals("Spymaster")) {
					
					os.writeBytes("Please provide a hint for your team." + CRLF);
	            
	                while (CodenamesServer.getHint().equals("")) {
	                	String hint = br.readLine();
	                    if (!hint.equals("")) {
	                    	CodenamesServer.setHint(hint);
	                        os.writeBytes("Hint received!" + CRLF);
	                    }
	                }
	                
	                os.writeBytes("Please provide the number of words your hint correlates with." + CRLF);
	                
	                int targetWords = 0;
	                
	                while (CodenamesServer.getTargetWords() == 0) {
	                	String resp = br.readLine();
	                	if (resp != null) {
	                		targetWords = Integer.parseInt(resp);
	                		CodenamesServer.setTargetWords(targetWords);
	                		os.writeBytes("Number of words received!" + CRLF);
	                	}
	                }
	                
	                // set hint flag to be true so agent can begin guessing
	                CodenamesServer.setHintDone(true);
	                
	                // do not proceed until agent is done guessing
	                os.writeBytes("Waiting for guess!"+ CRLF);
	                while (!CodenamesServer.getGuessDone()) {
						Thread.sleep(5000);
					}
				}

				// prompt the agent to guess a word/words
				if (role.equals("Agent")){
					 
					// do not begin guessing until spy master is done giving a hint
					os.writeBytes("Waiting for hint!"+ CRLF);
					while (!CodenamesServer.getHintDone()) {
						Thread.sleep(5000);
					}
					
					// print the hint & number of associated words
					os.writeBytes("The hint for this turn is " + CodenamesServer.getHint() + CRLF);
					os.writeBytes("The number of words this hunt correlated with is" + CodenamesServer.getTargetWords() + CRLF);

					
					// print number of guesses for this turn
					int guessesLeft = CodenamesServer.getTargetWords();
					os.writeBytes("You will have " + guessesLeft + " guess(es)." + CRLF);

					
					// while they have guesses left, get guess from agent and pass it to the handleGuess function
					while (guessesLeft > 0) {

						String guess = null;

						os.writeBytes("Please guess a word for your team" + CRLF);

						while (guess == null) {
							guess = br.readLine();
							if (guess != null) {
								os.writeBytes("Your hint was received!" + CRLF);
								os.writeBytes(handleGuess(guess));
							}
						}

						guessesLeft--;
						os.writeBytes("You have " + guessesLeft + " guess(es) left." + CRLF);
					}
					
					
					// set guess done flag to true so that the spy master thread can continue
					CodenamesServer.setGuessDone(true);
					
					// set turn flag to false
					CodenamesServer.setTurnInProcess(false);
				}
				
				Thread.sleep(5000);

			} else {
				
				// if it is not the players turn, make the thread wait until the game is over
				os.writeBytes("No actions necessary at the moment. Please wait for your turn." + CRLF);
				
				while (CodenamesServer.getTurnInProcess()) {
					Thread.sleep(5000);
				}
				
				Thread.sleep(5000);
			}
			
			// print current score after turn
			os.writeBytes("The score is now..." + CRLF + "Red Team: "+ CodenamesServer.getRedCorrect() + CRLF + "Blue Team: " + CodenamesServer.getBlueCorrect() + CRLF);
			
			// check if any team has won, if it has the game running flag will be set to false
			checkWinConditions();
			
			// only one thread will change the turn so that it is not changed by each player
			if (team.equals("Red") && role.equals("Spymaster")) {
				CodenamesServer.changeTurn();
			}
			
			Thread.sleep(5000);
		}
		
		// print winner
		os.writeBytes("The winner is the  "+ CodenamesServer.getWinner() + " team!");
        
		// close socket
		os.close();
		br.close();
		socket.close();
	}

	private String handleGuess(String guess){
		
		// if its the players turn, and their guess is correct, and their guess wasn't already guessed correctly, then add a point and add it to the correct word list for their team
		if (CodenamesServer.getTurn().equals("Red") && team.equals("Red") 
				&& CodenamesServer.getRedCards().contains(guess) && CodenamesServer.getCorrectGuessRed().contains(guess)==false) {
            CodenamesServer.incrementRedCorrect();
            CodenamesServer.addCorrectGuessRed(guess);
            return("Correct! " + guess + " belongs to the Red Team." + CRLF);
            
        } else if (CodenamesServer.getTurn().equals("Blue") && team.equals("Blue") 
        		&& CodenamesServer.getBlueCards().contains(guess) && CodenamesServer.getCorrectGuessBlue().contains(guess)==false) {
            CodenamesServer.incrementBlueCorrect();
            CodenamesServer.addCorrectGuessBlue(guess);
            return("Correct! " + guess + " belongs to the Blue Team." + CRLF);
        // if their guess is the black card, end game and disqualify them
        } else if (guess.equals(CodenamesServer.getBlackCard())) {
        	CodenamesServer.endGame();
        	CodenamesServer.setDisqualifiedTeam(team);
            return("Game Over! You guessed the Black (Assassin) Word. Your team loses!" + CRLF);
            
        } else {
        	return("Sorry! That guess didn't belong to your team." + CRLF);
        }
	}
	
	private void checkWinConditions() {
		if(CodenamesServer.getRedCorrect()==5) {
			CodenamesServer.setWinner(CRLF);
			CodenamesServer.endGame();
		}
		if(CodenamesServer.getRedCorrect()==5) {
			CodenamesServer.setWinner(CRLF);
			CodenamesServer.endGame();
		}
	}
	
	public void setRole(String role) {
        this.role = role;
    }
	
	public void setTeam(String team) {
        this.team = team;
    }
	
}
