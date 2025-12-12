## Revenue Statistics Quick Guide for Admin

### Accessing Revenue Statistics

1. **From Dashboard:**
   - Click on the yellow "Revenue" card that shows "View revenue statistics"
   - Or use the sidebar menu → "Revenue Statistics" link

2. **Direct URL:**
   - http://localhost:8080/CNW23N16/HotelBooking/admin/revenue

### Understanding the Views

#### **Monthly View** (Default)
- Shows revenue grouped by month for a selected year
- **Example:** See all revenue for each month in 2025
- Use case: Track seasonal trends and monthly performance

#### **Daily View**
- Shows revenue for each day in a selected month
- **Example:** See daily revenue for December 2025
- Use case: Identify peak booking days and daily performance

#### **Yearly View**
- Shows revenue grouped by year (all-time data)
- Use case: Compare year-over-year revenue trends

### How to Use Filters

1. **Select Year:**
   - Choose any year from 2020 to current year
   - Available in Month and Day views

2. **Select Month:** (Only in Daily View)
   - Choose any month from January to December
   - Shows all days in that month

3. **Click "Filter" Button:**
   - Applies your selection
   - Updates all cards, charts, and tables

### Reading the Statistics

**Summary Cards (Top Section):**
- **Total Revenue (₫)**: Sum of all paid bookings for the period
- **Total Bookings**: Count of completed bookings
- **Average Per Booking (₫)**: Revenue ÷ Bookings

**Revenue Chart:**
- **Bars**: Total revenue (in Vietnamese Dong ₫)
- **Line**: Number of bookings
- Hover over bars/points to see exact values

**Statistics Table:**
- **Date/Period**: Day, Month, or Year depending on view
- **Revenue**: Total amount for that period
- **Bookings**: Number of completed bookings
- **Avg/Booking**: Average revenue per booking

### Key Features

✓ **Only counts PAID bookings** - Unpaid orders are excluded

✓ **Date-based grouping** - Uses check-in date for calculation

✓ **Responsive charts** - Interactive Chart.js visualization

✓ **Professional formatting** - Currency in Vietnamese Dong (₫)

✓ **Full history** - Access data from 2020 onwards

### Example Scenarios

**Scenario 1: Check Monthly Performance**
1. View = Monthly (default)
2. Year = 2025
3. Click Filter
4. See all months in 2025 with revenue totals

**Scenario 2: Analyze Peak Days**
1. View = Daily
2. Year = 2025, Month = December
3. Click Filter
4. Find which days had most bookings/revenue

**Scenario 3: Year-over-Year Comparison**
1. View = Yearly
2. View chart showing 2020-2025 trend
3. Compare growth across years

### Troubleshooting

**No data showing?**
- Check if any bookings have payment_status = 'paid'
- Verify the date range has bookings
- Try different year/month selection

**Chart not displaying?**
- Refresh the page (F5)
- Check browser console for JavaScript errors
- Ensure JavaScript is enabled in browser

**Incorrect revenue?**
- Only PAID bookings are counted
- Unpaid bookings are excluded intentionally
- Check booking payment status in admin dashboard

### Technical Details

- **Data Source:** Bookings table (only paid bookings)
- **Calculation:** SUM of total_amount field
- **Grouping:** By check-in date
- **Formatting:** Vietnamese Dong (₫) with comma separators
- **Performance:** Optimized SQL queries with proper indexing
