package com.isar.imagine.viewmodels

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase

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
@Database(entities = [RetailerEntity::class], version = 1)
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
