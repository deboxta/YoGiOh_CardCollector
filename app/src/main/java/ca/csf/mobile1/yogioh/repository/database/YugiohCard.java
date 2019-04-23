package ca.csf.mobile1.yogioh.repository.database;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import ca.csf.mobile1.yogioh.repository.database.Enum.CardAttributes;
import ca.csf.mobile1.yogioh.repository.database.Enum.CardTypes;
import ca.csf.mobile1.yogioh.repository.database.Enum.MonsterTypes;

@Entity
public class YugiohCard
{

    public static final String DEFAULTPATH = "res/mipmap/Blue-Eyes-Dragon.png";
    public static final String DEFAULTDESCRIPTION = "This legendary dragon is a powerful engine of destruction. Virtually invincible, very few have faced this awesome creature and lived to tell the tale";
    public static final String DEFAULTNAME = "Blue-Eyes White Dragon";
    public static final int DEFAULTATTACKVALUE = 3000;
    public static final int DEFAULTDEFENSEVALUE = 2500;

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
        }
        catch (IOException e)
        {
            cardPicture = null;
            e.printStackTrace();
        }
        cardDescription = DEFAULTDESCRIPTION;
        cardAttack = DEFAULTATTACKVALUE;
        cardDefense = DEFAULTDEFENSEVALUE;
    }

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
    public float nbStars;

    @ColumnInfo(name = "card_picture", typeAffinity = ColumnInfo.BLOB)
    public byte[] cardPicture;

    @ColumnInfo(name = "description")
    public String cardDescription;

    @ColumnInfo(name = "card_attack")
    public int cardAttack;

    @ColumnInfo(name = "card_defense")
    public int cardDefense;

}
