package ca.csf.mobile1.yogioh.activity.queries.player;

import android.os.AsyncTask;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.model.YugiohPlayerDAO;

public class FetchPlayersAsyncTask extends AsyncTask<Void, Void, List<YugiohPlayer>>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;
    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;

    private YugiohPlayerDAO yugiohPlayerDAO;

    public FetchPlayersAsyncTask(YugiohPlayerDAO yugiohPlayerDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
    {
        if (onExecute == null) throw new IllegalArgumentException("onExecute cannot be null");
        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onDataBaseError == null) throw new IllegalArgumentException("onDataBaseError cannot be null");

        this.onExecute = onExecute;
        this.onSuccess = onSuccess;
        this.onDataBaseError = onDataBaseError;

        this.yugiohPlayerDAO = yugiohPlayerDAO;

        isDataBaseError = false;
    }

    @Override
    protected List<YugiohPlayer> doInBackground(Void... voids)
    {
        List<YugiohPlayer> yugiohPlayers = null;
        try {
            yugiohPlayerDAO.selectAll();
        }catch (Exception e){
            isDataBaseError = true;
        }

        return yugiohPlayers;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onCardsFetching();
    }

    @Override
    protected void onPostExecute(List<YugiohPlayer> yugiohPlayers)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onCardsFetched(yugiohPlayers);
    }

    public interface ListenerFetched
    {
        void onCardsFetched(List<YugiohPlayer> yugiohPlayers);
    }

    public interface ListenerFetching
    {
        void onCardsFetching();
    }
}
