import jdk.swing.interop.SwingInterOpUtils;

import java.util.*;

public class Player {
    private String name;
    private Card[] cards = new Card[13];
    private Card[] giveAway = new Card[3];
    private int numOfPoints;
    private int[] giveAwayPosition = new int[3];
    ArrayList<Card> cardsWon = new ArrayList<>();
    private String cardsToTrade;

    Player(String name){
        this.name = name;
    }
    void showPlayerCards(){
        System.out.println("---------------------------------------------");
        int x = 1;
        for (Card card : cards) {
            if (card == null) {
                System.out.println(x + ": N/A");
                x++;
            } else {
                System.out.printf("%d: %s of %s\n", x, card.rank, card.suit);
                x++;
            }
        }
        System.out.println("---------------------------------------------");
    }

    String showGivenAwayCards() {
        cardsToTrade = "the " + giveAway[0].getString() + ", the " + giveAway[1].getString() + ", and the " +  giveAway[2].getString();
        return cardsToTrade;
    }
    void receiveCard(Card card, int position){
        cards[position] = card;
    }

    void giveAwayCard(Card card, int position) {
        giveAway[position] = card;
    }

    void giveAwayCardPosition(int cardPosition, int arrayPosition) {
        giveAwayPosition[arrayPosition] = cardPosition;
    }

    void addToWinningPile(Card card) {
        cardsWon.add(card);
    }

    void removeFromWinningPile(int pos) {
        cardsWon.remove(pos);
    }

    int getCardPosition(Card card) {
        Card search = card;
        for (int x = 0; x < cards.length; x++) {
            if (cards[x] == search) {
                return x;
            }
        }
        return -1;
    }


    String getName(){
        return name;
    }

    Card[] getCards() {
        return cards;
    }

    Card[] getGiveAway() {
        return giveAway;
    }

    int[] getGiveAwayPosition() {
        return giveAwayPosition;
    }

    void addPoints(int points) {
        numOfPoints = numOfPoints + points;
    }

    void removePoints(int points) {
        numOfPoints = numOfPoints - points;
    }

    int getNumOfPoints() {
        return numOfPoints;
    }

    Card[] getCardsWon() {
        Card[] winnings = new Card[cardsWon.size()];
        for (int x = 0; x < winnings.length; x++) {
            winnings[x] = cardsWon.get(x);
        }
        return winnings;
    }

    void showWinningPile(Card[] winningPile) {
        System.out.print(name + " has won");
        for (Card card : winningPile) {
            System.out.printf(", the %s of %s", card.rank, card.suit);
        }
        System.out.println();
    }

}
