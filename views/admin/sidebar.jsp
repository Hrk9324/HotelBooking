<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!-- Top Navbar -->
<nav class="navbar navbar-dark bg-dark fixed-top">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">Hotel Booking Admin</a>
        <div class="d-flex">
            <span class="navbar-text text-white me-3">
                Welcome, ${sessionScope.user.fullName}
            </span>
        </div>
    </div>
</nav>

<!-- Sidebar Navigation -->
<div class="d-flex flex-column flex-shrink-0 p-3 bg-light" style="width: 280px; min-height: 100vh; position: fixed; top: 56px; left: 0; bottom: 0; overflow-y: auto;">
    <ul class="nav nav-pills flex-column mb-auto">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/dashboard" 
               class="nav-link ${pageContext.request.requestURI.contains('dashboard') ? 'active' : ''}">
                <i class="bi bi-speedometer2"></i> Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/hotels" 
               class="nav-link ${(pageContext.request.requestURI.contains('hotel_list') || pageContext.request.requestURI.contains('hotel_form')) ? 'active' : ''}">
                <i class="bi bi-building"></i> Manage Hotels
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/rooms" 
               class="nav-link ${(pageContext.request.requestURI.contains('room_list') || pageContext.request.requestURI.contains('room_form')) ? 'active' : ''}">
                <i class="bi bi-door-closed"></i> Manage Rooms
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/checkin" 
               class="nav-link ${pageContext.request.requestURI.contains('checkin') ? 'active' : ''}">
                <i class="bi bi-person-check"></i> Check-in
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/revenue" 
               class="nav-link ${pageContext.request.requestURI.contains('revenue') ? 'active' : ''}">
                <i class="bi bi-bar-chart"></i> Revenue Statistics
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/customer-stats" 
               class="nav-link ${pageContext.request.requestURI.contains('customer-stats') ? 'active' : ''}">
                <i class="bi bi-people-fill"></i> Customer Usage
            </a>
        </li>
        <li class="nav-item mt-3">
            <a href="${pageContext.request.contextPath}/logout" 
               class="nav-link text-danger">
                <i class="bi bi-box-arrow-right"></i> Logout
            </a>
        </li>
    </ul>
</div>
