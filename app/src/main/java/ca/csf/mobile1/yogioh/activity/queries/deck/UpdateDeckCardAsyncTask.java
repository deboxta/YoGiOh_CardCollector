package ca.csf.mobile1.yogioh.activity.queries.deck;

import android.os.AsyncTask;

import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;

public class UpdateDeckCardAsyncTask extends AsyncTask<YugiohDeckCard, Void, Void>
{

    private boolean isDataBaseError;

    private ListenerModifying onExecute;
    private ListenerModified onSuccess;
    private final Runnable onDataBaseError;
    private YugiohDeckDAO yugiohDeckDAO;

    public UpdateDeckCardAsyncTask(YugiohDeckDAO yugiohDeckDAO, ListenerModifying onExecute, ListenerModified onSuccess, Runnable onDataBaseError)
    {
        if (yugiohDeckDAO == null) throw new RuntimeException("Yugioh");
        if (onExecute == null) throw new IllegalArgumentException("onExecute cannot be null");
        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onDataBaseError == null) throw new IllegalArgumentException("onDataBaseError cannot be null");

        this.onExecute = onExecute;
        this.onSuccess = onSuccess;
        this.yugiohDeckDAO = yugiohDeckDAO;
        this.onDataBaseError = onDataBaseError;

        isDataBaseError = false;
    }

    @Override
    protected Void doInBackground(YugiohDeckCard... yugiohDeckCards)
    {
        try
        {
            yugiohDeckDAO.updateCardAmount(yugiohDeckCards[0]);
        }
        catch(Exception e)
        {
            isDataBaseError = true;
        }

        return null;
    }

    protected void onPreExecute(){onExecute.onModifying();}

    protected void onPostExecute(Void...voids)
    {
        if(isDataBaseError == true)onDataBaseError.run();
        onSuccess.onModified();
    }

    public interface ListenerModifying
    {
        void onModifying();
    }

    public interface ListenerModified
    {
        void onModified();
    }


}
