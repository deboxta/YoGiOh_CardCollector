package ca.csf.mobile1.yogioh.repository.Objects.deck;

public class DeckTable {
    public static final String CREATE_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS deck(" +
            "deck_no      INTEGER     PRIMARY KEY     NOT NULL," +
            "deck_card_no TEXT                        NOT NULL," +
            "FOREIGN KEY(deck_card_no) REFERENCES artist(card_no)" +
            ");";
    public  static  final String DROP_TABLE = "" +
            "DROP TABLE IF EXISTS deck;";
}
