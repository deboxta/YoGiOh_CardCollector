package ca.csf.mobile1.yogioh.repository.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohDeckCard;

@Dao
public interface YugiohDeckDAO
{
    @Query("SELECT * FROM YugiohDeckCard WHERE player_id IN(:playerId)")
    List<YugiohDeckCard> selectAll(int playerId);

    @Query("SELECT * FROM YugiohDeckCard WHERE card_id IN(:cardId) AND player_id IN(:playerId)")
    YugiohDeckCard selectOwnedCardById(int cardId, int playerId);

    @Insert
    long insertOne(YugiohDeckCard yugiohDeckCard);

    @Insert
    long[] insertAll(YugiohDeckCard...yugiohDeckCards);

    @Update
    void updateCardAmount(YugiohDeckCard yugiohDeckCard);

    @Delete
    void delete(YugiohDeckCard yugiohDeckCard);
}
