# Fix Monthly View Revenue Statistics Bug

## Problem
Khi chọn tháng 11 để xem doanh thu, hệ thống lại hiển thị dữ liệu của tháng 12.

## Root Cause
- Trước đó, servlet lấy tất cả tháng của năm rồi filter ở client-side với logic removeIf()
- Logic filter có thể bị sai hoặc dữ liệu được sort không đúng

## Solution Implemented

### 1. Created New Query Method in BookingDAO
**File:** `src/model/dao/BookingDAO.java`

Added method `getRevenueByMonthAndYear(int year, int month)`:
- Query trực tiếp từ database lấy doanh thu của một tháng cụ thể
- Dùng `WHERE YEAR(checkin_date) = ? AND MONTH(checkin_date) = ?`
- Trả về 1 RevenueStats object (không phải List)
- Chỉ lấy booking có `payment_status = 'paid'`

### 2. Updated AdminRevenueServlet Logic
**File:** `src/controller/admin/AdminRevenueServlet.java`

Updated month view handling:
```java
if (monthParam != null && !monthParam.isEmpty()) {
    // Specific month selected - get data for that month only
    RevenueStats monthStat = bookingDAO.getRevenueByMonthAndYear(year, month);
    if (monthStat.getPeriod() != null) {
        monthlyStats.add(monthStat);
    }
} else {
    // No month selected - get all months of the year
    monthlyStats = bookingDAO.getRevenueByMonth(year);
}
```

## How It Works Now

### Monthly View:
1. **User chọn tháng 11**: 
   - Servlet gọi `getRevenueByMonthAndYear(2025, 11)`
   - Query lấy đúng dữ liệu của tháng 11/2025
   - Hiển thị chỉ doanh thu tháng 11

2. **User không chọn tháng (All Months)**:
   - Servlet gọi `getRevenueByMonth(2025)`
   - Query lấy tất cả tháng của 2025
   - Hiển thị doanh thu từng tháng

## Testing
1. Restart Tomcat
2. Go to Admin Revenue Statistics
3. Click "Monthly" tab
4. Select Year: 2025, Month: 11
5. Should show ONLY November 2025 data
6. Try other months to verify

## Benefits
✅ Query specific month directly from database (no client-side filtering)
✅ More accurate and reliable
✅ Cleaner code logic
✅ No sorting/filtering issues
