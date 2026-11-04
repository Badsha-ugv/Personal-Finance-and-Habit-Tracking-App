package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ProfileSheet
import com.example.ui.components.QuickEntryBottomSheet
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.ContactLedgerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinancesScreen
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.LoansScreen
import com.example.ui.screens.ShoppingScreen
import com.example.ui.theme.CalmClarityTheme
import com.example.ui.theme.DeepSlateHero
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.CalmClarityViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    private val viewModel: CalmClarityViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val showQuickAdd by viewModel.showQuickAddSheet.collectAsStateWithLifecycle()
            val quickAddType by viewModel.selectedQuickAddType.collectAsStateWithLifecycle()
            val showProfile by viewModel.showProfileSheet.collectAsStateWithLifecycle()

            val selectedCpId by viewModel.selectedCounterpartId.collectAsStateWithLifecycle()
            val cpEntries by viewModel.currentCounterpartEntries.collectAsStateWithLifecycle()

            val selectedTripId by viewModel.selectedTripId.collectAsStateWithLifecycle()
            val tripItems by viewModel.currentTripItems.collectAsStateWithLifecycle()

            val quickAddSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val profileSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            CalmClarityTheme(themeMode = uiState.themeMode) {
                // BackHandler precedence:
                // 1. If in Counterpart ledger detail -> back to counterparts
                // 2. If in Shopping trip detail -> back to shopping trips
                // 3. If in secondary tab -> back to OVERVIEW
                BackHandler(enabled = selectedCpId != null || selectedTripId != null || uiState.currentTab != NavigationTab.OVERVIEW) {
                    when {
                        selectedCpId != null -> viewModel.selectCounterpart(null)
                        selectedTripId != null -> viewModel.selectShoppingTrip(null)
                        uiState.currentTab != NavigationTab.OVERVIEW -> viewModel.setTab(NavigationTab.OVERVIEW)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        CalmClarityBottomNavigation(
                            currentTab = uiState.currentTab,
                            onTabSelected = { viewModel.setTab(it) }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                val initial = when (uiState.currentTab) {
                                    NavigationTab.LOANS -> "LOAN"
                                    NavigationTab.SHOPPING -> "SHOPPING"
                                    else -> "TRANSACTION"
                                }
                                viewModel.openQuickAdd(initial)
                            },
                            containerColor = DeepSlateHero,
                            contentColor = Color.White,
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("main_fat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Quick Add Entry",
                                tint = Color(0xFF6BD8CB),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (selectedCpId != null) {
                            val counterpart = uiState.counterparts.find { it.counterpart.id == selectedCpId }?.counterpart
                            if (counterpart != null) {
                                ContactLedgerScreen(
                                    counterpart = counterpart,
                                    entries = cpEntries,
                                    isPrivacyMasked = uiState.isPrivacyMasked,
                                    onBack = { viewModel.selectCounterpart(null) },
                                    onAddEntry = { type, amount, notes ->
                                        viewModel.addLedgerEntry(counterpart.id, type, amount, notes)
                                    },
                                    onDeleteEntry = { viewModel.deleteLedgerEntry(it) }
                                )
                            } else {
                                viewModel.selectCounterpart(null)
                            }
                        } else {
                            AnimatedContent(
                                targetState = uiState.currentTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "screen_transition"
                            ) { targetTab ->
                                when (targetTab) {
                                    NavigationTab.OVERVIEW -> {
                                        DashboardScreen(
                                            state = uiState,
                                            onTogglePrivacy = { viewModel.togglePrivacyMask() },
                                            onNavigateTab = { viewModel.setTab(it) },
                                            onOpenProfile = { viewModel.toggleProfileSheet(true) },
                                            onQuickAdd = { viewModel.openQuickAdd(it) },
                                            onToggleLoanSettled = { viewModel.toggleLoanSettled(it) },
                                            onDeleteLoan = { viewModel.deleteLoan(it) },
                                            onDeleteTransaction = { viewModel.deleteTransaction(it) }
                                        )
                                    }
                                    NavigationTab.FINANCES -> {
                                        FinancesScreen(
                                            state = uiState,
                                            onQuickAddTransaction = { viewModel.openQuickAdd("TRANSACTION") },
                                            onDeleteTransaction = { viewModel.deleteTransaction(it) }
                                        )
                                    }
                                    NavigationTab.LOANS -> {
                                        LoansScreen(
                                            state = uiState,
                                            onOpenCounterpartLedger = { viewModel.selectCounterpart(it) },
                                            onAddCounterpart = { name, phone, notes -> viewModel.addCounterpart(name, phone, notes) },
                                            onQuickAddLoan = { viewModel.openQuickAdd("LOAN") },
                                            onToggleSettled = { viewModel.toggleLoanSettled(it) },
                                            onDeleteLoan = { viewModel.deleteLoan(it) }
                                        )
                                    }
                                    NavigationTab.SHOPPING -> {
                                        ShoppingScreen(
                                            state = uiState,
                                            selectedTripId = selectedTripId,
                                            currentTripItems = tripItems,
                                            onSelectTrip = { viewModel.selectShoppingTrip(it) },
                                            onAddTrip = { title, budget, account -> viewModel.addShoppingTrip(title, budget, account) },
                                            onDeleteTrip = { viewModel.deleteShoppingTrip(it) },
                                            onAddItem = { tripId, name, qty, price -> viewModel.addShoppingItem(tripId, name, qty, price) },
                                            onToggleItemPurchased = { viewModel.toggleShoppingItemPurchased(it) },
                                            onDeleteItem = { viewModel.deleteShoppingItem(it) },
                                            onCompleteTrip = { trip, finalTotal, deduct -> viewModel.completeShoppingTrip(trip, finalTotal, deduct) }
                                        )
                                    }
                                    NavigationTab.ANALYTICS -> {
                                        AnalyticsScreen(state = uiState)
                                    }
                                }
                            }
                        }
                    }

                    // Quick Entry Bottom Sheet
                    if (showQuickAdd) {
                        QuickEntryBottomSheet(
                            sheetState = quickAddSheetState,
                            initialTab = quickAddType,
                            onDismiss = { viewModel.closeQuickAdd() },
                            onAddTransaction = { title, subtitle, category, amount, type, account ->
                                viewModel.addTransaction(title, subtitle, category, amount, type, account)
                            },
                            onAddLoan = { counterpart, direction, amount, dueDays, notes ->
                                viewModel.addLoan(counterpart, direction, amount, dueDays, notes)
                            },
                            onAddHabit = { name, category, targetMins, color, ringIdx ->
                                viewModel.addHabit(name, category, targetMins, color, ringIdx)
                            },
                            onAddShoppingTrip = { title, budget, account ->
                                viewModel.addShoppingTrip(title, budget, account)
                            }
                        )
                    }

                    // Profile & Settings Bottom Sheet
                    if (showProfile) {
                        ProfileSheet(
                            sheetState = profileSheetState,
                            currentTheme = uiState.themeMode,
                            accounts = uiState.accounts,
                            isPrivacyMasked = uiState.isPrivacyMasked,
                            onDismiss = { viewModel.toggleProfileSheet(false) },
                            onSelectTheme = { viewModel.setThemeMode(it) },
                            onResetData = {
                                viewModel.resetData()
                                viewModel.toggleProfileSheet(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalmClarityBottomNavigation(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("calm_clarity_bottom_nav")
    ) {
        val navItems = listOf(
            Triple(NavigationTab.OVERVIEW, Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
            Triple(NavigationTab.FINANCES, Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
            Triple(NavigationTab.LOANS, Icons.Filled.SwapHoriz, Icons.Outlined.SwapHoriz),
            Triple(NavigationTab.SHOPPING, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
            Triple(NavigationTab.ANALYTICS, Icons.Filled.Analytics, Icons.Outlined.Analytics)
        )

        navItems.forEach { (tab, filledIcon, outlinedIcon) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
            )
        }
    }
}
