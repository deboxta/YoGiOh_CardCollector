package ca.csf.mobile1.yogioh.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class YugiohCard
{
    public static final String EMPTY_STRING = "";
    public static final int VALUE_ZERO = 0;

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "card_name")
    public String cardName;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "attribute")
    public String attribute;

    @ColumnInfo(name = "monster_type")
    public String monsterType;

    @ColumnInfo(name = "nb_Stars")
    public int nbStars;

    @ColumnInfo(name = "description")
    public String cardDescription;

    @ColumnInfo(name = "card_attack")
    public int cardAttack;

    @ColumnInfo(name = "card_defense")
    public int cardDefense;

    public YugiohCard()
    {
        cardName = EMPTY_STRING;
        type = EMPTY_STRING;
        attribute = EMPTY_STRING;
        monsterType = EMPTY_STRING;
        nbStars = VALUE_ZERO;
        cardDescription = EMPTY_STRING;
        cardAttack = VALUE_ZERO;
        cardDefense = VALUE_ZERO;
    }

    public YugiohCard(int id, String cardName, String type, String attribute, String monsterType, int nbStars, String cardDescription, int cardAttack, int cardDefense)
    {
        this.id = id;
        this.cardName = cardName;
        this.type = type;
        this.attribute = attribute;
        this.monsterType = monsterType;
        this.nbStars = nbStars;
        this.cardDescription = cardDescription;
        this.cardAttack = cardAttack;
        this.cardDefense = cardDefense;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YugiohCard card = (YugiohCard) o;
        return id == card.id &&
                nbStars == card.nbStars &&
                cardAttack == card.cardAttack &&
                cardDefense == card.cardDefense &&
                cardName.equals(card.cardName) &&
                type.equals(card.type) &&
                Objects.equals(attribute, card.attribute) &&
                Objects.equals(monsterType, card.monsterType) &&
                cardDescription.equals(card.cardDescription);
    }
}
