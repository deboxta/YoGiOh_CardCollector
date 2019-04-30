package ca.csf.mobile1.yogioh.repository.database;


import androidx.room.RoomDatabase;

import ca.csf.mobile1.yogioh.model.YugiohCard;
import ca.csf.mobile1.yogioh.model.YugiohCardDAO;
import ca.csf.mobile1.yogioh.model.YugiohDeckDAO;
import ca.csf.mobile1.yogioh.model.YugiohPlayer;
import ca.csf.mobile1.yogioh.model.YugiohPlayerDAO;

@androidx.room.Database(entities = {YugiohCard.class, YugiohPlayer.class}, version = YugiohDatabase.VERSION_OF_DATABASE, exportSchema = false)
public abstract class YugiohDatabase extends RoomDatabase
{
    public static final int VERSION_OF_DATABASE = 3;
    public abstract YugiohCardDAO yugiohCardDao();
    public abstract YugiohPlayerDAO yugiohPlayerDAO();
    public abstract YugiohDeckDAO yugiohDeckDAO();
}
