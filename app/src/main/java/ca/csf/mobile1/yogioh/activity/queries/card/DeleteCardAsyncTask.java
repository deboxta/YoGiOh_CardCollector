package ca.csf.mobile1.yogioh.activity.queries.card;

import android.os.AsyncTask;

import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;

public class DeleteCardAsyncTask extends AsyncTask<YugiohCard, Void, Void>
{
    private boolean isDataBaseError;

    private ListenerDeleting onExecute;
    private ListenerDeleted onSuccess;
    private final Runnable onDataBaseError;

    private YugiohCardDAO yugiohCardDAO;

    public DeleteCardAsyncTask(YugiohCardDAO yugiohCardDAO, ListenerDeleting onExecute, ListenerDeleted onSuccess, Runnable onDataBaseError)
    {
        if (onExecute == null) throw new IllegalArgumentException("onExecute cannot be null");
        if (onSuccess == null) throw new IllegalArgumentException("onSuccess cannot be null");
        if (onDataBaseError == null) throw new IllegalArgumentException("onDataBaseError cannot be null");

        this.onExecute = onExecute;
        this.onSuccess = onSuccess;
        this.onDataBaseError = onDataBaseError;

        this.yugiohCardDAO = yugiohCardDAO;

        isDataBaseError = false;
    }

    @Override
    protected Void doInBackground(YugiohCard... yugiohCards)
    {
        try {
            yugiohCardDAO.delete(yugiohCards[0]);
        }catch (Exception e){
            isDataBaseError = true;
        }
        return null;
    }


    @Override
    protected void onPreExecute()
    {
        onExecute.onCardsDeleting();
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onCardsDeleted();
    }

    public interface ListenerDeleted
    {
        void onCardsDeleted();
    }

    public interface ListenerDeleting
    {
        void onCardsDeleting();
    }
}
