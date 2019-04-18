package ca.csf.mobile1.yogioh.repository.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ca.csf.mobile1.yogioh.repository.Objects.card.CardTable;
import ca.csf.mobile1.yogioh.repository.Objects.deck.Deck;
import ca.csf.mobile1.yogioh.repository.Objects.deck.DeckTable;

public class DbConnectionFactory extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Database";
    public static final int DATABASE_VERSION = 1;

    public DbConnectionFactory(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CardTable.CREATE_TABLE);
        database.execSQL(DeckTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(CardTable.DROP_TABLE);
        database.execSQL(DeckTable.DROP_TABLE);
        onCreate(database);
    }
}
