package ca.csf.mobile1.yogioh.activity.queries.player;

import android.os.AsyncTask;

import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.model.YugiohPlayerDAO;

public class InsertOnePlayerAsyncTask extends AsyncTask<YugiohPlayer, Void, Long>
{
    private boolean isDataBaseError;

    private ListenerInserting onExecute;
    private ListenerInserted onSuccess;
    private final Runnable onDataBaseError;

    private YugiohPlayerDAO yugiohPlayerDAO;

    public InsertOnePlayerAsyncTask(YugiohPlayerDAO yugiohPlayerDAO, ListenerInserting onExecute, ListenerInserted onSuccess, Runnable onDataBaseError)
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
    protected  Long doInBackground(YugiohPlayer... yugiohPlayers)
    {

        Long id = null;
        try
        {
            id = yugiohPlayerDAO.insertOne(yugiohPlayers[0]);
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }

        return id;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onPlayerInserting();
    }

    @Override
    protected void onPostExecute(Long id)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onPlayerInserted(id);
    }

    public interface ListenerInserted
    {
        void onPlayerInserted(Long id);
    }

    public interface ListenerInserting
    {
        void onPlayerInserting();
    }
}
