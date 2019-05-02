package ca.csf.mobile1.yogioh.activity.Queries.Deck;

import android.os.AsyncTask;

import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.model.YugiohDeckDAO;

public class FetchCardInDeckAsyncTask extends AsyncTask<Integer, Void, YugiohDeckCard>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;
    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;
    private YugiohDeckDAO yugiohDeckDAO;

    public FetchCardInDeckAsyncTask(YugiohDeckDAO yugiohDeckDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
    {
        if (yugiohDeckDAO == null) throw new IllegalArgumentException("yugiohDeckDAO cannot be null");
        if (onExecute == null) throw new IllegalArgumentException("onExecute cannot be null");
        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onDataBaseError == null) throw new IllegalArgumentException("onDataBaseError cannot be null");

        this.yugiohDeckDAO = yugiohDeckDAO;
        this.onExecute = onExecute;
        this.onSuccess = onSuccess;
        this.onDataBaseError = onDataBaseError;

        isDataBaseError = false;
    }

    @Override
    protected YugiohDeckCard doInBackground(Integer... integers)
    {
        YugiohDeckCard cardInDeck = null;

        try
        {
            cardInDeck = yugiohDeckDAO.selectOwnedCardById(integers[0], integers[1]);
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }
        return cardInDeck;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onCardsFetching();
    }

    @Override
    protected void onPostExecute(YugiohDeckCard cardInDeck)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onCardsFetched(cardInDeck);
    }

    public interface ListenerFetched
    {
        void onCardsFetched(YugiohDeckCard cardInDeck);
    }

    public interface ListenerFetching
    {
        void onCardsFetching();
    }
}
