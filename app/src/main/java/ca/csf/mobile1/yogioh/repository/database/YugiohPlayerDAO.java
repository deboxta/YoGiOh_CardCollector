package ca.csf.mobile1.yogioh.repository.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohPlayer;

@Dao
public interface YugiohPlayerDAO
{
    @Query("SELECT * FROM yugiohplayer")
    List<YugiohPlayer> selectAll();

    @Query("SELECT * FROM yugiohplayer WHERE id IN(:playersId)")
    List<YugiohPlayer> FindAllByIds(long[] playersId);

    @Insert
    long insertOne(YugiohPlayer yugiohPlayer);

    @Delete
    void delete(YugiohPlayer yugiohPlayer);

}
