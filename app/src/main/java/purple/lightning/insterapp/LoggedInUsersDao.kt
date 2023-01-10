package purple.lightning.insterapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LoggedInUsersDao {
    @Insert
    fun insert(loggedInUsersEntity: LoggedInUsersEntity)

    @Update
    fun update(loggedInUsersEntity: LoggedInUsersEntity)

    @Delete
    fun delete(loggedInUsersEntity: LoggedInUsersEntity)

    @Query("SELECT * from `LoggedInUsersTable`")
    fun fetchAllLoggedInUsers():List<LoggedInUsersEntity>

    @Query("SELECT * FROM `LoggedInUsersTable` where username=:username")
    fun fetchUserByUsername(username:String): Flow<LoggedInUsersEntity>

    @Query("SELECT * FROM `LoggedInUsersTable` where isLoggedIn=:boolval")
    fun fetchUserByLogStatus(boolval:Boolean): LoggedInUsersEntity

    @Query("UPDATE `LoggedInUsersTable` SET isLoggedIn=:loggedInStatus where username=:username")
    fun updateLoggedInStatus(username:String, loggedInStatus:Boolean)
}