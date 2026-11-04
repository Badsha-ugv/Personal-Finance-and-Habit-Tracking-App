# Calm Clarity — Comprehensive System Upgrade & Feature Expansion

This update transforms **Calm Clarity** into a complete executive management suite by introducing full **Contact Loan Ledgers** (two-way multi-transaction debt tracking with partial repayments), a dedicated **Market & Shopping Planner** (budgeting, item checklist, and instant expense sync), a visual **Executive Analytics** engine, an **Account & Profile** manager with explicit Light/Dark theme switching, and bug fixes for text field readability and layout wrapping.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions were confirmed with the user:

- **Two-Way Contact Loan Ledger**: Clicking into any loan or counterpart opens a dedicated Contact Ledger detailing all historic transactions (Lent, Borrowed, Partial Repayments, Settled increments) and calculating real-time net balance ("You are owed ৳ X" or "You owe ৳ Y").
- **Market Shopping List with Expense Sync**: Supports creating shopping trips with target budgets, item checklists (quantity, estimated price, bought state), and a 1-tap checkout prompt to record the total spent into the Finance accounts.
- **5-Tab Navigation**: `Overview`, `Finances`, `Loans`, `Shopping`, and `Analytics`, with an executive Profile modal in the top header.
- **Dark & Light Mode Polish**: Complete input field color specification preventing white-on-white text issues, accompanied by an in-app theme toggle.
- **Transaction Item Layout Fix**: Resolving the narrow-column vertical text clipping seen on account name tags in transaction cards.

---

## 1. Overview & Core Concept

Ambitious operators need to track complex bilateral relationships (lending and borrowing with the same person over time with partial repayments) and plan market purchases against set budgets. This update broadens Calm Clarity's scope from static single-event loans to interactive living ledgers, adds pre-market shopping workflows, and provides high-level financial and habit analytics.

---

## 2. User Experience & Visual Design

### 1. Two-Way Contact Loan Ledger (`ContactLedgerScreen`)
- **Contact Overview**: Displays all registered counterparts (e.g. Tariqul Islam, Apex Syndicate, Farhan Ahmed) with their net balance indicator (Emerald for money owed to you, Amber/Coral for money you owe).
- **Ledger Detail View**:
  - Hero card with counterpart avatar, net balance, and breakdown of total lent vs total repaid.
  - Chronological transaction log: each entry shows date, type (`LENT`, `BORROWED`, `REPAID_BY_THEM`, `REPAID_BY_YOU`), amount in `৳`, and notes.
  - Action buttons: "Lend Money", "Borrow Money", "Record Repayment" with amount, date, and note.
  - Option to mark entire balance settled.

### 2. Market & Shopping Planner (`ShoppingScreen`)
- **Active Trips & History**: Displays shopping trips (e.g., "Weekly Executive Groceries", "Q4 Hardware & Tech Setup") with budget, total spent, and status (`Planning`, `In Progress`, `Completed`).
- **Trip Detail / Checklist**:
  - Target budget bar comparing total planned vs total bought.
  - Add items with item name, quantity, estimated cost, and actual cost.
  - Interactive checkbox to check off items while in the market.
  - "Complete Shopping" button: allows entering the final bill total and automatically prompts to record it as an expense in Prime Treasury or Operating Liquid accounts.

### 3. Executive Analytics (`AnalyticsScreen`)
- **Cash Flow Dynamics**: Monthly income vs expense ratio, net savings rate, and average daily burn.
- **Spending by Category**: Visual distribution bars for Software, Investment, Office, Travel, Health, Dining, and Tax.
- **Loan Portfolio Breakdown**: Net credit exposure (Total Lent minus Total Borrowed), repayment velocity, and counterpart concentration.
- **Habit Discipline Index**: Cumulative streak records, weekly adherence percentages, and ring consistency.

