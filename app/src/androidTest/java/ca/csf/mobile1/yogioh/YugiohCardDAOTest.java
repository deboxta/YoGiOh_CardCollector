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

import ca.csf.mobile1.yogioh.model.CardTypes;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(AndroidJUnit4.class)
public class YugiohCardDAOTest
{
    private static final String ANTHONYCARDNAME = "Anthony le furry aspect";
    private static final String ANTOINECARDNAME = "Antoine l'aspect du temps";
    private static final String BLACKMAGICIANCARDNAME = "Black Magician";
    private YugiohCardDAO yugiohCardDAO;
    private YugiohDatabase db;

    @Before
    public void createDb()
    {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, YugiohDatabase.class).build();
        yugiohCardDAO = db.yugiohCardDao();
    }

    @After
    public void closeDb() throws IOException
    {
        db.close();
    }

    @Test
    public void writeCardAndReadAllInList()
    {
        YugiohCard[] yugiohCards = createCards();
        yugiohCardDAO.insertAll(yugiohCards);
        List<YugiohCard> allCards = yugiohCardDAO.selectAll();
        assertEquals(3, allCards.size());
    }

    @Test
    public void writeCardAndReadInListByName()
    {
        YugiohCard card = new YugiohCard();
        card.cardName = BLACKMAGICIANCARDNAME;
        card.id = (int) yugiohCardDAO.insertOne(card);
        YugiohCard byName = yugiohCardDAO.findCardByName(BLACKMAGICIANCARDNAME);
        assertEquals(card, byName);
    }

    @Test
    public void writeCardsAndReadInListByIds()
    {
        YugiohCard[] yugiohCards = createCards();
        long[] newCardsId = assignNewIds(yugiohCards);
        List<YugiohCard> byIds = yugiohCardDAO.findAllByIds(newCardsId);
        for (int i = 0; i < byIds.size(); i++)
        {
            assertEquals(yugiohCards[i], byIds.get(i));
        }
    }


    @Test
    public void writeCardsAndReadInListByType()
    {
        YugiohCard[] yugiohCards = createCards();
        List<YugiohCard> byType = yugiohCardDAO.findCardByType(CardTypes.Monster.toString());
        for (int i = 0; i < byType.size(); i++)
        {
            assertSame(byType.get(i).type, CardTypes.Monster.toString());
        }
    }

    @Test
    public void deleteCard()
    {
        YugiohCard[] yugiohCards = createCards();
        assignNewIds(yugiohCards);
        yugiohCardDAO.delete(yugiohCards[2]);
        int temp = yugiohCardDAO.selectAll().size();
        assertEquals(2, yugiohCardDAO.selectAll().size());
        for (int i = 0; i < yugiohCardDAO.selectAll().size(); i++)
        {
            assertEquals(yugiohCards[i], yugiohCardDAO.selectAll().get(i));
        }
    }

    private YugiohCard[] createCards()
    {
        YugiohCard[] yugiohCards = new YugiohCard[3];
        yugiohCards[0] = new YugiohCard();
        yugiohCards[1] = new YugiohCard();
        yugiohCards[1].cardName = ANTHONYCARDNAME;
        yugiohCards[2] = new YugiohCard();
        yugiohCards[2].cardName = ANTOINECARDNAME;
        return yugiohCards;
    }

    private long[] assignNewIds(YugiohCard[] yugiohCards)
    {
        long[] newCardsId = yugiohCardDAO.insertAll(yugiohCards);
        for (int i = 0; i < newCardsId.length; i++)
        {
            yugiohCards[i].id = (int) newCardsId[i];
        }
        return newCardsId;
    }


}

