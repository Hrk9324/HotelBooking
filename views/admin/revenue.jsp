<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Revenue Statistics - Hotel Booking Admin</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Chart.js for charts -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <style>
        body {
            padding-top: 56px;
        }
        .main-content {
            margin-left: 280px;
            padding: 20px 25px;
        }
        .nav-link {
            color: #333;
            padding: 10px 15px;
            margin-bottom: 5px;
            border-radius: 5px;
        }
        .nav-link:hover {
            background-color: #e9ecef;
            color: #000;
        }
        .nav-link.active {
            background-color: #0d6efd;
            color: white;
        }
        .stat-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px 15px;
            border-radius: 10px;
            margin-bottom: 20px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .stat-card h6 {
            font-size: 12px;
            opacity: 0.9;
            margin-bottom: 8px;
        }
        .stat-card .amount {
            font-size: 24px;
            font-weight: bold;
        }
        .stat-card.revenue {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .stat-card.bookings {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        .stat-card.average {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
        .view-buttons {
            margin-bottom: 15px;
        }
        .table-responsive {
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        table thead {
            background-color: #667eea;
            color: white;
        }
        table tbody tr:hover {
            background-color: #f8f9fa;
        }
        table {
            font-size: 14px;
        }
        table th, table td {
            padding: 10px 12px;
        }
        .chart-container {
            position: relative;
            height: 250px;
            margin-bottom: 30px;
        }
        .filter-section {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
        }
        .currency {
            color: #667eea;
            font-weight: bold;
        }
        .card {
            border: none;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        }
        .card-body {
            padding: 20px;
        }
        .card-header {
            background-color: #f8f9fa;
            border-bottom: 1px solid #e9ecef;
            padding: 15px 20px;
        }
        .card-header h5 {
            margin: 0;
            font-size: 16px;
        }
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="sidebar.jsp" />

    <!-- Main Content -->
    <main class="main-content">
        <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
            <h1 class="h2"><i class="bi bi-bar-chart"></i> Revenue Statistics</h1>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- View Selection Buttons -->
        <div class="view-buttons">
            <a href="?view=month" class="btn btn-outline-primary ${view == 'month' ? 'active' : ''}">
                <i class="bi bi-calendar-month"></i> Monthly
            </a>
            <a href="?view=day&year=${selectedYear}&month=${selectedMonth}" class="btn btn-outline-primary ${view == 'day' ? 'active' : ''}">
                <i class="bi bi-calendar-day"></i> Daily
            </a>
            <a href="?view=year" class="btn btn-outline-primary ${view == 'year' ? 'active' : ''}">
                <i class="bi bi-calendar-year"></i> Yearly
            </a>
        </div>

        <!-- Filter Section for Day and Month Views -->
        <c:if test="${view == 'day' || view == 'month'}">
            <div class="filter-section">
                <form method="get" class="row g-3 align-items-end">
                    <input type="hidden" name="view" value="${view}">
                    
                    <div class="col-md-3">
                        <label for="yearSelect" class="form-label">Year:</label>
                        <select id="yearSelect" name="year" class="form-select">
                            <c:forEach var="y" begin="2020" end="${currentYear}">
                                <option value="${y}" <c:if test="${y == selectedYear}">selected</c:if>>${y}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <c:if test="${view == 'day' || view == 'month'}">
                        <div class="col-md-3">
                            <label for="monthSelect" class="form-label">Month:</label>
                            <select id="monthSelect" name="month" class="form-select">
                                <option value="">-- All Months --</option>
                                <option value="1" <c:if test="${selectedMonth == 1}">selected</c:if>>January</option>
                                <option value="2" <c:if test="${selectedMonth == 2}">selected</c:if>>February</option>
                                <option value="3" <c:if test="${selectedMonth == 3}">selected</c:if>>March</option>
                                <option value="4" <c:if test="${selectedMonth == 4}">selected</c:if>>April</option>
                                <option value="5" <c:if test="${selectedMonth == 5}">selected</c:if>>May</option>
                                <option value="6" <c:if test="${selectedMonth == 6}">selected</c:if>>June</option>
                                <option value="7" <c:if test="${selectedMonth == 7}">selected</c:if>>July</option>
                                <option value="8" <c:if test="${selectedMonth == 8}">selected</c:if>>August</option>
                                <option value="9" <c:if test="${selectedMonth == 9}">selected</c:if>>September</option>
                                <option value="10" <c:if test="${selectedMonth == 10}">selected</c:if>>October</option>
                                <option value="11" <c:if test="${selectedMonth == 11}">selected</c:if>>November</option>
                                <option value="12" <c:if test="${selectedMonth == 12}">selected</c:if>>December</option>
                            </select>
                        </div>
                    </c:if>

                    <!-- Date picker for Day view -->
                    <c:if test="${view == 'day'}">
                        <div class="col-md-3">
                            <label for="dateSelect" class="form-label">Select Date:</label>
                            <input type="date" id="dateSelect" name="date" class="form-control" 
                                   value="${selectedDate}" min="${selectedYear}-${selectedMonth < 10 ? '0' : ''}${selectedMonth}-01">
                        </div>
                    </c:if>

                    <div class="col-md-3">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-search"></i> Filter
                        </button>
                    </div>
                </form>
            </div>
        </c:if>

        <!-- Summary Cards -->
        <div class="row mb-3">
            <div class="col-md-4">
                <div class="stat-card revenue">
                    <h6>Total Revenue</h6>
                    <div class="amount">
                        <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫"/>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card bookings">
                    <h6>Total Bookings</h6>
                    <div class="amount">${totalBookings}</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card average">
                    <h6>Average Per Booking</h6>
                    <div class="amount">
                        <c:choose>
                            <c:when test="${totalBookings > 0}">
                                <fmt:formatNumber value="${totalRevenue / totalBookings}" type="currency" currencySymbol="₫"/>
                            </c:when>
                            <c:otherwise>
                                <fmt:formatNumber value="0" type="currency" currencySymbol="₫"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- Chart Section -->
        <div class="card mb-3">
            <div class="card-body">
                <h5 class="card-title mb-3">Revenue Chart</h5>
                <div class="chart-container">
                    <canvas id="revenueChart"></canvas>
                </div>
            </div>
        </div>

        <!-- Statistics Table - Only show when no date is selected -->
        <c:if test="${view != 'day' || empty selectedDate}">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">
                    <c:choose>
                        <c:when test="${view == 'day'}">
                            <c:set var="monthNames" value="January,February,March,April,May,June,July,August,September,October,November,December" />
                            Daily Revenue - <c:out value="${monthNames.split(',')[selectedMonth - 1]}" /> ${selectedYear}
                        </c:when>
                        <c:when test="${view == 'month'}">
                            <c:choose>
                                <c:when test="${selectedMonth > 0}">
                                    <c:set var="monthNames" value="January,February,March,April,May,June,July,August,September,October,November,December" />
                                    Monthly Revenue - <c:out value="${monthNames.split(',')[selectedMonth - 1]}" /> ${selectedYear}
                                </c:when>
                                <c:otherwise>
                                    Monthly Revenue - ${selectedYear}
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:when test="${view == 'year'}">Yearly Revenue Statistics</c:when>
                        <c:otherwise>Monthly Revenue - ${selectedYear}</c:otherwise>
                    </c:choose>
                </h5>
            </div>
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead>
                        <tr>
                            <th>
                                <c:choose>
                                    <c:when test="${view == 'day'}">Date</c:when>
                                    <c:when test="${view == 'year'}">Year</c:when>
                                    <c:otherwise>Month</c:otherwise>
                                </c:choose>
                            </th>
                            <th class="text-end">Revenue</th>
                            <th class="text-end">Bookings</th>
                            <th class="text-end">Avg/Booking</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty stats}">
                                <c:forEach var="stat" items="${stats}">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${view == 'day'}">
                                                    <strong>${stat.period}</strong>
                                                </c:when>
                                                <c:when test="${view == 'year'}">
                                                    <strong>${stat.period}</strong>
                                                </c:when>
                                                <c:otherwise>
                                                    <strong>
                                                        <a href="?view=day&year=${fn:substringBefore(stat.period, '-')
                                                                }&month=${fn:substringAfter(stat.period, '-')}"
                                                           class="text-decoration-none">
                                                            <fmt:parseDate value="${stat.period}-01" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
                                                            <fmt:formatDate value="${parsedDate}" pattern="MMMM yyyy"/>
                                                        </a>
                                                    </strong>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-end">
                                            <span class="currency">
                                                <fmt:formatNumber value="${stat.totalRevenue}" type="currency" currencySymbol="₫"/>
                                            </span>
                                        </td>
                                        <td class="text-end">${stat.bookingCount}</td>
                                        <td class="text-end">
                                            <fmt:formatNumber value="${stat.averagePerBooking}" type="currency" currencySymbol="₫"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="4" class="text-center text-muted py-4">
                                        No data available for selected period
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
        </c:if>

        <!-- Booking Details Section - Only show when date is selected -->
        <c:if test="${view == 'day' && not empty selectedDate}">
            <div class="card mt-4">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="bi bi-card-list"></i> Booking Details - Starting from 
                        <fmt:parseDate value="${selectedDate}" pattern="yyyy-MM-dd" var="parsedSelectedDate" type="date"/>
                        <fmt:formatDate value="${parsedSelectedDate}" pattern="MMMM dd, yyyy"/>
                    </h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead>
                            <tr>
                                <th>Booking ID</th>
                                <th>User ID</th>
                                <th>Check-in Date</th>
                                <th>Check-out Date</th>
                                <th class="text-end">Total Amount</th>
                                <th class="text-center">Payment Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty dateBookings}">
                                    <c:forEach var="booking" items="${dateBookings}">
                                        <tr>
                                            <td>
                                                <strong>#${booking.bookingId}</strong>
                                            </td>
                                            <td>${booking.userId}</td>
                                            <td>
                                                <fmt:formatDate value="${booking.checkinDate}" pattern="yyyy-MM-dd"/>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${booking.checkoutDate}" pattern="yyyy-MM-dd"/>
                                            </td>
                                            <td class="text-end">
                                                <span class="currency">
                                                    <fmt:formatNumber value="${booking.totalAmount}" type="currency" currencySymbol="₫"/>
                                                </span>
                                            </td>
                                            <td class="text-center">
                                                <span class="badge bg-success">${booking.paymentStatus}</span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="6" class="text-center text-muted py-4">
                                            No bookings for this date
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>
    </main>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Chart Initialization -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const stats = ${empty stats ? '[]' : 'new java.util.ArrayList()'};
            const labels = [];
            const revenues = [];
            const bookings = [];

            // Parse data from table rows
            const rows = document.querySelectorAll('table tbody tr');
            rows.forEach(row => {
                const cells = row.querySelectorAll('td');
                if (cells.length >= 3) {
                    labels.push(cells[0].textContent.trim());
                    // Parse revenue (remove currency symbol and formatting)
                    const revenueTxt = cells[1].textContent.replace(/[₫,]/g, '').trim();
                    revenues.push(parseFloat(revenueTxt) || 0);
                    // Parse bookings count
                    bookings.push(parseInt(cells[2].textContent) || 0);
                }
            });

            // Create chart if data exists
            if (labels.length > 0) {
                const ctx = document.getElementById('revenueChart');
                if (ctx) {
                    new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: labels,
                            datasets: [
                                {
                                    label: 'Revenue (₫)',
                                    data: revenues,
                                    backgroundColor: 'rgba(102, 126, 234, 0.6)',
                                    borderColor: 'rgba(102, 126, 234, 1)',
                                    borderWidth: 2,
                                    tension: 0.4,
                                    yAxisID: 'y'
                                },
                                {
                                    label: 'Bookings',
                                    data: bookings,
                                    backgroundColor: 'rgba(245, 87, 108, 0.6)',
                                    borderColor: 'rgba(245, 87, 108, 1)',
                                    borderWidth: 2,
                                    yAxisID: 'y1',
                                    type: 'line'
                                }
                            ]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            interaction: {
                                mode: 'index',
                                intersect: false
                            },
                            scales: {
                                y: {
                                    type: 'linear',
                                    display: true,
                                    position: 'left',
                                    title: {
                                        display: true,
                                        text: 'Revenue (₫)'
                                    }
                                },
                                y1: {
                                    type: 'linear',
                                    display: true,
                                    position: 'right',
                                    title: {
                                        display: true,
                                        text: 'Number of Bookings'
                                    },
                                    grid: {
                                        drawOnChartArea: false
                                    }
                                }
                            },
                            plugins: {
                                legend: {
                                    display: true,
                                    position: 'top'
                                }
                            }
                        }
                    });
                }
            }
        });
    </script>
</body>
</html>
