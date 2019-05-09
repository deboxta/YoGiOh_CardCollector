package ca.csf.mobile1.yogioh.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class YugiohPlayer
{


    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_name")
    public String playerUserName;

    @ColumnInfo(name = "name")
    public String name;

    public YugiohPlayer()
    {
        playerUserName = YugiohCard.EMPTY_STRING;
        name = YugiohCard.EMPTY_STRING;
    }

    public YugiohPlayer(int id, String playerUserName, String name)
    {
        this.playerUserName = playerUserName;
        this.name = name;
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
