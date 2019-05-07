package ca.csf.mobile1.yogioh.activity.queries.card;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import ca.csf.mobile1.yogioh.model.YugiohCard;

public class InitialInsetionAsynchTask extends AsyncTask<Void, Void, YugiohCard>
{

    @Override
    protected YugiohCard doInBackground(Void... voids)
    {
        return null;
    }

    private void prepareInitialCards()
    {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,"yugiohinsertion.txt");

        StringBuilder text = new StringBuilder();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while((line = br.readLine()) != null)
            {
                text.append(line);
                text.append('\n');
            }
            br.close();;
        }
        catch(Exception e)
        {
            //Bonjour
        }

    }
}
