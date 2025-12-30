package com.example.inventory.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inventory.data.cipher.SqlCipherKeyManager
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom

@Database(entities = [Item::class], version = 3, exportSchema = false)
abstract class InventoryDatabase: RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null


        fun getDatabase(context: Context): InventoryDatabase {
            return Instance ?: synchronized(this) {

                // С этого момента Room работает не с обычным SQLite, а с SQLCipher.
                System.loadLibrary("sqlcipher")

                // сохранение данных между запусками
                val prefs = context.getSharedPreferences(
                    "sqlcipher_prefs", // имя файла настроек
                    Context.MODE_PRIVATE // доступ только этому приложению
                )

                val keyManager = SqlCipherKeyManager(prefs)

                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    .openHelperFactory(keyManager.getSupportFactory())
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }


}