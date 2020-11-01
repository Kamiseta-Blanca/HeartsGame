import java.util.*;

public class Card {
    String suit;
    String rank;

    Card(String cardSuit, String cardRank) {
        this.suit = cardSuit;
        this.rank = cardRank;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public String getString() {
       String card = this.rank + " of " + this.suit;
       return card;
    }
}
