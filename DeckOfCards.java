import java.util.*;

public class DeckOfCards {
    private static final int size = 52;
    private static Card[] deckOfCards = new Card[size];

    static Card[] getDeckOfCards() {
        int count = 0;

        String[] suits = {"Diamonds", "Clubs", "Hearts", "Spades"};
        String[] ranks = {"Ace", "King", "Queen", "Jack", "Ten", "Nine", "Eight", "Seven", "Six", "Five", "Four", "Three", "Two",};

        for (String s : suits) {
            for (String r : ranks) {
                Card card = new Card(s, r);
                deckOfCards[count] = card;
                count++;
            }
        }
        return deckOfCards;
    }

    static Card[] shuffleCards(Card[] deckOfCards) {
        Random rand = new Random();
        int j;
        for (int i = 0; i < size; i++) {
            j = rand.nextInt(size);
            Card temp = deckOfCards[i];
            deckOfCards[i] = deckOfCards[j];
            deckOfCards[j] = temp;
        }
        return deckOfCards;
    }

    static Player[] dealCards(Player[] players, Card[] deck) {
        int numOfCardsPerPlayer = deck.length / players.length;
        for (int i = 0; i < deck.length; i++) {
            int positionInHand = i % numOfCardsPerPlayer;
            players[i % players.length].receiveCard(deck[i], positionInHand);
        }

        return players;
    }
}
