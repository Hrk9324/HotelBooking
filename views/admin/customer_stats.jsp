<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Usage Statistics</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { padding-top: 56px; }
        .main-content { margin-left: 280px; padding: 30px; }
        .nav-link { color: #333; padding: 10px 15px; margin-bottom: 5px; border-radius: 5px; }
        .nav-link:hover { background-color: #e9ecef; color: #000; }
        .nav-link.active { background-color: #0d6efd; color: white; }
        .stat-card { border-radius: 12px; color: white; padding: 18px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        .stat-card.bookings { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .stat-card.rooms { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
        .stat-card.customers { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
        .table thead { background-color: #667eea; color: white; }
        .table tbody tr:hover { background-color: #f8f9fa; }
    </style>
</head>
<body>
    <jsp:include page="sidebar.jsp" />

    <main class="main-content">
        <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
            <h1 class="h2"><i class="bi bi-people-fill me-2"></i>Customer Usage Statistics</h1>
        </div>

        <c:set var="totalBookings" value="0" />
        <c:set var="totalRooms" value="0" />
        <c:set var="totalCustomers" value="${stats != null ? stats.size() : 0}" />

        <c:if test="${not empty stats}">
            <c:forEach var="s" items="${stats}">
                <c:set var="totalBookings" value="${totalBookings + s.bookingCount}" />
                <c:set var="totalRooms" value="${totalRooms + s.roomCount}" />
            </c:forEach>
        </c:if>

        <!-- Summary cards -->
        <div class="row g-3 mb-3">
            <div class="col-md-4">
                <div class="stat-card customers">
                    <small class="text-uppercase opacity-75">Total Customers</small>
                    <div class="fs-2 fw-bold">${totalCustomers}</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card bookings">
                    <small class="text-uppercase opacity-75">Total Bookings</small>
                    <div class="fs-2 fw-bold">${totalBookings}</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card rooms">
                    <small class="text-uppercase opacity-75">Total Rooms Used</small>
                    <div class="fs-2 fw-bold">${totalRooms}</div>
                </div>
            </div>
        </div>

        <!-- Data table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="bi bi-table me-2"></i>Customer Room Usage</h5>
            </div>
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Full Name</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th class="text-end">Bookings</th>
                            <th class="text-end">Rooms Used</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty stats}">
                                <c:forEach var="s" items="${stats}" varStatus="loop">
                                    <tr>
                                        <td>${loop.index + 1}</td>
                                        <td>${s.fullName}</td>
                                        <td>${s.username}</td>
                                        <td>${s.email}</td>
                                        <td class="text-end">${s.bookingCount}</td>
                                        <td class="text-end">${s.roomCount}</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="text-center text-muted py-4">No customer data available.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

