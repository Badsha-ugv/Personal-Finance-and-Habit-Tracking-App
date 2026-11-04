package com.example.data

import com.example.data.model.AccountEntity
import com.example.data.model.CounterpartEntity
import com.example.data.model.HabitEntity
import com.example.data.model.LoanEntity
import com.example.data.model.LoanLedgerEntryEntity
import com.example.data.model.ShoppingItemEntity
import com.example.data.model.ShoppingTripEntity
import com.example.data.model.TransactionEntity

object SeedData {
    val initialAccounts = listOf(
        AccountEntity(id = 1, name = "Prime Treasury", accountType = "Treasury", balance = 2850000.0),
        AccountEntity(id = 2, name = "Operating Liquid", accountType = "Operating", balance = 640000.0),
        AccountEntity(id = 3, name = "Investment Pool", accountType = "Investment", balance = 1200000.0),
        AccountEntity(id = 4, name = "Cash Reserve", accountType = "Cash", balance = 85000.0)
    )

    fun initialTransactions(): List<TransactionEntity> {
        val now = System.currentTimeMillis()
        val hour = 3600 * 1000L
        return listOf(
            TransactionEntity(
                id = 1,
                title = "Tech Venture Dividend",
                subtitle = "Q3 Distributed Yield",
                category = "Investment",
                amount = 350000.0,
                type = "INCOME",
                accountName = "Prime Treasury",
                timestamp = now - hour * 2
            ),
            TransactionEntity(
                id = 2,
                title = "AWS Infrastructure Tier",
                subtitle = "Compute & Cloud Storage",
                category = "Software",
                amount = 42500.0,
                type = "EXPENSE",
                accountName = "Operating Liquid",
                timestamp = now - hour * 6
            ),
            TransactionEntity(
                id = 3,
                title = "Advisory Retainer Fee",
                subtitle = "Chowdhury Holdings",
                category = "Income",
                amount = 220000.0,
                type = "INCOME",
                accountName = "Prime Treasury",
                timestamp = now - hour * 24
            ),
            TransactionEntity(
                id = 4,
                title = "Commercial Office Lease",
                subtitle = "Gulshan Avenue Tower",
                category = "Office",
                amount = 85000.0,
                type = "EXPENSE",
                accountName = "Operating Liquid",
                timestamp = now - hour * 30
            ),
            TransactionEntity(
                id = 5,
                title = "Executive Health Assessment",
                subtitle = "Comprehensive Screening",
                category = "Health",
                amount = 18500.0,
                type = "EXPENSE",
                accountName = "Operating Liquid",
                timestamp = now - hour * 48
            ),
            TransactionEntity(
                id = 6,
                title = "Private Aviation Transit",
                subtitle = "DAC-CGP Executive Route",
                category = "Travel",
                amount = 65000.0,
                type = "EXPENSE",
                accountName = "Operating Liquid",
                timestamp = now - hour * 72
            )
        )
    }

    fun initialLoans(): List<LoanEntity> {
        val now = System.currentTimeMillis()
        val day = 86400 * 1000L
        return listOf(
            LoanEntity(
                id = 1,
                counterpartName = "Tariqul Islam",
                counterpartAvatarInitials = "TI",
                direction = "LENT",
                amount = 170000.0,
                returnDueDate = now + (day * 12),
                isSettled = false,
                notes = "Net remaining balance (Orig: ৳ 250k, Repaid: ৳ 80k)"
            ),
            LoanEntity(
                id = 2,
                counterpartName = "Apex Capital Syndicate",
                counterpartAvatarInitials = "AC",
                direction = "BORROWED",
                amount = 350000.0,
                returnDueDate = now + (day * 30),
                isSettled = false,
                notes = "Net liability tranche (Orig: ৳ 500k, Repaid: ৳ 150k)"
            ),
            LoanEntity(
                id = 3,
                counterpartName = "Farhan Ahmed",
                counterpartAvatarInitials = "FA",
                direction = "LENT",
                amount = 80000.0,
                returnDueDate = now + (day * 3),
                isSettled = false,
                notes = "Short-term bridge balance (Orig: ৳ 120k, Repaid: ৳ 40k)"
            )
        )
    }

    val initialCounterparts = listOf(
        CounterpartEntity(
            id = 1,
            name = "Tariqul Islam",
            initials = "TI",
            phoneOrEmail = "+880 1711-098231",
            notes = "Cofounder & Tech Operating Partner"
        ),
        CounterpartEntity(
            id = 2,
            name = "Apex Capital Syndicate",
            initials = "AC",
            phoneOrEmail = "syndicate@apexcapital.bd",
            notes = "Structured Strategic Debt Facility"
        ),
        CounterpartEntity(
            id = 3,
            name = "Farhan Ahmed",
            initials = "FA",
            phoneOrEmail = "+880 1819-445522",
            notes = "Angel Syndication & Co-investor"
        )
    )

