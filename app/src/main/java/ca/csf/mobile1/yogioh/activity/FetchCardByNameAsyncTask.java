package ca.csf.mobile1.yogioh.activity;

import android.os.AsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;

public class FetchCardByNameAsyncTask extends AsyncTask<String, Void, YugiohCard>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;
    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;
    private YugiohCardDAO yugiohCardDAO;

    public FetchCardByNameAsyncTask(YugiohCardDAO yugiohCardDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
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
    protected YugiohCard doInBackground(String... strings)
    {
        YugiohCard wantedCard = null;

        try
        {
            wantedCard = yugiohCardDAO.findCardByName(strings[0]);
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }

        return wantedCard;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onCardsFetching();
    }

    @Override
    protected void onPostExecute(YugiohCard yugiohCards)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onCardsFetched(yugiohCards);
    }

    public interface ListenerFetched
    {
        void onCardsFetched(YugiohCard playerDeck);
    }

    public interface ListenerFetching
    {
        void onCardsFetching();
    }
}
