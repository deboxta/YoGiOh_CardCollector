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


    /**
     * Default constructor of the YugiohDeckCard.
     */
    public YugiohDeckCard()
    {
        playerId = 1;
        cardId = 1;
        amountOwned = 1;
    }

    /**
     * Constructor of the YugiohDeckCard class with arguments.
     *
     * @param playerId      The id of the player (it should always be )
     * @param cardId        The id of the card that is to be added to the player's deck.
     * @param amountOwned   The amount of that card the player owns (by default it should always be 1).
     */
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
}
