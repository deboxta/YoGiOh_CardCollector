package ca.csf.mobile1.yogioh.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

import ca.csf.mobile1.yogioh.util.ConstantsUtil;

@Entity
public class YugiohCard
{
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

        cardName = ConstantsUtil.EMPTY_STRING;
        type = ConstantsUtil.EMPTY_STRING;
        attribute = ConstantsUtil.EMPTY_STRING;
        monsterType = ConstantsUtil.EMPTY_STRING;
        nbStars = ConstantsUtil.VALUE_ZERO;
        cardDescription = ConstantsUtil.EMPTY_STRING;
        cardAttack = ConstantsUtil.VALUE_ZERO;
        cardDefense = ConstantsUtil.VALUE_ZERO;
    }

    /**
     * Constructor of the YugiohCard class that takes arguments.
     *
     * @param id                The id of the card
     * @param cardName          The name of the card
     * @param type              The type of the card (Monster, Spell, Trap)
     * @param attribute         The attribute of the card (Dark, Divine, Earth, Fire, Light, Water or Wind),
     *                          send argument "null" if card type is Spell or Trap
     * @param monsterType       The type of the monster chosen from (Aqua, Beast, Beast-Warrior, Creator God, Cyberse, Dinosaur, Divine-Beast, Dragon,
     *                          Fairy, Fiend, Fish, Insect, Machine, Plant, Psychic, Pyro, Reptile, Rock, Sea Serpent, SpellCaster, Thunder,
     *                          Warrior, Winged Beast, Wyrm or Zombie), send argument "null" if card type is Spell or Trap.
     * @param nbStars           The number of star/level of the monster. send -1 if card type is a spell or a  trap.
     * @param cardDescription   Description of the card.
     * @param cardAttack        Damage of the card represented by a int. Send -1 if the card type is a spell or a trap.
     * @param cardDefense       Defense of the card represented by a int. Send -1 if the card type is a spell or a trap.
     */
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
