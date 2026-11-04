package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalmClarityRepository
import com.example.data.model.AccountEntity
import com.example.data.model.CounterpartEntity
import com.example.data.model.HabitEntity
import com.example.data.model.LoanEntity
import com.example.data.model.LoanLedgerEntryEntity
import com.example.data.model.ShoppingItemEntity
import com.example.data.model.ShoppingTripEntity
import com.example.data.model.TransactionEntity
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class CounterpartSummary(
    val counterpart: CounterpartEntity,
    val totalLent: Double,
    val totalBorrowed: Double,
    val totalRepaidByThem: Double,
    val totalRepaidByYou: Double,
    val netBalance: Double // > 0: they owe you; < 0: you owe them
)

data class CalmClarityUiState(
    val isPrivacyMasked: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val currentTab: NavigationTab = NavigationTab.OVERVIEW,
    val accounts: List<AccountEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val loans: List<LoanEntity> = emptyList(),
    val counterparts: List<CounterpartSummary> = emptyList(),
    val allLedgerEntries: List<LoanLedgerEntryEntity> = emptyList(),
    val shoppingTrips: List<ShoppingTripEntity> = emptyList(),
    val habits: List<HabitEntity> = emptyList(),
    val netLiquidity: Double = 0.0,
    val totalInflow: Double = 0.0,
    val totalOutflow: Double = 0.0,
    val totalLent: Double = 0.0,
    val totalBorrowed: Double = 0.0,
    val overallHabitPercent: Int = 0
)

enum class NavigationTab(val label: String) {
    OVERVIEW("Overview"),
    FINANCES("Finances"),
    LOANS("Loans"),
    SHOPPING("Shopping"),
    ANALYTICS("Analytics")
}

