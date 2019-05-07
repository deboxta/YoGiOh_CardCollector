package ca.csf.mobile1.yogioh.activity.Queries.Card;

import android.os.AsyncTask;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;

public class FetchCardsAsyncTask extends AsyncTask<Void, Void, List<YugiohCard>>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;
    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;
    private YugiohCardDAO yugiohCardDAO;

    public FetchCardsAsyncTask(YugiohCardDAO yugiohCardDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
    {
        if (yugiohCardDAO == null) throw new IllegalArgumentException("yugiohCardDAO cannot be null");
        if (onExecute == null) throw new IllegalArgumentException("onExecute cannot be null");
        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onDataBaseError == null) throw new IllegalArgumentException("onDataBaseError cannot be null");

        this.yugiohCardDAO = yugiohCardDAO;
        this.onExecute = onExecute;
        this.onSuccess = onSuccess;
        this.onDataBaseError = onDataBaseError;

        isDataBaseError = false;
    }

    @Override
    protected List<YugiohCard> doInBackground(Void... voids)
    {
        List<YugiohCard> wantedCards = null;

        try
        {
            wantedCards = yugiohCardDAO.selectAll();
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }

        return wantedCards;
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
        void onCardsFetched(List<YugiohCard> yugiohCards);
    }

    public interface ListenerFetching
    {
        void onCardsFetching();
    }
}
