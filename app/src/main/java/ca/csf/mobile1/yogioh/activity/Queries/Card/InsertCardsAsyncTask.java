package ca.csf.mobile1.yogioh.activity.Queries.Card;

import android.os.AsyncTask;

import java.util.List;

import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;

public class InsertCardsAsyncTask extends AsyncTask<YugiohCard, Void, Long[]>
{
    private boolean isDataBaseError;

    private ListenerFetching onExecute;
    private ListenerFetched onSuccess;
    private final Runnable onDataBaseError;

    private YugiohCardDAO yugiohCardDAO;

    public InsertCardsAsyncTask(YugiohCardDAO yugiohCardDAO, ListenerFetching onExecute, ListenerFetched onSuccess, Runnable onDataBaseError)
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
    protected Long[] doInBackground(YugiohCard... yugiohCards)
    {
        Long[] longs = null;
        try {
            longs = convertPrimitiveToWrapper(yugiohCardDAO.insertAll(yugiohCards));
        }catch (Exception e){
            onDataBaseError.run();
        }
        return longs;
    }

    private Long[] convertPrimitiveToWrapper(long[] longs) {
        Long[] result = new Long[longs.length];
        for (int i = 0; i < longs.length; i++) {
            result[i] = longs[i];
        }
        return result;
    }

    @Override
    protected void onPreExecute()
    {
        onExecute.onCardsFetching();
    }

    @Override
    protected void onPostExecute(Long[] longs)
    {
        if (isDataBaseError) onDataBaseError.run();
        else onSuccess.onCardsFetched(longs);
    }

    public interface ListenerFetched
    {
        void onCardsFetched(Long[] longs);
    }

    public interface ListenerFetching
    {
        void onCardsFetching();
    }
}
