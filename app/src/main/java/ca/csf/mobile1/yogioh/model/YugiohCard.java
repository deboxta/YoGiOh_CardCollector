package ca.csf.mobile1.yogioh.model;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class YugiohCard
{

    private static final String DEFAULTPATH = "res/mipmap/blue_eyes_dragon.png";
    private static final String DEFAULTDESCRIPTION = "This legendary dragon is a powerful engine of destruction. Virtually invincible, very few have faced this awesome creature and lived to tell the tale";
    private static final String DEFAULTNAME = "Blue-Eyes White Dragon";
    private static final int DEFAULTATTACKVALUE = 3000;
    private static final int DEFAULTDEFENSEVALUE = 2500;

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

    @ColumnInfo(name = "card_picture", typeAffinity = ColumnInfo.BLOB)
    public byte[] cardPicture;

    @ColumnInfo(name = "description")
    public String cardDescription;

    @ColumnInfo(name = "card_attack")
    public int cardAttack;

    @ColumnInfo(name = "card_defense")
    public int cardDefense;

    @RequiresApi(api = Build.VERSION_CODES.O)

    public YugiohCard()
    {
        cardName = DEFAULTNAME;
        type = CardTypes.Monster.toString();
        attribute = CardAttributes.Light.toString();
        monsterType = MonsterTypes.Dragon.toString();
        nbStars = 8;
        try
        {
            cardPicture = Files.readAllBytes(new File(DEFAULTPATH).toPath());
        } catch (IOException e)
        {
            cardPicture = null;
            e.printStackTrace();
        }
        cardDescription = DEFAULTDESCRIPTION;
        cardAttack = DEFAULTATTACKVALUE;
        cardDefense = DEFAULTDEFENSEVALUE;
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
                Arrays.equals(cardPicture, card.cardPicture) &&
                cardDescription.equals(card.cardDescription);
    }
}
