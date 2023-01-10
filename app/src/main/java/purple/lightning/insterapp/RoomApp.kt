package purple.lightning.insterapp

import android.app.Application

class RoomApp: Application(){
    val db by lazy {
        InsterRoomDatabase.getInstance(this)
    }
}