### 4. Profile & Account Settings (`ProfileSheet`)
- Tapping the avatar ("HB") in the top header opens the executive profile drawer.
- Theme selection: System Default, Light Mode, or Deep Slate Dark Mode.
- Account information (Name: "HB Badsha", Email: "hb.badsha01@gmail.com", Currency: Bangladeshi Taka ৳).
- Liquidity account overview with quick balances and data reset/seed reload options.

### 5. Contrast & Visual Fixes
- **OutlinedTextField Contrast**: Fixes container, text, and label colors across both Light and Dark modes to ensure dark text on light containers in Light Mode, and crisp white text on slate containers in Dark Mode.
- **Transaction Card Spacing**: Replaces constrained horizontal layout in `TransactionRowItem` with fluid flex rows to prevent character-by-character vertical text squishing.

---

## 3. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                      UI Scaffold                       │
│  Top App Bar: Greeting + Privacy Eye + Profile/Theme   │
│  Bottom Nav: Overview | Finances | Loans | Shop | Data │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────┴────────────────────────────┐
│                    Screen Composables                  │
│ ├─ DashboardScreen (Net balance, rings, obligations)   │
│ ├─ FinancesScreen (Accounts, cashflow, tx filters)     │
│ ├─ LoansScreen & ContactLedgerScreen (Two-way ledger)  │
│ ├─ ShoppingScreen & ShoppingTripDetail (Checklist)     │
│ ├─ AnalyticsScreen (Category bars, habit metrics)      │
│ └─ ProfileSheet (Theme toggle, user info, accounts)    │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                 CalmClarityViewModel                   │
│ (Theme state, Ledger CRUD, Shopping CRUD, Analytics)   │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                CalmClarityRepository                   │
│   (Room Database + Seed Preload for all entities)      │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                  Room Database Entities                │
│  ├─ AccountEntity       ├─ TransactionEntity           │
│  ├─ CounterpartEntity   ├─ LoanLedgerEntryEntity       │
│  ├─ ShoppingTripEntity  ├─ ShoppingItemEntity          │
│  └─ HabitEntity                                        │
└────────────────────────────────────────────────────────┘
```

### New Database Entities

1. **CounterpartEntity**:
   - `id: Long`, `name: String`, `initials: String`, `phoneOrEmail: String`, `notes: String`
2. **LoanLedgerEntryEntity**:
   - `id: Long`, `counterpartId: Long`, `type: String` ("LENT", "BORROWED", "REPAID_BY_THEM", "REPAID_BY_YOU"), `amount: Double`, `notes: String`, `timestamp: Long`
3. **ShoppingTripEntity**:
   - `id: Long`, `title: String`, `budget: Double`, `totalSpent: Double`, `isCompleted: Boolean`, `createdDate: Long`, `completedDate: Long?`
4. **ShoppingItemEntity**:
   - `id: Long`, `tripId: Long`, `name: String`, `quantity: String`, `estimatedPrice: Double`, `actualPrice: Double`, `isPurchased: Boolean`

---

## 4. Execution Steps

1. **Database Expansion**: Create `CounterpartEntity`, `LoanLedgerEntryEntity`, `ShoppingTripEntity`, and `ShoppingItemEntity` with corresponding DAOs and Room database version update.
2. **Repository & Seed Data**: Expand repository with bilateral loan calculation methods (net balance, transaction history, partial repayments) and shopping list CRUD; add rich seed data for counterparts and shopping lists.
3. **ViewModel Enhancement**: Wire state flows for active counterpart ledger, shopping trips, theme mode (System/Light/Dark), and analytics metrics.
4. **UI Implementation**:
   - `ContactLedgerScreen`: Detailed history for each person with quick actions to add loan or repayment.
   - `ShoppingScreen`: Interactive market checklist, budget tracker, and expense sync dialog.
   - `AnalyticsScreen`: Spending categories, liquidity margins, and habit discipline charts.
   - `ProfileSheet`: Theme switch, profile details, and account balances.
   - `TransactionRowItem` fix: Proper layout without vertical text truncation.
   - `TextField` styling fix: Clean high-contrast colors in all modes.
5. **Verification**: Run `compile_applet` and update tests to verify all new features build and pass.
