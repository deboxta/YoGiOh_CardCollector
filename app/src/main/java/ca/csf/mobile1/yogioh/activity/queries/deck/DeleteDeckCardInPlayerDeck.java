package ca.csf.mobile1.yogioh.activity.queries.deck;

import android.os.AsyncTask;
import ca.csf.mobile1.yogioh.model.YugiohDeckCard;
import ca.csf.mobile1.yogioh.repository.database.YugiohDeckDAO;

public class DeleteDeckCardInPlayerDeck extends AsyncTask<YugiohDeckCard,Void,Void>
{
    private boolean isDataBaseError;

    private ListenerDeleting onExecute;
    private ListenerDeleted onSuccess;
    private final Runnable onDataBaseError;
    private YugiohDeckDAO yugiohDeckDAO;

    public DeleteDeckCardInPlayerDeck(YugiohDeckDAO yugiohDeckDAO, ListenerDeleting onExecute, ListenerDeleted onSuccess, Runnable onDataBaseError)
    {
        //BEN_CORRECTION : Validations manquantes ici. Pourtant, vous l'avez fait dans les autres tâches async...
        this.yugiohDeckDAO = yugiohDeckDAO;
        this.onExecute = onExecute;
        this.onSuccess = onSuccess;
        this.onDataBaseError = onDataBaseError;

        isDataBaseError = false;
    }

    @Override
    protected Void doInBackground(YugiohDeckCard... yugiohDeckCards)
    {
        try
        {
            yugiohDeckDAO.delete(yugiohDeckCards[0]);
        }
        catch(Exception e)
        {
            isDataBaseError = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        if(isDataBaseError) onDataBaseError.run();
        onSuccess.onDeleted();
    }

    public interface ListenerDeleting
    {
        void onDeleting();
    }

    public interface ListenerDeleted
    {
        void onDeleted();
    }


}
