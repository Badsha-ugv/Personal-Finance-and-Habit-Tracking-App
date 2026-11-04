package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SeedData
import com.example.ui.viewmodel.CalmClarityViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Calm Clarity", appName)
    }

    @Test
    fun `verify formatTaka formats currency with Bangladeshi Taka symbol and masks properly`() {
        val formattedLive = CalmClarityViewModel.formatTaka(4775000.0, isMasked = false)
        assertTrue("Must include Taka symbol", formattedLive.startsWith("৳"))
        assertTrue("Must format digits", formattedLive.contains("4,775,000"))

        val formattedMasked = CalmClarityViewModel.formatTaka(4775000.0, isMasked = true)
        assertEquals("৳ ••••••", formattedMasked)
    }

    @Test
    fun `verify initial seed accounts and transactions are populated`() {
        val accounts = SeedData.initialAccounts
        assertEquals(4, accounts.size)

        val txs = SeedData.initialTransactions()
        assertTrue("Transactions must have initial records", txs.isNotEmpty())

        val loans = SeedData.initialLoans()
        assertTrue("Loans must have initial records", loans.isNotEmpty())

        val habits = SeedData.initialHabits
        assertEquals(3, habits.size)

        val counterparts = SeedData.initialCounterparts
        assertEquals(3, counterparts.size)

        val ledgerEntries = SeedData.initialLedgerEntries()
        assertTrue("Ledger entries should have initial history", ledgerEntries.isNotEmpty())

        val shoppingTrips = SeedData.initialShoppingTrips
        assertEquals(2, shoppingTrips.size)
    }
}
