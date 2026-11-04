package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subtitle: String = "",
    val category: String,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val accountName: String, // "Prime Treasury", "Operating Liquid", "Cash Reserve"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val counterpartName: String,
    val counterpartAvatarInitials: String,
    val direction: String, // "LENT" (receivable) or "BORROWED" (liability)
    val amount: Double,
    val returnDueDate: Long,
    val isSettled: Boolean = false,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "counterparts")
data class CounterpartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val initials: String,
    val phoneOrEmail: String = "",
    val notes: String = "",
    val createdDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "loan_ledger_entries")
data class LoanLedgerEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val counterpartId: Long,
    val type: String, // "LENT", "BORROWED", "REPAID_BY_THEM", "REPAID_BY_YOU"
    val amount: Double,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "shopping_trips")
data class ShoppingTripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val budget: Double,
    val totalSpent: Double = 0.0,
    val isCompleted: Boolean = false,
    val targetAccountName: String = "Operating Liquid",
    val createdDate: Long = System.currentTimeMillis(),
    val completedDate: Long? = null
)

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val name: String,
    val quantity: String = "1",
    val estimatedPrice: Double = 0.0,
    val actualPrice: Double = 0.0,
    val isPurchased: Boolean = false
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // "Executive Focus", "Physical Stamina", "Strategic Review"
    val currentStreakDays: Int = 0,
    val completedToday: Boolean = false,
    val ringIndex: Int = 0, // 0 = Outer, 1 = Middle, 2 = Inner
    val targetMinutes: Int = 60,
    val completedMinutes: Int = 0,
    val colorHex: String = "#6366F1"
)

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val accountType: String, // "Treasury", "Operating", "Cash", "Investment"
    val balance: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)
