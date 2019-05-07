package ca.csf.mobile1.yogioh.activity.queries.player;

import android.os.AsyncTask;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.repository.database.YugiohPlayerDAO;

public class FetchPlayersByIdsAsyncTask extends AsyncTask<Long, Void, List<YugiohPlayer>>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;
    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;

    private YugiohPlayerDAO yugiohPlayerDAO;

    public FetchPlayersByIdsAsyncTask(YugiohPlayerDAO yugiohPlayerDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
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
    protected List<YugiohPlayer> doInBackground(Long... ids)
    {

        List<YugiohPlayer> yugiohPlayers = null;
        try
        {
            yugiohPlayers = yugiohPlayerDAO.FindAllByIds(convertWrapperToPrimitive(ids));
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }

        return yugiohPlayers;
    }

    private long[] convertWrapperToPrimitive(Long[] wrapperIds) {
        long[] ids = new long[wrapperIds.length];
        for (int i = 0; i < wrapperIds.length; i++) {
            ids[i] = wrapperIds[i];
        }
        return ids;
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
