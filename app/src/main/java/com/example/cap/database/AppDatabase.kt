package com.example.cap.database

import android.content.Context
import androidx.room.*
import com.example.cap.database.dao.EventDAO
import com.example.cap.domain.Event

@Database(entities = [Event::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDAO

    companion object {
        /*
        為 Instance 加上 @Volatile 註解。
        系統一律不會快取易失變數的值，所有讀取與寫入作業都會在主記憶體中完成。
        這些功能有助確保 Instance 的值隨時處於最新狀態，而且對於所有執行緒均保持一致。
        也就是說，任一執行緒對 Instance 所做的變更會立即向所有其他執行緒顯示。
         */
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration()
                    .build().also { Instance = it }
            }
        }
    }
}