package purple.lightning.insterapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LoggedInUsersEntity::class], version = 5, exportSchema = true)
abstract class InsterRoomDatabase: RoomDatabase(){

    abstract fun loggedInUsersDao(): LoggedInUsersDao

    companion object{
        @Volatile
        private var INSTANCE: InsterRoomDatabase? = null

        fun getInstance(context: Context):InsterRoomDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        InsterRoomDatabase::class.java,
                        "inster_room_database"
                    ).allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}