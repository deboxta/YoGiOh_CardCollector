package ca.csf.mobile1.yogioh.activity.queries.card;

import android.os.AsyncTask;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;

public class FetchCardsByIdsAsyncTask extends AsyncTask<Long,Void,List<YugiohCard>>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;
    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;

    private YugiohCardDAO yugiohCardDAO;

    public FetchCardsByIdsAsyncTask(YugiohCardDAO yugiohCardDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
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
    protected List<YugiohCard> doInBackground(Long... ids)
    {
        List<YugiohCard> yugiohCards = null;
        try {
            yugiohCards =  yugiohCardDAO.findAllByIds(convertWrapperToPrimitive(ids));
        }catch (Exception e){
            isDataBaseError = true;
        }
        return yugiohCards;
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
        onExecute.onCardsFetching();
    }

    @Override
    protected void onPostExecute(List<YugiohCard> yugiohCards)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onCardsFetched(yugiohCards);
    }

    public interface ListenerFetched
    {
        void onCardsFetched(List<YugiohCard> playerDeck);
    }

    public interface ListenerFetching
    {
        void onCardsFetching();
    }
}
