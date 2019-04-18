package ca.csf.mobile1.yogioh.repository.Objects.card;

public class CardTable {
    public static final String CREATE_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS card(" +
            "card_no      INTEGER     PRIMARY KEY     NOT NULL," +
            "card_name    TEXT                        NOT NULL," +
            "card_element TEXT                        NOT NULL," +
            "card_level   REAL                        NOT NULL" +
            "card_type    TEXT                        NOT NULL" +
            "card_attack  REAL                        NOT NULL" +
            "card_defence REAL                        NOT NULL" +
            ");";
    public  static  final String DROP_TABLE = "" +
            "DROP TABLE IF EXISTS card;";
}
