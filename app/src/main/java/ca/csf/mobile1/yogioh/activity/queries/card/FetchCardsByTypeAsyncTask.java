package ca.csf.mobile1.yogioh.activity.queries.card;

import android.os.AsyncTask;

import java.util.List;

import ca.csf.mobile1.yogioh.model.CardTypes;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;

public class FetchCardsByTypeAsyncTask extends AsyncTask<CardTypes,Void, List<YugiohCard>>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;
    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;

    private YugiohCardDAO yugiohCardDAO;

    public FetchCardsByTypeAsyncTask(YugiohCardDAO yugiohCardDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
    {
        if (onExecute == null) throw new IllegalArgumentException("onExecute cannot be null");
        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onDataBaseError == null) throw new IllegalArgumentException("onDataBaseError cannot be null");

        this.onExecute = onExecute;
        this.onSuccess = onSuccess;
        this.onDataBaseError = onDataBaseError;

        this.yugiohCardDAO = yugiohCardDAO;

        isDataBaseError = false;
    }

    @Override
    protected List<YugiohCard> doInBackground(CardTypes... cardType)
    {
        List<YugiohCard> yugiohCards = null;
        try {
            yugiohCards =  yugiohCardDAO.findCardByType(cardType[0].toString());
        }catch (Exception e){
            isDataBaseError = true;
        }
        return yugiohCards;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onCardsFetching();
    }

    @Override
    protected void onPostExecute(List<YugiohCard> yugiohCards)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onCardsFetched(yugiohCards);
    }

    public interface ListenerFetched
    {
        void onCardsFetched(List<YugiohCard> playerDeck);
    }

    public interface ListenerFetching
    {
        void onCardsFetching();
    }
}
