package ca.csf.mobile1.yogioh.repository.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface YugiohDAO
{
    @Query("SELECT * FROM yugiohcard")
    List<YugiohCard> selectAll();

    @Query("SELECT * FROM yugiohcard WHERE id IN(:cardsID)")
    List<YugiohCard> loadAllByIds(int[] cardsID);

    @Insert
    void insertAll(YugiohCard...yugiohCards);

    @Delete
    void delete(YugiohCard yugiohCard);


}
