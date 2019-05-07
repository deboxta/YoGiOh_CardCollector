package ca.csf.mobile1.yogioh.activity.queries.player;

import android.os.AsyncTask;

import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.model.YugiohPlayerDAO;

public class DeletePlayerAsyncTask extends AsyncTask<YugiohPlayer, Void, Void> 
{
    private boolean isDataBaseError;

    private ListenerDeleting onExecute;
    private ListenerDeleted onSuccess;
    private final Runnable onDataBaseError;

    private YugiohPlayerDAO yugiohPlayerDAO;

    public DeletePlayerAsyncTask(YugiohPlayerDAO yugiohPlayerDAO, ListenerDeleting onExecute, ListenerDeleted onSuccess, Runnable onDataBaseError)
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
    protected Void doInBackground(YugiohPlayer... yugiohPlayers)
    {
        try
        {
            yugiohPlayerDAO.delete(yugiohPlayers[0]);
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }

        return null;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onPlayerFetching();
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onPlayerFetched();
    }

    public interface ListenerDeleted
    {
        void onPlayerFetched();
    }

    public interface ListenerDeleting
    {
        void onPlayerFetching();
    }
}
