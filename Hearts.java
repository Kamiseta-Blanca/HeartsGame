import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Hearts  {
    static Scanner input = new Scanner(System.in);
    static Player[] players = new Player[4];
    static Card[] deck = DeckOfCards.getDeckOfCards();
    static int roundNumber;
    static int humanPlayers;
    static int cpuPlayers;
    static int[] points = new int[4];
    static Card[] cardsInThePile = new Card[4];
    static boolean heartsBroken = false;
    static boolean overPoints = false;
    static int pointLimit;

    public static void main(String[] args) {

        boolean legalAmountOfPlayers = false;

        Card[] shuffledCards = DeckOfCards.shuffleCards(deck);

        System.out.println("Welcome to Hearts");
        System.out.println("You are able to access the Rules of this game by typing in 'Rules' at anytime. (Except for the name selection)");
        System.out.println("You are also able to access how many Points each player has after the first Hand starts by typing 'Points'.");
        System.out.println();
        System.out.println("Please enter in the amount of human players entering this game (0 - 4)");
        while (!legalAmountOfPlayers) {
            String response = input.nextLine();
            if (response.matches("\\d+")) {
                humanPlayers = Integer.parseInt(response);
                if (humanPlayers > 4 || humanPlayers < 0) {
                    System.out.println("This Program supports up to 0-4 human players. Please choose again.");
                } else {
                    legalAmountOfPlayers = true;
                }
            } else if (response.equalsIgnoreCase("Rules")) {
                openRules();
                System.out.println("Please enter in the amount of human players entering this game (0 - 4)");
            }
        }

        cpuPlayers = 4 - humanPlayers;

        for (int x = 1; x <= humanPlayers; x++) {
            System.out.println("Please enter Player #" + x + "'s name");
            players[x - 1] = new Player(input.nextLine());
        }

        for(int i = (humanPlayers + 1); i <= 4 ; i++) {
            System.out.println("Please enter CPU #" + i + "'s name");
            players[i - 1] = new Player(input.nextLine());
        }

        System.out.println();
        System.out.println("Ok, it's time to play Hearts!");
        System.out.println("Let's deal out the cards");
        System.out.println();

        System.out.print("Shuffling");
        for (int x = 0; x < 3; x++) {
            pause();
            System.out.print(".");
        }

        pause();
        System.out.println();
        System.out.println();

        Player[] playersWithCards = DeckOfCards.dealCards(players, shuffledCards);

        if (humanPlayers > 0) {
            System.out.println("---------------------------------------------");
        }

        for (int x = 0; x < players.length; x++) {
            if (x < humanPlayers ) {
                System.out.println("Here is " + players[x].getName() + "'s Deck");
                players[x].showPlayerCards();
                pause();
            }
        }

        System.out.println("How many points would you like to play too? \n(Normal games usually go up to 100 points)");
        boolean keepAsking = true;
        while (keepAsking) {
            String response = input.nextLine();
            if (response.matches("\\d+")) {
                pointLimit = Integer.parseInt(response);
                keepAsking = false;
            } else if (response.equalsIgnoreCase("Rules")) {
                openRules();
                System.out.println("How many points would you like to play too? \n(Normal games usually go up to 100 points)");
            }
        }

        System.out.println();

        roundNumber = 1;

        while (!overPoints) {
            System.out.println("Alright, it's time to pass out the cards.");

           pause();
            System.out.println();

            for (int x = 0; x < humanPlayers; x++) {
                System.out.println(players[x].getName() + "'s Deck:");
                playersWithCards[x].showPlayerCards();
                playerPass(x);
                pause();

                System.out.println();
            }

            if (humanPlayers < 4 && (roundNumber != 4)) {
                System.out.print("Waiting for the CPUs to finish choosing what cards to pass");
                for (int x = 0; x < 3; x++) {
                    pause();
                    System.out.print(".");
                }
                System.out.println();
            }

            System.out.println();

            for (int x = humanPlayers; x < 4; x++) {
                cpuPass(x);
            }

            if (roundNumber % 4 > 0) {
                System.out.println("All the cards have been chosen, it's time to pass.");
                tradeCards(roundNumber);
            }

           pause();

            int startingPlayer = firstTrick();
            for (int x = 1; x < 13; x++) {
                startingPlayer = trick(startingPlayer);
            }
            countPoints();
            if (!overPoints) {
                roundNumber++;
                shuffledCards = DeckOfCards.shuffleCards(deck);
                DeckOfCards.dealCards(players, shuffledCards);
                System.out.println("All the cards have been played, it's time to reshuffle the deck.");

                pause();

                System.out.print("Shuffling");
                for (int x = 0; x < 3; x++) {
                    pause();
                    System.out.print(".");
                }

                pause();
                System.out.println();
                System.out.println();

                heartsBroken = false;

            } else {
                break;
            }
        }
    }


    public static void playerPass(int x) { //Determines which three cards the player (if human) wants to pass
        Scanner input = new Scanner(System.in);
        boolean playedLegalCard;
        String response;
        int cardPosition = -1;

        System.out.println(players[x].getName() + ", which three cards do you want to pass? (Use numbers)");
        int cardNumber = 0;
        Card[] playerCards = players[x].getCards();
        while (cardNumber < 3) {
            playedLegalCard = false;
            while (!playedLegalCard) {
                response = input.nextLine();
                if (response.equalsIgnoreCase("Rules")) {
                    players[x].showPlayerCards();
                    openRules();
                    System.out.println(players[x].getName() + ", which three cards do you want to pass? (Use numbers)");
                } else if (response.equalsIgnoreCase("Points") && (roundNumber > 1)) {
                    openPoints();
                    players[x].showPlayerCards();
                    System.out.println(players[x].getName() + ", which three cards do you want to pass? (Use numbers)");

                } else if (response.matches("\\d+")) {
                    cardPosition = Integer.parseInt(response);
                    if (playerCards[cardPosition - 1] == null) {
                        System.out.println("You don't have that card. Please pick another.");
                    } else {
                        playedLegalCard = true;
                    }
                }
            }
            players[x].giveAwayCard(playerCards[cardPosition - 1], cardNumber);
            players[x].giveAwayCardPosition((cardPosition - 1), cardNumber);
            System.out.println("You have chosen the " + playerCards[cardPosition - 1].getString());
            cardNumber++;
        }
    }

    public static void cpuPass(int playerNumber) { //determines which three cards the player (if CPU) wants to pass
        int cardNumber = 0;
        String[] ranks = {"Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two",};
        Card[] playerCards = players[playerNumber].getCards();
        while (cardNumber < 3) {
            for (int x = 0; x < 13; x++) {
                for (int y = 0; y < playerCards.length; y++) {
                    if (playerCards[y] == null) {
                        y++;
                    }
                    if (y == 13) {
                        break;
                    }
                    if (playerCards[y].getRank().contains(ranks[x])) {
                        players[playerNumber].giveAwayCard(playerCards[y], cardNumber);
                        players[playerNumber].giveAwayCardPosition(y, cardNumber);
                        cardNumber++;
                        if (cardNumber == 3) {
                            break;
                        }
                    }
                    if (cardNumber == 3) {
                        break;
                    }
                }
                if (cardNumber == 3) {
                    break;
                }
            }
            if (cardNumber == 3) {
                break;
            }
        }
    }

    public static void tradeCards(int roundNumber) { //Swaps cards with the other players based on Round Number
        int[] cardPosition1 = players[0].getGiveAwayPosition();
        Card[] cardsToTrade1 = players[0].getGiveAway();

        int[] cardPosition2= players[1].getGiveAwayPosition();
        Card[] cardsToTrade2 = players[1].getGiveAway();

        int[] cardPosition3 = players[2].getGiveAwayPosition();
        Card[] cardsToTrade3 = players[2].getGiveAway();

        int[] cardPosition4 = players[3].getGiveAwayPosition();
        Card[] cardsToTrade4 = players[3].getGiveAway();

        if (roundNumber % 4 == 1) {
            for (int x = 0; x < 3; x++){
                players[0].receiveCard(cardsToTrade4[x], cardPosition1[x]);
                players[1].receiveCard(cardsToTrade1[x], cardPosition2[x]);
                players[2].receiveCard(cardsToTrade2[x], cardPosition3[x]);
                players[3].receiveCard(cardsToTrade3[x], cardPosition4[x]);
            }

            for (int x = 0; x < humanPlayers; x++) {
                int position = x - 1;
                if (position == -1) {
                    position = 3;
                }
                System.out.println(players[x].getName() + " has received " + players[position].showGivenAwayCards() + " from " + players[position].getName() + ".");
            }
            System.out.println();
        }

        if (roundNumber % 4 == 2) {
            for (int x = 0; x < 3; x++) {
                players[0].receiveCard(cardsToTrade2[x], cardPosition1[x]);
                players[1].receiveCard(cardsToTrade3[x], cardPosition2[x]);
                players[2].receiveCard(cardsToTrade4[x], cardPosition3[x]);
                players[3].receiveCard(cardsToTrade1[x], cardPosition4[x]);
            }

            for (int x = 0; x < humanPlayers; x++) {
                int position = x + 1;
                if (position == 4) {
                    position = 0;
                }
                System.out.println(players[x].getName() + " has received " + players[position].showGivenAwayCards() + " from " + players[position].getName() + ".");
            }
            System.out.println();
        }

        if (roundNumber % 4 == 3) {
            for (int x = 0; x < 3; x++) {
                players[0].receiveCard(cardsToTrade3[x], cardPosition1[x]);
                players[1].receiveCard(cardsToTrade4[x], cardPosition2[x]);
                players[2].receiveCard(cardsToTrade1[x], cardPosition3[x]);
                players[3].receiveCard(cardsToTrade2[x], cardPosition4[x]);
            }
            for (int x = 0; x < humanPlayers; x++) {
                int position = x + 2;
                if (position > 3) {
                    position = position % 3;
                }
                System.out.println(players[x].getName() + " has received " + players[position].showGivenAwayCards() + " from " + players[position].getName() + ".");
            }
            System.out.println();
        }
    }

    public static int firstTrick() { //Performs the First Trick of the First Hand
        int playerNumber = -1;
        int position = -1;
        for (int x = 0; x < players.length; x++) {
            Card[] hand =  players[x].getCards();
            for (int y = 0; y < players[x].getCards().length; y++) {
                if (hand[y].getString().contains("Two") && hand[y].getString().contains("Clubs")) {
                    playerNumber = x;
                    position = y;
                    break;
                }
                if (playerNumber >= 0) {
                    break;
                }
            }
            if (playerNumber >= 0) {
                break;
            }
        }

        System.out.println(players[playerNumber].getName() + " is going first.");
        Card[] handBase =  players[playerNumber].getCards();
        cardsInThePile[0] = handBase[position];
        System.out.println(players[playerNumber].getName() + " has played the " + handBase[position].getString());
        pause();
        players[playerNumber].receiveCard(null, position);

        playerNumber++;

        if (playerNumber == 4) {
            playerNumber = 0;
        }

        for (int x = 1; x < 4; x++) {
            pause();
            if (playerNumber < humanPlayers) {
                Card[] hand =  players[playerNumber].getCards();
                boolean hasClubs = false;
                boolean playedLegalCard = false;
                for (Card card : hand) {
                    if (card.getSuit().equals("Clubs")) {
                        hasClubs = true;
                        break;
                    }
                }
                System.out.println();
                System.out.println(players[playerNumber].getName() + ", it is your turn.");
                pause();
                System.out.println(players[playerNumber].getName() + ", what card do you want to play?");
                players[playerNumber].showPlayerCards();
                while (!playedLegalCard) {
                    String response = input.nextLine();
                    if (response.equalsIgnoreCase("Rules")) {
                        openRules();
                        players[playerNumber].showPlayerCards();
                        System.out.println(players[playerNumber].getName() + ", what card do you want to play?");
                    } else if (response.equalsIgnoreCase("Points") && (roundNumber > 1)) {
                        openPoints();
                        players[playerNumber].showPlayerCards();
                        System.out.println(players[playerNumber].getName() + ", what card do you want to play?");
                    } else if (response.matches("\\d+")) {
                        position = Integer.parseInt(response) - 1;
                        if (position > players[playerNumber].getCards().length) {
                            System.out.println("You don't have a card in that position. Please choose another card");
                        }
                        else if (!hand[position].getSuit().equals("Clubs") && hasClubs) {
                            System.out.println("You must play a Clubs!");
                        } else {
                            playedLegalCard = true;
                        }
                    }
                }
                System.out.println("You have chosen to play the " + hand[position].getString());
                cardsInThePile[x] = hand[position];
                players[playerNumber].receiveCard(null, (position));
                playerNumber++;

            } else {
                Card[] hand =  players[playerNumber].getCards();
                cardsInThePile[x] = whatToPlay("Clubs", playerNumber);
                position = players[playerNumber].getCardPosition(cardsInThePile[x]);
                System.out.println(players[playerNumber].getName() + " has played the " + hand[position].getString());
                players[playerNumber].receiveCard(null, position);
                playerNumber++;
                if (playerNumber == 4) {
                    playerNumber = 0;
                }
            }
        }

        position = whoHasWon(cardsInThePile, "Clubs");
        int winningPlayer = (position) + (playerNumber + 1);
        if (winningPlayer > 4) {
            winningPlayer = winningPlayer % 4;
        }
        System.out.println(players[winningPlayer - 1].getName() + " has won this round with the " + cardsInThePile[position].getString());
        for (Card card : cardsInThePile) {
            players[winningPlayer - 1].addToWinningPile(card);
        }
        Arrays.fill(cardsInThePile, null);
        System.out.println();
        return winningPlayer - 1;
    }

    public static int trick(int startingPlayer) { //performs any subsequent tricks based on the winner of the last Trick
        int playerNumber = startingPlayer;
        int position = -1;
        Card[] hand =  players[playerNumber].getCards();
        String suit;
        String[] ranks = {"Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two",};

        System.out.println(players[playerNumber].getName() + " is going first.");
        pause();
        if (playerNumber < humanPlayers) {
            hand = players[playerNumber].getCards();
            boolean playedLegalCard = false;
            players[playerNumber].showPlayerCards();
            System.out.println("What card do you want to play?");
            while (!playedLegalCard) {
                String response = input.nextLine();
                if (response.equalsIgnoreCase("Rules")) {
                    openRules();
                    players[playerNumber].showPlayerCards();
                    System.out.println("What card do you want to play?");
                } else if (response.equalsIgnoreCase("Points") && (roundNumber > 1)) {
                    openPoints();
                    players[playerNumber].showPlayerCards();
                    System.out.println("What card do you want to play?");
                } else if (response.matches("\\d+")) {
                    position = (Integer.parseInt(response) - 1);
                    if (hand[position] == null) {
                        System.out.println("You don't have a card in the position. Please choose another card");
                    } else if (hand[position].getSuit().equals("Hearts") && !heartsBroken) {
                        System.out.println("You can't play that card, Heart's has not been broken.");
                    } else {
                        playedLegalCard = true;
                    }
                }
            }
        } else {
            for (int x = 0; x < ranks.length; x++) {
                for (int y = 0; y < hand.length; y++) {
                    while (hand[y] == null) {
                        if (hand[y] == null) {
                            y++;
                        }
                        if (y == 13) {
                            break;
                        }
                    }
                    if (y == 13) {
                        break;
                    }

                    if (!heartsBroken) {
                        if (hand[y].getRank().equals(ranks[x]) && !hand[y].getSuit().equals("Hearts")) {
                            position = y;
                            break;
                        }
                    } else {
                        if (hand[y].getRank().equals(ranks[x])) {
                            position = y;
                            break;
                        }
                        if (position > 0) {
                            break;
                        }
                    }
                    if (position > 0) {
                        break;
                    }
                }
                if (position > 0) {
                    break;
                }
            }
        }
        cardsInThePile[0] = hand[position];
        suit = hand[position].getSuit();
        if (playerNumber < humanPlayers) {
            System.out.println("You have chosen to play the " + hand[position].getString());
        } else {
            System.out.println(players[playerNumber].getName() + " has played the " + hand[position].getString());
        }

      pause();

        players[playerNumber].receiveCard(null, position);
        playerNumber++;
        if (playerNumber == 4) {
            playerNumber = 0;
        }


        for (int x = 1; x < players.length; x++) {
            if (playerNumber < humanPlayers) {
                hand = players[playerNumber].getCards();
                boolean hasSuit = false;
                boolean playedLegalCard = false;
                for (int y = 0; y < hand.length; y++) {
                    while (hand[y] == null) {
                        if (hand[y] == null) {
                            y++;
                        }
                        if (y == 13) {
                            break;
                        }
                    }
                    if (y == 13) {
                        break;
                    }
                    if (hand[y].getSuit().equals(suit)) {
                        hasSuit = true;
                        break;
                    }
                }
                System.out.println(players[playerNumber].getName() + ", it is your turn.");
                System.out.println();
                pause();
                players[playerNumber].showPlayerCards();
                System.out.println(players[playerNumber].getName() + ", what card do you want to play?");
                while (!playedLegalCard) {
                    String response = input.nextLine();
                    if (response.equalsIgnoreCase("Rules")) {
                        openRules();
                        players[playerNumber].showPlayerCards();
                        System.out.println(players[playerNumber].getName() + ", what card do you want to play?");
                    } else if (response.equalsIgnoreCase("Points") && (roundNumber > 1)) {
                        openPoints();
                        players[playerNumber].showPlayerCards();
                        System.out.println("What card do you want to play?");
                    } else if (response.matches("\\d+")) {
                        position = (Integer.parseInt(response) - 1);
                        if (hand[position] == null) {
                            System.out.println("You don't have a card in the position. Please choose another card");
                        } else if (!hand[position].getSuit().equals(suit) && hasSuit) {
                            System.out.println("You must play a " + suit + "!");
                        } else {
                            playedLegalCard = true;
                        }
                    }
                }
                System.out.println("You have chosen to play the " + hand[position].getString());
                cardsInThePile[x] = hand[position];
                players[playerNumber].receiveCard(null, (position));
                playerNumber++;

            } else {
                hand = players[playerNumber].getCards();
                cardsInThePile[x] = whatToPlay(suit, playerNumber);
                position = players[playerNumber].getCardPosition(cardsInThePile[x]);
                System.out.println(players[playerNumber].getName() + " has played the " + hand[position].getString());
                pause();
                players[playerNumber].receiveCard(null, position);
                playerNumber++;
                if (playerNumber == 4) {
                    playerNumber = 0;
                }
            }
        }

        position = whoHasWon(cardsInThePile, suit);
        int winningPlayer = (position) + (playerNumber + 1);
        if (winningPlayer > 4) {
            winningPlayer = winningPlayer % 4;
        }
        System.out.println(players[winningPlayer - 1].getName() + " has won this round with the " + cardsInThePile[position].getString());
        for (Card card : cardsInThePile) {
            players[winningPlayer - 1].addToWinningPile(card);
        }
        Arrays.fill(cardsInThePile, null);
        System.out.println();
        return winningPlayer - 1;
    }

    public static Card whatToPlay(String suit, int playerNumber) { //determines which card the CPU will play during a Trick
        Card[] hand =  players[playerNumber].getCards();
        String[] ranks = {"Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two",};
        for (int x = 0; x < 13; x++) {
            for (int y = 0; y < hand.length; y++) {
                while (hand[y] == null) {
                    if (hand[y] == null) {
                        y++;
                    }
                    if (y == 13) {
                        break;
                    }
                }
                if (y == 13) {
                    break;
                }

                if (hand[y].getSuit().equals(suit) && hand[y].getRank().equals(ranks[x])) {
                    return hand[y];
                }
            }
        }

        for (int x = 0; x < 13; x++) {
            for (int y = 0; y < hand.length; y++) {
                while (hand[y] == null) {
                    if (hand[y] == null) {
                        y++;
                    }
                    if (y == 13) {
                        break;
                    }
                }

                if (y == 13) {
                    break;
                }

                if (hand[y].getSuit().equals("Hearts") && hand[y].getRank().equals(ranks[x])) {
                    heartsBroken = true;
                    return hand[y];
                }
            }
        }

        for (int x = 0; x < 13; x++) {
            for (int y = 0; y < hand.length; y++) {
                while (hand[y] == null) {
                    if (hand[y] == null) {
                        y++;
                    }
                    if (y == 13) {
                        break;
                    }
                }
                if (y == 13) {
                    break;
                }
                if ((hand[y].getSuit().equals("Diamonds") || hand[y].getSuit().equals("Spades")) && hand[y].getRank().equals(ranks[x]) || hand[y].getSuit().equals("Clubs")) {
                    return hand[y];
                }
            }
        }

        return null;
    }

    public static int whoHasWon(Card[] cardsInThePile, String suit) { //determines which card has won after a Trick
        int position = -1;
        String[] ranks = {"Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two",};
        for (int x = 0; x < ranks.length; x++) {
            for (int y = 0; y < cardsInThePile.length; y++) {
                if ((cardsInThePile[y].getRank().equals(ranks[x])) && cardsInThePile[y].getSuit().equals(suit)) {
                    position = y;
                    return position;
                }
            }
        }
        return position;
    }

    public static void countPoints () { //Counts up points after the Trick
        boolean shotTheMoon = false;
        int x = 0;
        System.out.println("All the cards have been dealt, it's time to count up points.");
        System.out.println();

        for (Player player : players) {
            Card[] cardsWon = player.getCardsWon();
            player.showWinningPile(cardsWon);
        }

        System.out.println();

        for (Player player : players) {
            Card[] cardsWon = player.getCardsWon();
            int numOfHearts = 0;
            for (Card card : cardsWon) {
                if (card.getSuit().equals("Hearts")) {
                    player.addPoints(1);
                    numOfHearts++;
                }

                if (card.getSuit().equals("Spades") && card.getRank().equals("Queen")) {
                    player.addPoints(13);
                    numOfHearts++;
                }
                player.removeFromWinningPile(0);
            }

            if (shotTheMoon) {
                player.addPoints(26);
            }

            if (numOfHearts == 14) {
                player.removePoints(13);
                shotTheMoon = true;
            }

            points[x] = player.getNumOfPoints();
            x++;
        }

        for (int y = 0; y < points.length; y++) {
            System.out.println(players[y].getName() + " has " + points[y] + " points.");
        }

        addToPoints();

        for (x = 0; x < points.length; x++) {
            if (points[x] >= pointLimit) {
                overPoints = true;
            }
        }

        if (overPoints) {
            int minValue = 1000;
            int winnerPlayer = -1;
            for (int y = 0; y < points.length; y++) {
                if (points[y] < minValue) {
                    minValue = points[y];
                    winnerPlayer = y;
                }
            }

            System.out.println(players[winnerPlayer].getName() + " has won the game with the lowest score of " + minValue + " points!");

        }
    }

    static void openRules() {
        boolean continueReading = true;
        while (continueReading) {
            try {
                FileReader reader = new FileReader("src/Rules.txt");
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            String response = input.nextLine();
            if (response.equalsIgnoreCase("Overview")) {
                try {
                    FileReader reader = new FileReader("src/Overview.txt");
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                    }
                    reader.close();
                    System.out.println();
                    pause();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (response.equalsIgnoreCase("Starting Off")) {
                try {
                    FileReader reader = new FileReader("src/Starting Off.txt");
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                    }
                    reader.close();
                    System.out.println();
                    pause();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (response.equalsIgnoreCase("Passing Cards")) {
                try {
                    FileReader reader = new FileReader("src/Passing Cards.txt");
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                    }
                    reader.close();
                    System.out.println();
                    pause();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (response.equalsIgnoreCase("Tricks")) {
                try {
                    FileReader reader = new FileReader("src/Tricks.txt");
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                    }
                    reader.close();
                    System.out.println();
                    pause();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (response.equalsIgnoreCase("Scoring Points")) {
                try {
                    FileReader reader = new FileReader("src/Scoring Points.txt");
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                    }
                    reader.close();
                    System.out.println();
                    pause();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (response.equalsIgnoreCase("Exit")) {
                continueReading = false;
            }
        }
    }

    static void pause() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void addToPoints() {
        try {
            FileWriter writer = new FileWriter("src/Points.txt", true);
            writer.write("In Round #" + roundNumber + " - " + players[0].getName() + ": " + points[0] + " points. " + players[1].getName() + ": " + points[1] + " points. " + players[2].getName() + ": " + points[2] + " points. " + players[3].getName() + ": " + points[3] + " points.");
            writer.write("\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void openPoints() {
        try {
            FileReader reader = new FileReader("src/Points.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
