package ca.csf.mobile1.yogioh.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class YugiohPlayer
{

    public static final String DEFAULTPLAYERUSERNAME = "plucTheMachine";
    public static final String DEFAULTUSERNAME = "Pierre-Luc Maltais";
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_name")
    public String playerUserName;

    @ColumnInfo(name = "name")
    public String name;

    public YugiohPlayer()
    {
        playerUserName = DEFAULTPLAYERUSERNAME;
        name = DEFAULTUSERNAME;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YugiohPlayer that = (YugiohPlayer) o;
        return id == that.id &&
                playerUserName.equals(that.playerUserName) &&
                name.equals(that.name);
    }
}
