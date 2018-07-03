package com.folio.dooley1001.folio.dataBase;
import android.content.ContentValues;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Type;
import java.util.Arrays;
import com.folio.dooley1001.folio.models.easyrest.watchListStructures;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    private static final String DATABASE_NAME = "Folio.db";
    private static final String DATABASE_TABLE = "watch_list";
    private static final int DATABASE_VERSION = 1;
    //db columes
    private static final String WATCH_LIST_COL_0 = "ID";
    private static final String WATCH_LIST_COL_1 = "FAVORITES";
    private static final String DEFAULT_WATCH_LIST = "BTC,ETH,LTC";

    // Use singleton design pattern so there is only ever one DB object floating around
    public static synchronized DatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Run this method when the DB Schema is upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    // Run this method the first time the DB is ever created
    @Override
    public void onCreate (SQLiteDatabase database) {
        // Drop the table if it exists
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        // Create the new table with 2 columns. One for ID and one for the coin list
        database.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + WATCH_LIST_COL_0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + WATCH_LIST_COL_1 + " TEXT)");
        // Convert the defaultCoinsList into our default list of string. Use comma as the delimeter
        List<String> defaultCoinsList = new ArrayList<>(Arrays.asList(DEFAULT_WATCH_LIST.split(",")));
        // Instantiate a serializer so we can easily load/store the list in the DB
        Gson gson = new Gson();
        // Serialize the list of strings into a JSON payload that we can store in the DB
        String favoritesListString = gson.toJson(defaultCoinsList);
        // Put the serialized paylout into a ContentValues object to prepare it for storage
        ContentValues defaultFavoriteCoins = new ContentValues();
        defaultFavoriteCoins.put(WATCH_LIST_COL_1, favoritesListString);
        // Insert the list into the DB
        database.insert(DATABASE_TABLE, null, defaultFavoriteCoins);
    }

    // Grab the user's list of favorite coins out of the DB and save them into an object that
    // represents the coins as a list and as a hashtable. The hashtable can be used for fast lookups
    // The list will maintain the order of the user's favorite coins
    public watchListStructures getFavorites() {
        // Get a connection to the DB
        SQLiteDatabase db = this.getReadableDatabase();
        // Pull the favorites out of the DB
        Cursor cursor = db.rawQuery("select FAVORITES from " + DATABASE_TABLE, null);
        // Move the cursor to the first element returned by the query
        cursor.moveToPosition(0);
        // Get the list represented as a string out of the cursor
        String favoritesListString = cursor.getString(0);
        cursor.close();

        // Instantiate a new serializer so that we can convert the string from the DB into a real list object
        Gson gson = new Gson();
        // Tell Gson what type we want to convert the serialized string to
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        // Load the string of the user's favorite coins into a list of strings!
        ArrayList<String> favoritesList = gson.fromJson(favoritesListString, type);

        // Instantiate a new hashmap and put all of the items in the list above into the hashmap
        HashMap<String, String> favoritesMap = new HashMap<>();
        for (int i = 0; i < favoritesList.size(); i++) {
            favoritesMap.put(favoritesList.get(i), favoritesList.get(i));
        }
        // Return a wrapper object that holds both the list and the hashmap
        return new watchListStructures(favoritesList, favoritesMap);
    }

    // This will allow us to save the user's coin favorites any time by passing in a wrapper object
    // that contains the list of the user's favorite coins as well as the hashtable representation
    public void saveCoinFavorites(watchListStructures favs) {
        // Get a writeable connection to the db
        SQLiteDatabase db = this.getWritableDatabase();
        // Instantiate a new serializer so that we can convert our list of coins back into a string
        // that is storable in the DB
        Gson gson = new Gson();
        // Convert the list of the user's favorite coins into a string
        String favoritesListString = gson.toJson(favs.watchList);
        // Put the serialized paylout into a ContentValues object to prepare it for storage
        ContentValues newCoinFavorites = new ContentValues();
        newCoinFavorites.put(WATCH_LIST_COL_1, favoritesListString);
        // Update the row in the DB with the new list of favorites!
        db.update(DATABASE_TABLE, newCoinFavorites, null, null);
    }
}