@OptIn(ExperimentalCoroutinesApi::class)
class CalmClarityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CalmClarityRepository

    private val _isPrivacyMasked = MutableStateFlow(false)
    val isPrivacyMasked: StateFlow<Boolean> = _isPrivacyMasked.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _currentTab = MutableStateFlow(NavigationTab.OVERVIEW)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _showQuickAddSheet = MutableStateFlow(false)
    val showQuickAddSheet: StateFlow<Boolean> = _showQuickAddSheet.asStateFlow()

    private val _selectedQuickAddType = MutableStateFlow("TRANSACTION")
    val selectedQuickAddType: StateFlow<String> = _selectedQuickAddType.asStateFlow()

    private val _showProfileSheet = MutableStateFlow(false)
    val showProfileSheet: StateFlow<Boolean> = _showProfileSheet.asStateFlow()

    // Active detail drilldowns
    private val _selectedCounterpartId = MutableStateFlow<Long?>(null)
    val selectedCounterpartId: StateFlow<Long?> = _selectedCounterpartId.asStateFlow()

    private val _selectedTripId = MutableStateFlow<Long?>(null)
    val selectedTripId: StateFlow<Long?> = _selectedTripId.asStateFlow()

    val currentTripItems: StateFlow<List<ShoppingItemEntity>> = _selectedTripId.flatMapLatest { tripId ->
        if (tripId != null) repository.getShoppingItemsForTrip(tripId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentCounterpartEntries: StateFlow<List<LoanLedgerEntryEntity>> = _selectedCounterpartId.flatMapLatest { cpId ->
        if (cpId != null) repository.getLedgerEntriesForCounterpart(cpId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CalmClarityRepository(
            transactionDao = db.transactionDao(),
            loanDao = db.loanDao(),
            habitDao = db.habitDao(),
            accountDao = db.accountDao(),
            counterpartDao = db.counterpartDao(),
            loanLedgerDao = db.loanLedgerDao(),
            shoppingTripDao = db.shoppingTripDao(),
            shoppingItemDao = db.shoppingItemDao()
        )

        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }
    }

    private val _uiCoreFlow = combine(
        _isPrivacyMasked,
        _themeMode,
        _currentTab,
        repository.allAccounts
    ) { masked, theme, tab, accounts ->
        Quadruple(masked, theme, tab, accounts)
    }

    private val _uiFinanceFlow = combine(
        repository.allTransactions,
        repository.allLoans,
        repository.allHabits
    ) { txs, loans, habits ->
        Triple(txs, loans, habits)
    }

    private val _uiLedgerAndShopFlow = combine(
        repository.allCounterparts,
        repository.allLedgerEntries,
        repository.allShoppingTrips
    ) { counterparts, ledgerEntries, shoppingTrips ->
        Triple(counterparts, ledgerEntries, shoppingTrips)
    }

    val uiState: StateFlow<CalmClarityUiState> = combine(
        _uiCoreFlow,
        _uiFinanceFlow,
        _uiLedgerAndShopFlow
    ) { core, finance, ledgerAndShop ->
        val (masked, theme, tab, accounts) = core
        val (txs, loans, habits) = finance
        val (counterpartsList, ledgerEntries, shoppingTrips) = ledgerAndShop

        val net = accounts.sumOf { it.balance }
        val inflow = txs.filter { it.type == "INCOME" }.sumOf { it.amount }
        val outflow = txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }

        // Compute counterpart summaries
        val counterpartSummaries = counterpartsList.map { cp ->
            val cpEntries = ledgerEntries.filter { it.counterpartId == cp.id }
            val lent = cpEntries.filter { it.type == "LENT" }.sumOf { it.amount }
            val borrowed = cpEntries.filter { it.type == "BORROWED" }.sumOf { it.amount }
            val repaidByThem = cpEntries.filter { it.type == "REPAID_BY_THEM" }.sumOf { it.amount }
            val repaidByYou = cpEntries.filter { it.type == "REPAID_BY_YOU" }.sumOf { it.amount }
            val netBal = (lent - repaidByThem) - (borrowed - repaidByYou)
            CounterpartSummary(
                counterpart = cp,
                totalLent = lent,
                totalBorrowed = borrowed,
                totalRepaidByThem = repaidByThem,
                totalRepaidByYou = repaidByYou,
                netBalance = netBal
            )
        }

        val totalLentCalculated = counterpartSummaries.filter { it.netBalance > 0 }.sumOf { it.netBalance }
            .ifZeroFallback(loans.filter { !it.isSettled && it.direction == "LENT" }.sumOf { it.amount })

        val totalBorrowedCalculated = counterpartSummaries.filter { it.netBalance < 0 }.sumOf { -it.netBalance }
            .ifZeroFallback(loans.filter { !it.isSettled && it.direction == "BORROWED" }.sumOf { it.amount })

        val habitPercent = if (habits.isNotEmpty()) {
            val totalTarget = habits.sumOf { it.targetMinutes }.coerceAtLeast(1)
            val totalDone = habits.sumOf {
                if (it.completedToday) it.targetMinutes else it.completedMinutes
            }
            ((totalDone.toDouble() / totalTarget.toDouble()) * 100).toInt().coerceIn(0, 100)
        } else 0

        CalmClarityUiState(
            isPrivacyMasked = masked,
            themeMode = theme,
            currentTab = tab,
            accounts = accounts,
            transactions = txs,
            recentTransactions = txs.take(6),
            loans = loans,
            counterparts = counterpartSummaries,
            allLedgerEntries = ledgerEntries,
            shoppingTrips = shoppingTrips,
            habits = habits,
            netLiquidity = net,
            totalInflow = inflow,
            totalOutflow = outflow,
            totalLent = totalLentCalculated,
            totalBorrowed = totalBorrowedCalculated,
            overallHabitPercent = habitPercent
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalmClarityUiState()
    )

    fun togglePrivacyMask() {
        _isPrivacyMasked.value = !_isPrivacyMasked.value
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
        // Clear drilldown states when switching tabs
        _selectedCounterpartId.value = null
        _selectedTripId.value = null
    }

    fun openQuickAdd(initialType: String = "TRANSACTION") {
        _selectedQuickAddType.value = initialType
        _showQuickAddSheet.value = true
    }

    fun closeQuickAdd() {
        _showQuickAddSheet.value = false
    }

    fun setQuickAddType(type: String) {
        _selectedQuickAddType.value = type
    }

    fun toggleProfileSheet(open: Boolean) {
        _showProfileSheet.value = open
    }

    fun selectCounterpart(id: Long?) {
        _selectedCounterpartId.value = id
    }

    fun selectShoppingTrip(id: Long?) {
        _selectedTripId.value = id
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetAllDataToDefault()
        }
    }

    // --- Transactions ---

    fun addTransaction(
        title: String,
        subtitle: String,
        category: String,
        amount: Double,
        type: String,
        accountName: String
    ) {
        viewModelScope.launch {
            repository.addTransaction(title, subtitle, category, amount, type, accountName)
            closeQuickAdd()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // --- Counterpart & Loan Ledger ---

    fun addCounterpart(name: String, phoneOrEmail: String, notes: String) {
        viewModelScope.launch {
            val id = repository.addCounterpart(name, phoneOrEmail, notes)
            selectCounterpart(id)
        }
    }

    fun deleteCounterpart(counterpart: CounterpartEntity) {
        viewModelScope.launch {
            repository.deleteCounterpart(counterpart)
            if (_selectedCounterpartId.value == counterpart.id) {
                _selectedCounterpartId.value = null
            }
        }
    }

    fun addLedgerEntry(counterpartId: Long, type: String, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.addLedgerEntry(counterpartId, type, amount, notes)
        }
    }

    fun deleteLedgerEntry(entry: LoanLedgerEntryEntity) {
        viewModelScope.launch {
            repository.deleteLedgerEntry(entry)
        }
    }

    fun addLoan(
        counterpartName: String,
        direction: String,
        amount: Double,
        dueDaysFromNow: Int,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addLoan(counterpartName, direction, amount, dueDaysFromNow, notes)
            closeQuickAdd()
        }
    }

    fun toggleLoanSettled(loan: LoanEntity) {
        viewModelScope.launch {
            repository.toggleLoanSettled(loan)
        }
    }

    fun deleteLoan(loan: LoanEntity) {
        viewModelScope.launch {
            repository.deleteLoan(loan)
        }
    }

    // --- Shopping Trips & Items ---

    fun addShoppingTrip(title: String, budget: Double, targetAccount: String) {
        viewModelScope.launch {
            val id = repository.addShoppingTrip(title, budget, targetAccount)
            selectShoppingTrip(id)
            closeQuickAdd()
        }
    }

    fun deleteShoppingTrip(trip: ShoppingTripEntity) {
        viewModelScope.launch {
            repository.deleteShoppingTrip(trip)
            if (_selectedTripId.value == trip.id) {
                _selectedTripId.value = null
            }
        }
    }

    fun addShoppingItem(tripId: Long, name: String, quantity: String, estPrice: Double) {
        viewModelScope.launch {
            repository.addShoppingItem(tripId, name, quantity, estPrice)
        }
    }

    fun toggleShoppingItemPurchased(item: ShoppingItemEntity) {
        viewModelScope.launch {
            repository.toggleShoppingItemPurchased(item)
        }
    }

    fun updateShoppingItem(item: ShoppingItemEntity) {
        viewModelScope.launch {
            repository.updateShoppingItem(item)
        }
    }

    fun deleteShoppingItem(item: ShoppingItemEntity) {
        viewModelScope.launch {
            repository.deleteShoppingItem(item)
        }
    }

    fun completeShoppingTrip(trip: ShoppingTripEntity, finalTotal: Double, deductFromFinance: Boolean) {
        viewModelScope.launch {
            repository.completeShoppingTrip(trip, finalTotal, deductFromFinance)
        }
    }

    // --- Habits ---

    fun toggleHabitCompleted(habit: HabitEntity) {
        viewModelScope.launch {
            repository.toggleHabitCompleted(habit)
        }
    }

    fun addHabit(
        name: String,
        category: String,
        targetMinutes: Int,
        colorHex: String,
        ringIndex: Int
    ) {
        viewModelScope.launch {
            repository.addHabit(name, category, targetMinutes, colorHex, ringIndex)
            closeQuickAdd()
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    companion object {
        fun formatTaka(amount: Double, isMasked: Boolean): String {
            if (isMasked) return "৳ ••••••"
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            formatter.maximumFractionDigits = 0
            return "৳ " + formatter.format(amount)
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

private fun Double.ifZeroFallback(fallback: Double): Double {
    return if (this == 0.0) fallback else this
}
