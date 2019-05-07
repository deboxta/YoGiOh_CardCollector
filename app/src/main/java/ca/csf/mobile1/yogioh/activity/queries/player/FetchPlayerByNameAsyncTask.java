package ca.csf.mobile1.yogioh.activity.queries.player;

import android.os.AsyncTask;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.repository.database.YugiohPlayerDAO;

public class FetchPlayerByNameAsyncTask extends AsyncTask<String, Void, List<YugiohPlayer>>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;
    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;
    private YugiohPlayerDAO yugiohPlayerDAO;

    public FetchPlayerByNameAsyncTask(YugiohPlayerDAO yugiohPlayerDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
    {
        if (yugiohPlayerDAO == null) throw new IllegalArgumentException("yugiohPlayerDAO cannot be null");
        if (onExecute == null) throw new IllegalArgumentException("onExecute cannot be null");
        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onDataBaseError == null) throw new IllegalArgumentException("onDataBaseError cannot be null");

        this.yugiohPlayerDAO = yugiohPlayerDAO;
        this.onExecute = onExecute;
        this.onSuccess = onSuccess;
        this.onDataBaseError = onDataBaseError;

        isDataBaseError = false;
    }

    @Override
    protected List<YugiohPlayer> doInBackground(String... name)
    {
        List<YugiohPlayer> wantedPlayer = null;

        try
        {
            wantedPlayer = yugiohPlayerDAO.findByName(name[0]);
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }

        return wantedPlayer;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onPlayerFetching();
    }

    @Override
    protected void onPostExecute(List<YugiohPlayer> yugiohPlayers)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onPlayerFetched(yugiohPlayers);
    }

    public interface ListenerFetched
    {
        void onPlayerFetched(List<YugiohPlayer> yugiohPlayers);
    }

    public interface ListenerFetching
    {
        void onPlayerFetching();
    }
}
