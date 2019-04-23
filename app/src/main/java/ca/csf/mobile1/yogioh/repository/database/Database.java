package ca.csf.mobile1.yogioh.repository.database;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {YugiohCard.class}, version = Database.VERSION_OF_DATABASE)
public abstract class                                                                                                          Database extends RoomDatabase
{
    public static final int VERSION_OF_DATABASE = 1;
    public abstract YugiohDAO yugiohDAO();
}
