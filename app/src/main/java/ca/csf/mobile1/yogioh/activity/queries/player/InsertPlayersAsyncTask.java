package ca.csf.mobile1.yogioh.activity.Queries.Player;

import android.os.AsyncTask;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.model.YugiohPlayerDAO;

public class InsertPlayersAsyncTask extends AsyncTask<YugiohPlayer, Void, Long[]>
{
    private boolean isDataBaseError;

    private ListenerInserting onExecute;
    private ListenerInserted onSuccess;
    private final Runnable onDataBaseError;

    private YugiohPlayerDAO yugiohPlayerDAO;

    public InsertPlayersAsyncTask(YugiohPlayerDAO yugiohPlayerDAO, ListenerInserting onExecute, ListenerInserted onSuccess, Runnable onDataBaseError)
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
    protected  Long[] doInBackground(YugiohPlayer... yugiohPlayers)
    {

        Long[] ids = null;
        try
        {
            ids = convertPrimitiveToWrapper(yugiohPlayerDAO.insertAll(yugiohPlayers));
        }
        catch (Exception e)
        {
            isDataBaseError = true;
        }

        return ids;
    }

    private Long[] convertPrimitiveToWrapper(long[] primitiveIds) {
        Long[] ids = new Long[primitiveIds.length];
        for (int i = 0; i < primitiveIds.length; i++) {
            ids[i] = primitiveIds[i];
        }
        return ids;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onPlayerInserting();
    }

    @Override
    protected void onPostExecute(Long[] ids)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onPlayerInserted(ids);
    }

    public interface ListenerInserted
    {
        void onPlayerInserted(Long[] ids);
    }

    public interface ListenerInserting
    {
        void onPlayerInserting();
    }
}
