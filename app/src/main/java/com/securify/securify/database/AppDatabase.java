package com.securify.securify.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.securify.securify.gameModels.GameModel;

/**
 * Created by Alwin on 27.04.2018.
 */

@Database(version = 1,entities = {GameModel.class})
public abstract class AppDatabase extends RoomDatabase {
    abstract public GameModelDao gameModelDao();

    private static AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (AppDatabase.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"game_db").build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    public static class PopulateDbAsync extends AsyncTask<Void,Void,Void> {

        private final GameModelDao gameDao;

        PopulateDbAsync(AppDatabase db){
            gameDao=db.gameModelDao();
        }



        @Override
        protected Void doInBackground(final Void... params) {
            GameModel game = new GameModel();
            //Lujza: did not compile so I had to comment it out
            //game.id=1;
            gameDao.insertGame(game);
            return null;
        }
    }

}
