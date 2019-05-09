package ca.csf.mobile1.yogioh.activity.queries.card;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohCardDAO;

public class InitialInsetionAsynchTask extends AsyncTask<InputStream, Void, Void>
{

    private YugiohCardDAO yugiohCardDAO;

    public InitialInsetionAsynchTask(YugiohCardDAO yugiohCardDAO)
    {

        this.yugiohCardDAO = yugiohCardDAO;
    }

    @Override
    protected Void doInBackground(InputStream... inputStreams)
    {
        List<YugiohCard> yugiohCardList = prepareInitialCards(inputStreams[0]);
        yugiohCardDAO.insertAll(yugiohCardList);

        return null;
    }

    private List<YugiohCard> prepareInitialCards(InputStream inputStream)
    {
        String[] cardsInformations;

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            List<YugiohCard> yugiohCardList = new ArrayList<>();

            while((line = br.readLine()) != null)
            {
                YugiohCard yugiohCard = new YugiohCard();
                cardsInformations = line.split("!");
                yugiohCard.id = Integer.valueOf(cardsInformations[0]);
                yugiohCard.cardName = cardsInformations[1];
                yugiohCard.type = cardsInformations[2];
                yugiohCard.attribute = cardsInformations[3];
                yugiohCard.monsterType = cardsInformations[4];
                yugiohCard.nbStars = Integer.valueOf( cardsInformations[5]);
                yugiohCard.cardDescription = cardsInformations[6];
                yugiohCard.cardAttack = Integer.valueOf(cardsInformations[7]);
                yugiohCard.cardDefense = Integer.valueOf(cardsInformations[8]);
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
}

