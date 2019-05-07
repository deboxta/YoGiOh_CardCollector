package ca.csf.mobile1.yogioh.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class YugiohCard
{
    //TODO: Move to test eventually.
    private static final String DEFAULT_DESCRIPTION = "This legendary dragon is a powerful engine of destruction. Virtually invincible, very few have faced this awesome creature and lived to tell the tale";
    private static final String DEFAULT_NAME = "Blue-Eyes White Dragon";
    private static final int DEFAULT_ATTACK_VALUE = 3000;
    private static final int DEFAULT_DEFENSE_VALUE = 2500;

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
        cardName = DEFAULT_NAME;
        type = CardTypes.Monster.toString();
        attribute = CardAttributes.Light.toString();
        monsterType = MonsterTypes.Dragon.toString();
        nbStars = 8;
        cardDescription = DEFAULT_DESCRIPTION;
        cardAttack = DEFAULT_ATTACK_VALUE;
        cardDefense = DEFAULT_DEFENSE_VALUE;
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
