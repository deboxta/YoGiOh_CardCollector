package ca.csf.mobile1.yogioh.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"card_id", "player_id"},foreignKeys = {
        @ForeignKey(entity = YugiohPlayer.class,
                parentColumns = "id",
                childColumns = "player_id"
        ),
        @ForeignKey(entity = YugiohCard.class,
                parentColumns = "id",
                childColumns = "card_id")
})
public class YugiohDeckCard
{
    @ColumnInfo(name = "player_id")
    public int playerId;

    @ColumnInfo(name = "card_id")
    public int cardId;

    @ColumnInfo(name = "amount_owned")
    public int amountOwned;
}