    fun initialLedgerEntries(): List<LoanLedgerEntryEntity> {
        val now = System.currentTimeMillis()
        val day = 86400 * 1000L
        return listOf(
            // Tariqul Islam
            LoanLedgerEntryEntity(
                id = 1,
                counterpartId = 1,
                type = "LENT",
                amount = 250000.0,
                notes = "Supplier hardware invoice bridge loan",
                timestamp = now - (day * 15)
            ),
            LoanLedgerEntryEntity(
                id = 2,
                counterpartId = 1,
                type = "REPAID_BY_THEM",
                amount = 80000.0,
                notes = "First tranche returned via City Bank wire",
                timestamp = now - (day * 4)
            ),
            // Apex Capital Syndicate
            LoanLedgerEntryEntity(
                id = 3,
                counterpartId = 2,
                type = "BORROWED",
                amount = 500000.0,
                notes = "Q4 expansion credit facility disbursement",
                timestamp = now - (day * 30)
            ),
            LoanLedgerEntryEntity(
                id = 4,
                counterpartId = 2,
                type = "REPAID_BY_YOU",
                amount = 150000.0,
                notes = "Principal amortization payment #1",
                timestamp = now - (day * 10)
            ),
            // Farhan Ahmed
            LoanLedgerEntryEntity(
                id = 5,
                counterpartId = 3,
                type = "LENT",
                amount = 120000.0,
                notes = "Short-term equity swap deposit",
                timestamp = now - (day * 7)
            ),
            LoanLedgerEntryEntity(
                id = 6,
                counterpartId = 3,
                type = "REPAID_BY_THEM",
                amount = 40000.0,
                notes = "Instant settlement transfer",
                timestamp = now - (day * 2)
            )
        )
    }

    val initialShoppingTrips = listOf(
        ShoppingTripEntity(
            id = 1,
            title = "Executive Market Run — Groceries & Supplies",
            budget = 15000.0,
            totalSpent = 4750.0,
            isCompleted = false,
            targetAccountName = "Operating Liquid"
        ),
        ShoppingTripEntity(
            id = 2,
            title = "Hardware & Desk Engineering",
            budget = 45000.0,
            totalSpent = 42500.0,
            isCompleted = true,
            targetAccountName = "Operating Liquid"
        )
    )

    val initialShoppingItems = listOf(
        // Trip 1
        ShoppingItemEntity(
            id = 1,
            tripId = 1,
            name = "Himalayan Mineral Salts & Spices",
            quantity = "2 packs",
            estimatedPrice = 1200.0,
            actualPrice = 1150.0,
            isPurchased = true
        ),
        ShoppingItemEntity(
            id = 2,
            tripId = 1,
            name = "Organic Cold-Pressed Olive Oil",
            quantity = "3 bottles",
            estimatedPrice = 3600.0,
            actualPrice = 3600.0,
            isPurchased = true
        ),
        ShoppingItemEntity(
            id = 3,
            tripId = 1,
            name = "Hydroponic Greens & Micro Herbs",
            quantity = "1 crate",
            estimatedPrice = 1800.0,
            actualPrice = 0.0,
            isPurchased = false
        ),
        ShoppingItemEntity(
            id = 4,
            tripId = 1,
            name = "Almond Milk & Artisan Yogurt",
            quantity = "4 packs",
            estimatedPrice = 2400.0,
            actualPrice = 0.0,
            isPurchased = false
        ),
        ShoppingItemEntity(
            id = 5,
            tripId = 1,
            name = "Fresh Salmon & Sea Bass Steaks",
            quantity = "2 kg",
            estimatedPrice = 4500.0,
            actualPrice = 0.0,
            isPurchased = false
        ),
        // Trip 2
        ShoppingItemEntity(
            id = 6,
            tripId = 2,
            name = "Thunderbolt 4 Ultra Dock",
            quantity = "1 unit",
            estimatedPrice = 22000.0,
            actualPrice = 21500.0,
            isPurchased = true
        ),
        ShoppingItemEntity(
            id = 7,
            tripId = 2,
            name = "Mechanical Wrist Rest & Felt Mat",
            quantity = "2 items",
            estimatedPrice = 4500.0,
            actualPrice = 4200.0,
            isPurchased = true
        ),
        ShoppingItemEntity(
            id = 8,
            tripId = 2,
            name = "Ergonomic Vertical Mouse",
            quantity = "1 unit",
            estimatedPrice = 15000.0,
            actualPrice = 13600.0,
            isPurchased = true
        )
    )

    val initialHabits = listOf(
        HabitEntity(
            id = 1,
            name = "Deep Focus Protocol",
            category = "Executive Focus",
            currentStreakDays = 14,
            completedToday = false,
            ringIndex = 0,
            targetMinutes = 120,
            completedMinutes = 90,
            colorHex = "#6366F1" // Indigo
        ),
        HabitEntity(
            id = 2,
            name = "Cardiovascular Stamina",
            category = "Physical Stamina",
            currentStreakDays = 8,
            completedToday = true,
            ringIndex = 1,
            targetMinutes = 45,
            completedMinutes = 45,
            colorHex = "#0D9488" // Teal
        ),
        HabitEntity(
            id = 3,
            name = "Strategic Liquidity Review",
            category = "Strategic Review",
            currentStreakDays = 21,
            completedToday = false,
            ringIndex = 2,
            targetMinutes = 30,
            completedMinutes = 20,
            colorHex = "#10B981" // Emerald
        )
    )
}
