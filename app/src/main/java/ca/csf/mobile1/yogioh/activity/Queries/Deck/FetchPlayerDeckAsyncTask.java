package ca.csf.mobile1.yogioh.activity.Queries.Deck;

import android.os.AsyncTask;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.model.YugiohDeckDAO;

public class FetchPlayerDeckAsyncTask extends AsyncTask<Integer, Void, List<YugiohDeckCard>>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;

    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;
    private YugiohDeckDAO yugiohDeckDAO;

    public FetchPlayerDeckAsyncTask(YugiohDeckDAO yugiohDeckDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
    {
        if (yugiohDeckDAO == null) throw new IllegalArgumentException("yugiohCardDAO cannot be null");
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
    protected List<YugiohDeckCard> doInBackground(Integer... integers)
    {
        List<YugiohDeckCard> playerDeck = null;

        try
        {
            playerDeck = yugiohDeckDAO.selectAll(integers[0]);
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }
        return playerDeck;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onCardsFetching();
    }

    @Override
    protected void onPostExecute(List<YugiohDeckCard> playerDeck)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onCardsFetched(playerDeck);
    }

    public interface ListenerFetched
    {
        void onCardsFetched(List<YugiohDeckCard> playerDeck);
    }

    public interface ListenerFetching
    {
        void onCardsFetching();
    }
}
