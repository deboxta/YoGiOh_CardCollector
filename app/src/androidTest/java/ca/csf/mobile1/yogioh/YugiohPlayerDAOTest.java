package ca.csf.mobile1.yogioh;


import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.repository.database.YugiohPlayerDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class YugiohPlayerDAOTest
{
    private static final String TEST_DEFAULT_PLAYER_USERNAME = "plucTheMachine";
    private static final String TEST_DEFAULT_PLAYER_NAME = "Pierre-Luc Maltais";

    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohDatabase db;

    @Before
    public void createDb()
    {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, YugiohDatabase.class).build();
        yugiohPlayerDAO = db.yugiohPlayerDAO();
    }

    @After
    public void closeDb() throws IOException
    {
        db.close();
    }

    @Test
    public void readOnNull()
    {
        List<Object> empty = new ArrayList<>();
        assertEquals(empty, yugiohPlayerDAO.selectAll());
    }

    @Test
    public void writePlayerAndReadAllInList()
    {
        YugiohPlayer yugiohPlayer = new YugiohPlayer();
        generateDefaultPlayer(yugiohPlayer);
        long newId = yugiohPlayerDAO.insertOne(yugiohPlayer);
        yugiohPlayer.id = (int) newId;
        assertEquals(yugiohPlayer, yugiohPlayerDAO.selectAll().get(0));
    }

    @Test
    public void removePlayerFromList()
    {
        YugiohPlayer yugiohPlayer = new YugiohPlayer();
        generateDefaultPlayer(yugiohPlayer);
        long newId = yugiohPlayerDAO.insertOne(yugiohPlayer);
        yugiohPlayer.id = (int) newId;
        yugiohPlayerDAO.delete(yugiohPlayer);
        assertEquals(0, yugiohPlayerDAO.selectAll().size());
    }

    @Test
    public void findPlayerById()
    {
        YugiohPlayer yugiohPlayer = new YugiohPlayer();
        generateDefaultPlayer(yugiohPlayer);
        long newId = yugiohPlayerDAO.insertOne(yugiohPlayer);
        yugiohPlayer.id = (int) newId;

        YugiohPlayer yugiohPlayer1 = new YugiohPlayer();
        generateDefaultPlayer(yugiohPlayer1);
        newId = yugiohPlayerDAO.insertOne(yugiohPlayer1);
        yugiohPlayer1.id = (int) newId;

        long[] ids = {yugiohPlayer.id};
        List<YugiohPlayer> foundPlayer = yugiohPlayerDAO.FindAllByIds(ids);

        assertEquals(yugiohPlayer, foundPlayer.get(0));
    }

    private void generateDefaultPlayer(YugiohPlayer yugiohPlayer)
    {
        yugiohPlayer.name = TEST_DEFAULT_PLAYER_NAME;
        yugiohPlayer.playerUserName = TEST_DEFAULT_PLAYER_USERNAME;
    }


}
