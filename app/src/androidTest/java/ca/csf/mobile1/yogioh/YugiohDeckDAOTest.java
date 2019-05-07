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
import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;
import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.model.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.model.YugiohPlayerDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class YugiohDeckDAOTest
{

    private YugiohDeckDAO yugiohDeckDAO;
    private YugiohPlayerDAO yugiohPlayerDAO;
    private YugiohCardDAO yugiohCardDAO;
    private YugiohDatabase db;

    @Before
    public void createDb()
    {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, YugiohDatabase.class).build();
        yugiohDeckDAO = db.yugiohDeckDAO();
        yugiohCardDAO = db.yugiohCardDao();
        yugiohPlayerDAO = db.yugiohPlayerDAO();

    }

    @After
    public void closeDb() throws IOException
    {
        db.close();
    }

    @Test
    public void addCardInDeckAndRead()
    {
        YugiohPlayer yugiohPlayer = new YugiohPlayer();
        yugiohPlayer.id = (int)yugiohPlayerDAO.insertOne(yugiohPlayer);

        YugiohCard yugiohCard = new YugiohCard();
        yugiohCard.id = (int)yugiohCardDAO.insertOne(yugiohCard);

        YugiohDeckCard yugiohDeckCard  = new YugiohDeckCard();
        yugiohDeckCard.cardId = yugiohCard.id;
        yugiohDeckCard.playerId = yugiohPlayer.id;
        yugiohDeckCard.amountOwned = 1;

        yugiohDeckDAO.insertOne(yugiohDeckCard);

        List<YugiohDeckCard> yugiohDeckCardList = yugiohDeckDAO.selectAll(1);

        assertEquals(yugiohDeckCard,yugiohDeckCardList.get(0));
    }

    @Test
    public void removeCardFromPlayerDeck()
    {
        YugiohPlayer yugiohPlayer = new YugiohPlayer();
        yugiohPlayer.id = (int)yugiohPlayerDAO.insertOne(yugiohPlayer);

        YugiohCard yugiohCard = new YugiohCard();
        YugiohCard yugiohCard1 = new YugiohCard();
        yugiohCard.id = (int)yugiohCardDAO.insertOne(yugiohCard);
        yugiohCard1.id = (int)yugiohCardDAO.insertOne(yugiohCard1);

        YugiohDeckCard yugiohDeckCard  = new YugiohDeckCard();
        yugiohDeckCard.cardId = yugiohCard.id;
        yugiohDeckCard.playerId = yugiohPlayer.id;
        yugiohDeckCard.amountOwned = 1;

        YugiohDeckCard yugiohDeckCard1 = new YugiohDeckCard();
        yugiohDeckCard1.cardId = yugiohCard1.id;
        yugiohDeckCard1.playerId = yugiohPlayer.id;
        yugiohDeckCard1.amountOwned = 1;

        YugiohDeckCard[] yugiohDeckCardsArray = {yugiohDeckCard,yugiohDeckCard1};

        yugiohDeckDAO.insertAll(yugiohDeckCardsArray);

        yugiohDeckDAO.delete(yugiohDeckCard1);

        assertEquals(1,yugiohDeckDAO.selectAll(1).size());
    }
}
