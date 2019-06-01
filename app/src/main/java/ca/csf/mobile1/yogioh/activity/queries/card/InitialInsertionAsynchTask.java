package ca.csf.mobile1.yogioh.activity.queries.card;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;

public class InitialInsertionAsynchTask extends AsyncTask<InputStream, Void, Void>
{

    private static final String LINE_SPLIT_CHAR = "!";
    private YugiohCardDAO yugiohCardDAO;
    private Listener onSuccess;

    public InitialInsertionAsynchTask(YugiohCardDAO yugiohCardDAO, Listener onSuccess)
    {
        this.onSuccess = onSuccess;
        this.yugiohCardDAO = yugiohCardDAO;
    }

    @Override
    protected Void doInBackground(InputStream... inputStreams)
    {
        yugiohCardDAO.insertAll(prepareInitialCards(inputStreams[0]));
        return null;
    }

    private List<YugiohCard> prepareInitialCards(InputStream inputStream)
    {
        String[] cardsInformation;

        try
        {
            //BEN_CORRECTION : Nommage paresseux.
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            List<YugiohCard> yugiohCardList = new ArrayList<>();

            while((line = br.readLine()) != null)
            {
                YugiohCard yugiohCard = new YugiohCard();
                cardsInformation = line.split(LINE_SPLIT_CHAR);
                yugiohCard.id = Integer.valueOf(cardsInformation[0]);
                yugiohCard.cardName = cardsInformation[1];
                yugiohCard.type = cardsInformation[2];
                yugiohCard.attribute = cardsInformation[3];
                yugiohCard.monsterType = cardsInformation[4];
                yugiohCard.nbStars = Integer.valueOf(cardsInformation[5]);
                yugiohCard.cardDescription = cardsInformation[6];
                yugiohCard.cardAttack = Integer.valueOf(cardsInformation[7]);
                yugiohCard.cardDefense = Integer.valueOf(cardsInformation[8]);
                yugiohCardList.add(yugiohCard);
            }
            br.close();
            return yugiohCardList;
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);

        onSuccess.onCardsInitialised();
    }

    public interface Listener
    {
        void onCardsInitialised();
    }
}

