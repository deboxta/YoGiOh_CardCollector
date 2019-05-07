package ca.csf.mobile1.yogioh.activity.Queries.Player;

import android.os.AsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;
import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.model.YugiohPlayerDAO;

public class FetchPlayerByUsernameAsyncTask extends AsyncTask<String, Void, YugiohPlayer>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;
    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;
    private YugiohPlayerDAO yugiohPlayerDAO;

    public FetchPlayerByUsernameAsyncTask(YugiohPlayerDAO yugiohPlayerDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
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
    protected YugiohPlayer doInBackground(String... username)
    {
        YugiohPlayer wantedPlayer = null;

        try
        {
            wantedPlayer = yugiohPlayerDAO.findByUsername(username[0]);
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
    protected void onPostExecute(YugiohPlayer yugiohPlayer)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onPlayerFetched(yugiohPlayer);
    }

    public interface ListenerFetched
    {
        void onPlayerFetched(YugiohPlayer yugiohPlayer);
    }

    public interface ListenerFetching
    {
        void onPlayerFetching();
    }
}
