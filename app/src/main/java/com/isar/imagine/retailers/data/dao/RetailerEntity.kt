package com.isar.imagine.retailers.data.dao

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.isar.imagine.retailers.data.UserDetails

@Entity(tableName = "retailer")
data class RetailerEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val displayName: String,
    val disabled: Boolean,
)

// Retailer DAO
@Dao
interface RetailerDao {
    @Query("SELECT * FROM retailer")
    suspend fun getAllRetailers(): List<RetailerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailers(retailers: List<RetailerEntity>)
}

// Room Database
@Database(entities = [RetailerEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun retailerDao(): RetailerDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
@Dao
interface UserDao {
    @Query("SELECT * FROM Retailers")
    suspend fun getAllRetailers(): List<UserDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRetailers(retailers: List<UserDetails>)
}
@Database(entities = [UserDetails::class], version = 2,  exportSchema = true)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    companion object{
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE retailer RENAME TO Retailers")
    }
}