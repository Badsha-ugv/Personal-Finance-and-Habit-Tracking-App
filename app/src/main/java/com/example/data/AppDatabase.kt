package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AccountDao
import com.example.data.dao.CounterpartDao
import com.example.data.dao.HabitDao
import com.example.data.dao.LoanDao
import com.example.data.dao.LoanLedgerDao
import com.example.data.dao.ShoppingItemDao
import com.example.data.dao.ShoppingTripDao
import com.example.data.dao.TransactionDao
import com.example.data.model.AccountEntity
import com.example.data.model.CounterpartEntity
import com.example.data.model.HabitEntity
import com.example.data.model.LoanEntity
import com.example.data.model.LoanLedgerEntryEntity
import com.example.data.model.ShoppingItemEntity
import com.example.data.model.ShoppingTripEntity
import com.example.data.model.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        LoanEntity::class,
        HabitEntity::class,
        AccountEntity::class,
        CounterpartEntity::class,
        LoanLedgerEntryEntity::class,
        ShoppingTripEntity::class,
        ShoppingItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun loanDao(): LoanDao
    abstract fun habitDao(): HabitDao
    abstract fun accountDao(): AccountDao
    abstract fun counterpartDao(): CounterpartDao
    abstract fun loanLedgerDao(): LoanLedgerDao
    abstract fun shoppingTripDao(): ShoppingTripDao
    abstract fun shoppingItemDao(): ShoppingItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calm_clarity_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
