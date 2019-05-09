package ca.csf.mobile1.yogioh.repository.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohCard;

@Dao
public interface YugiohCardDAO
{
    @Query("SELECT * FROM yugiohcard")
    List<YugiohCard> selectAll();

    @Query("SELECT * FROM yugiohcard WHERE id IN(:cardsID)")
    List<YugiohCard> findAllByIds(long[] cardsID);

    @Query("SELECT * FROM yugiohcard WHERE card_name IN(:cardName)")
    YugiohCard findCardByName(String cardName);

    @Query("SELECT * FROM yugiohcard WHERE card_name IN(:cardType)")
    List<YugiohCard>findCardByType(String cardType);

    @Insert
    long[] insertAll(YugiohCard...yugiohCards);

    @Insert
    long[] insertAll(List<YugiohCard> yugiohCardList);

    @Insert
    long insertOne(YugiohCard yugiohCards);

    @Delete
    void delete(YugiohCard yugiohCard);


}
