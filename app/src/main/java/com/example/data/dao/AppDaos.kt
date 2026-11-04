package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AccountEntity
import com.example.data.model.CounterpartEntity
import com.example.data.model.HabitEntity
import com.example.data.model.LoanEntity
import com.example.data.model.LoanLedgerEntryEntity
import com.example.data.model.ShoppingItemEntity
import com.example.data.model.ShoppingTripEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
}

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY isSettled ASC, returnDueDate ASC")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(loans: List<LoanEntity>)

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)
}

@Dao
interface CounterpartDao {
    @Query("SELECT * FROM counterparts ORDER BY name ASC")
    fun getAllCounterparts(): Flow<List<CounterpartEntity>>

    @Query("SELECT * FROM counterparts WHERE id = :id LIMIT 1")
    fun getCounterpartById(id: Long): Flow<CounterpartEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounterpart(counterpart: CounterpartEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(counterparts: List<CounterpartEntity>)

    @Delete
    suspend fun deleteCounterpart(counterpart: CounterpartEntity)
}

@Dao
interface LoanLedgerDao {
    @Query("SELECT * FROM loan_ledger_entries WHERE counterpartId = :counterpartId ORDER BY timestamp DESC")
    fun getEntriesForCounterpart(counterpartId: Long): Flow<List<LoanLedgerEntryEntity>>

    @Query("SELECT * FROM loan_ledger_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<LoanLedgerEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: LoanLedgerEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<LoanLedgerEntryEntity>)

    @Delete
    suspend fun deleteEntry(entry: LoanLedgerEntryEntity)
}

@Dao
interface ShoppingTripDao {
    @Query("SELECT * FROM shopping_trips ORDER BY isCompleted ASC, createdDate DESC")
    fun getAllTrips(): Flow<List<ShoppingTripEntity>>

    @Query("SELECT * FROM shopping_trips WHERE id = :id LIMIT 1")
    fun getTripById(id: Long): Flow<ShoppingTripEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: ShoppingTripEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trips: List<ShoppingTripEntity>)

    @Update
    suspend fun updateTrip(trip: ShoppingTripEntity)

    @Delete
    suspend fun deleteTrip(trip: ShoppingTripEntity)
}

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_items WHERE tripId = :tripId ORDER BY isPurchased ASC, id ASC")
    fun getItemsForTrip(tripId: Long): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingItemEntity>)

    @Update
    suspend fun updateItem(item: ShoppingItemEntity)

    @Delete
    suspend fun deleteItem(item: ShoppingItemEntity)
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY ringIndex ASC, id ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(habits: List<HabitEntity>)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY id ASC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<AccountEntity>)

    @Update
    suspend fun updateAccount(account: AccountEntity)
}
