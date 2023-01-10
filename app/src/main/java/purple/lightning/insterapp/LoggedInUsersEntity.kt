package purple.lightning.insterapp

import android.graphics.Bitmap
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LoggedInUsersTable")
data class LoggedInUsersEntity(
    @PrimaryKey @NonNull
    var username: String = "",
    var password: String?= null,
    var isLoggedIn:Boolean = true
    )
