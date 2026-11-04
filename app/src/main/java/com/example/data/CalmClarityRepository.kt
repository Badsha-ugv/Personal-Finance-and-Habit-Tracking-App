package com.example.data

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CalmClarityRepository(
    private val transactionDao: TransactionDao,
    private val loanDao: LoanDao,
    private val habitDao: HabitDao,
    private val accountDao: AccountDao,
    private val counterpartDao: CounterpartDao,
    private val loanLedgerDao: LoanLedgerDao,
    private val shoppingTripDao: ShoppingTripDao,
    private val shoppingItemDao: ShoppingItemDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val recentTransactions: Flow<List<TransactionEntity>> = transactionDao.getRecentTransactions(8)
    val allLoans: Flow<List<LoanEntity>> = loanDao.getAllLoans()
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()
    val allCounterparts: Flow<List<CounterpartEntity>> = counterpartDao.getAllCounterparts()
    val allLedgerEntries: Flow<List<LoanLedgerEntryEntity>> = loanLedgerDao.getAllEntries()
    val allShoppingTrips: Flow<List<ShoppingTripEntity>> = shoppingTripDao.getAllTrips()

    fun getLedgerEntriesForCounterpart(counterpartId: Long): Flow<List<LoanLedgerEntryEntity>> =
        loanLedgerDao.getEntriesForCounterpart(counterpartId)

    fun getShoppingItemsForTrip(tripId: Long): Flow<List<ShoppingItemEntity>> =
        shoppingItemDao.getItemsForTrip(tripId)

    suspend fun checkAndSeedDatabase() {
        val existingAccounts = accountDao.getAllAccounts().first()
        if (existingAccounts.isEmpty()) {
            accountDao.insertAll(SeedData.initialAccounts)
            transactionDao.insertAll(SeedData.initialTransactions())
            loanDao.insertAll(SeedData.initialLoans())
            habitDao.insertAll(SeedData.initialHabits)
            counterpartDao.insertAll(SeedData.initialCounterparts)
            loanLedgerDao.insertAll(SeedData.initialLedgerEntries())
            shoppingTripDao.insertAll(SeedData.initialShoppingTrips)
            shoppingItemDao.insertAll(SeedData.initialShoppingItems)
        } else {
            // Check if new tables need seed
            val existingCounterparts = counterpartDao.getAllCounterparts().first()
            if (existingCounterparts.isEmpty()) {
                counterpartDao.insertAll(SeedData.initialCounterparts)
                loanLedgerDao.insertAll(SeedData.initialLedgerEntries())
                shoppingTripDao.insertAll(SeedData.initialShoppingTrips)
                shoppingItemDao.insertAll(SeedData.initialShoppingItems)
            }
        }
    }

    suspend fun resetAllDataToDefault() {
        accountDao.insertAll(SeedData.initialAccounts)
        transactionDao.insertAll(SeedData.initialTransactions())
        loanDao.insertAll(SeedData.initialLoans())
        habitDao.insertAll(SeedData.initialHabits)
        counterpartDao.insertAll(SeedData.initialCounterparts)
        loanLedgerDao.insertAll(SeedData.initialLedgerEntries())
        shoppingTripDao.insertAll(SeedData.initialShoppingTrips)
        shoppingItemDao.insertAll(SeedData.initialShoppingItems)
    }

    suspend fun addTransaction(
        title: String,
        subtitle: String,
        category: String,
        amount: Double,
        type: String,
        accountName: String
    ): Long {
        val transaction = TransactionEntity(
            title = title,
            subtitle = subtitle,
            category = category,
            amount = amount,
            type = type,
            accountName = accountName,
            timestamp = System.currentTimeMillis()
        )
        val id = transactionDao.insertTransaction(transaction)

        val accounts = accountDao.getAllAccounts().first()
        val account = accounts.find { it.name.equals(accountName, ignoreCase = true) }
        if (account != null) {
            val updatedBalance = if (type == "INCOME") {
                account.balance + amount
            } else {
                account.balance - amount
            }
            accountDao.updateAccount(account.copy(balance = updatedBalance, lastUpdated = System.currentTimeMillis()))
        }
        return id
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    // --- Counterpart & Loan Ledger ---

    suspend fun addCounterpart(name: String, phoneOrEmail: String, notes: String): Long {
        val initials = name.trim().split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .ifEmpty { "CP" }

        val entity = CounterpartEntity(
            name = name,
            initials = initials,
            phoneOrEmail = phoneOrEmail,
            notes = notes
        )
        return counterpartDao.insertCounterpart(entity)
    }

    suspend fun deleteCounterpart(counterpart: CounterpartEntity) {
        counterpartDao.deleteCounterpart(counterpart)
    }

    suspend fun addLedgerEntry(
        counterpartId: Long,
        type: String,
        amount: Double,
        notes: String
    ): Long {
        val entry = LoanLedgerEntryEntity(
            counterpartId = counterpartId,
            type = type,
            amount = amount,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        return loanLedgerDao.insertEntry(entry)
    }

    suspend fun deleteLedgerEntry(entry: LoanLedgerEntryEntity) {
        loanLedgerDao.deleteEntry(entry)
    }

    // Single loan table backward compatibility
    suspend fun addLoan(
        counterpartName: String,
        direction: String,
        amount: Double,
        dueDaysFromNow: Int,
        notes: String
    ): Long {
        val initials = counterpartName.trim().split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .ifEmpty { "EX" }

        val dueDate = System.currentTimeMillis() + (dueDaysFromNow.coerceAtLeast(1) * 86400 * 1000L)
        val loan = LoanEntity(
            counterpartName = counterpartName,
            counterpartAvatarInitials = initials,
            direction = direction,
            amount = amount,
            returnDueDate = dueDate,
            isSettled = false,
            notes = notes
        )
        val id = loanDao.insertLoan(loan)

        // Also register in Counterparts & Ledger
        val counterparts = counterpartDao.getAllCounterparts().first()
        val existingCp = counterparts.find { it.name.equals(counterpartName, ignoreCase = true) }
        val cpId = existingCp?.id ?: addCounterpart(counterpartName, "", notes)
        addLedgerEntry(cpId, direction, amount, notes)

        return id
    }

    suspend fun toggleLoanSettled(loan: LoanEntity) {
        val updated = loan.copy(isSettled = !loan.isSettled)
        loanDao.updateLoan(updated)
    }

    suspend fun deleteLoan(loan: LoanEntity) {
        loanDao.deleteLoan(loan)
    }

    // --- Shopping Trips & Items ---

    suspend fun addShoppingTrip(title: String, budget: Double, targetAccount: String): Long {
        val trip = ShoppingTripEntity(
            title = title,
            budget = budget,
            totalSpent = 0.0,
            isCompleted = false,
            targetAccountName = targetAccount,
            createdDate = System.currentTimeMillis()
        )
        return shoppingTripDao.insertTrip(trip)
    }

    suspend fun deleteShoppingTrip(trip: ShoppingTripEntity) {
        shoppingTripDao.deleteTrip(trip)
    }

    suspend fun addShoppingItem(tripId: Long, name: String, quantity: String, estPrice: Double): Long {
        val item = ShoppingItemEntity(
            tripId = tripId,
            name = name,
            quantity = quantity,
            estimatedPrice = estPrice,
            actualPrice = estPrice,
            isPurchased = false
        )
        return shoppingItemDao.insertItem(item)
    }

    suspend fun toggleShoppingItemPurchased(item: ShoppingItemEntity) {
        val updated = item.copy(isPurchased = !item.isPurchased)
        shoppingItemDao.updateItem(updated)
    }

    suspend fun updateShoppingItem(item: ShoppingItemEntity) {
        shoppingItemDao.updateItem(item)
    }

    suspend fun deleteShoppingItem(item: ShoppingItemEntity) {
        shoppingItemDao.deleteItem(item)
    }

    suspend fun completeShoppingTrip(
        trip: ShoppingTripEntity,
        finalTotal: Double,
        deductFromFinance: Boolean
    ) {
        val updatedTrip = trip.copy(
            isCompleted = true,
            totalSpent = finalTotal,
            completedDate = System.currentTimeMillis()
        )
        shoppingTripDao.updateTrip(updatedTrip)

        if (deductFromFinance && finalTotal > 0) {
            addTransaction(
                title = "Shopping: ${trip.title}",
                subtitle = "Market Run Completed",
                category = "Market",
                amount = finalTotal,
                type = "EXPENSE",
                accountName = trip.targetAccountName
            )
        }
    }

    // --- Habits ---

    suspend fun toggleHabitCompleted(habit: HabitEntity) {
        val newCompleted = !habit.completedToday
        val newStreak = if (newCompleted) habit.currentStreakDays + 1 else (habit.currentStreakDays - 1).coerceAtLeast(0)
        val newCompletedMinutes = if (newCompleted) habit.targetMinutes else (habit.completedMinutes / 2)
        val updated = habit.copy(
            completedToday = newCompleted,
            currentStreakDays = newStreak,
            completedMinutes = newCompletedMinutes
        )
        habitDao.updateHabit(updated)
    }

    suspend fun addHabit(
        name: String,
        category: String,
        targetMinutes: Int,
        colorHex: String,
        ringIndex: Int
    ): Long {
        val habit = HabitEntity(
            name = name,
            category = category,
            targetMinutes = targetMinutes,
            completedMinutes = 0,
            completedToday = false,
            currentStreakDays = 0,
            ringIndex = ringIndex,
            colorHex = colorHex
        )
        return habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.deleteHabit(habit)
    }
}
