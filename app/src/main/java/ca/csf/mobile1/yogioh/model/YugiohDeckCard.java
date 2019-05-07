package ca.csf.mobile1.yogioh.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import java.util.Objects;

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

    public YugiohDeckCard()
    {

    }

    public YugiohDeckCard(int playerId, int cardId, int amountOwned)
    {
        this.playerId = playerId;
        this.cardId = cardId;
        this.amountOwned = amountOwned;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YugiohDeckCard that = (YugiohDeckCard) o;
        return playerId == that.playerId &&
                cardId == that.cardId &&
                amountOwned == that.amountOwned;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(playerId, cardId, amountOwned);
    }
}
