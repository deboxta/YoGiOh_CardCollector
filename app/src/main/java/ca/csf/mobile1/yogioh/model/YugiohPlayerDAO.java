package ca.csf.mobile1.yogioh.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface YugiohPlayerDAO
{
    @Query("SELECT * FROM yugiohplayer")
    List<YugiohPlayer> selectAll();

    @Query("SELECT * FROM yugiohplayer WHERE id IN(:playersId)")
    List<YugiohPlayer> FindAllByIds(long[] playersId);

    @Query("SELECT * FROM yugiohplayer WHERE user_name IN(:username)")
    List<YugiohPlayer> findByUsername(String username);

    @Query("SELECT * FROM yugiohplayer WHERE name IN(:playerName)")
    List<YugiohPlayer> findByName(String playerName);

    @Insert
    long[] insertAll(YugiohPlayer...yugiohPlayers);

    @Insert
    long insertOne(YugiohPlayer yugiohPlayer);

    @Delete
    void delete(YugiohPlayer yugiohPlayer);

}
