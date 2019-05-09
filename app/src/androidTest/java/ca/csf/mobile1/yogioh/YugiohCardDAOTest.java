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
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;
import ca.csf.mobile1.yogioh.repository.database.YugiohDatabase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(AndroidJUnit4.class)
public class YugiohCardDAOTest
{
    private static final String ANTHONY_CARD_NAME = "Anthony le furry aspect";
    private static final String ANTOINE_CARD_NAME = "Antoine l'aspect du temps";
    private static final String BLACK_MAGICIAN_CARD_NAME = "Black Magician";


    private static final String TEST_DEFAULT_NAME = "Blue-Eyes White Dragon";
    private static final String TEST_DEFAULT_TYPE = "Monster";
    private static final String TEST_DEFAULT_ATTRIBUTE = "Light";
    private static final String TEST_DEFAULT_MONSTER_TYPE = "Dragon";
    private static final int TEST_DEFAULT_NB_STARS = 8;
    private static final String TEST_DEFAULT_DESCRIPTION = "This legendary dragon is a powerful engine of destruction. Virtually invincible, very few have faced this awesome creature and lived to tell the tale";
    private static final int TEST_DEFAULT_ATTACK_VALUE = 3000;
    private static final int TEST_DEFAULT_DEFENSE_VALUE = 2500;
    public static final String CARD_TYPE_MONSTER = "Monster";


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
        generateDefaultCard(card);
        card.cardName = BLACK_MAGICIAN_CARD_NAME;
        card.id = (int) yugiohCardDAO.insertOne(card);
        YugiohCard byName = yugiohCardDAO.findCardByName(BLACK_MAGICIAN_CARD_NAME);
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
        List<YugiohCard> byType = yugiohCardDAO.findCardByType(CARD_TYPE_MONSTER);
        for (int i = 0; i < byType.size(); i++)
        {
            assertSame(byType.get(i).type, CARD_TYPE_MONSTER);
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
        generateDefaultCard(yugiohCards[0]);
        yugiohCards[1] = new YugiohCard();
        generateDefaultCard(yugiohCards[1]);
        yugiohCards[1].cardName = ANTHONY_CARD_NAME;
        yugiohCards[2] = new YugiohCard();
        generateDefaultCard(yugiohCards[2]);
        yugiohCards[2].cardName = ANTOINE_CARD_NAME;
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

    private void generateDefaultCard(YugiohCard yugiohCard)
    {
        yugiohCard.cardName = TEST_DEFAULT_NAME;
        yugiohCard.type = TEST_DEFAULT_TYPE;
        yugiohCard.attribute = TEST_DEFAULT_ATTRIBUTE;
        yugiohCard.monsterType = TEST_DEFAULT_MONSTER_TYPE;
        yugiohCard.nbStars = TEST_DEFAULT_NB_STARS;
        yugiohCard.cardDescription = TEST_DEFAULT_DESCRIPTION;
        yugiohCard.cardAttack = TEST_DEFAULT_ATTACK_VALUE;
        yugiohCard.cardDefense = TEST_DEFAULT_DEFENSE_VALUE;

    }


}

