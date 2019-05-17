package ca.csf.mobile1.yogioh.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import ca.csf.mobile1.yogioh.util.ConstantsUtil;

@Entity
public class YugiohPlayer
{


    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_name")
    public String playerUserName;

    @ColumnInfo(name = "name")
    public String name;


    /**
     * Default constructor of the YugiohPlayer Class.
     */
    public YugiohPlayer()
    {
        playerUserName = ConstantsUtil.EMPTY_STRING;
        name = ConstantsUtil.EMPTY_STRING;
    }


    /**
     * Constructor of the YugiohPlayer class. There should not be more then 1 player as the feature is not supported.
     *
     * @param id                Id of the player (should always be 1)
     * @param playerUserName    The username of the player.
     * @param name              The real name of the player.
     */
    public YugiohPlayer(int id, String playerUserName, String name)
    {
        this.id = id;
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
