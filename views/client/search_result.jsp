<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results - Hotel Booking</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .hotel-card {
            transition: transform 0.2s;
        }
        .hotel-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.15);
        }
        .room-tag {
            display: inline-block;
            background-color: #e9ecef;
            padding: 5px 10px;
            border-radius: 5px;
            margin: 3px;
            font-size: 0.9rem;
        }
        .search-summary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px 0;
            margin-bottom: 30px;
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">Hotel Booking</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/login">Login</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/register">Register</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Search Summary -->
    <section class="search-summary">
        <div class="container">
            <h2 class="mb-3">Search Results</h2>
            <p class="lead mb-0">
                <i class="bi bi-people-fill"></i> ${search_people} people • 
                <i class="bi bi-calendar3"></i> ${search_checkin} to ${search_checkout}
            </p>
        </div>
    </section>

    <!-- Results Section -->
    <section class="py-4">
        <div class="container">
            <c:choose>
                <c:when test="${not empty hotels}">
                    <div class="row mb-3">
                        <div class="col-12">
                            <h4>Found ${hotels.size()} hotel(s) matching your criteria</h4>
                        </div>
                    </div>
                    
                    <div class="row g-4">
                        <c:forEach var="hotel" items="${hotels}">
                            <div class="col-md-6 col-lg-4">
                                <div class="card hotel-card h-100">
                                    <div class="card-body">
                                        <h5 class="card-title">${hotel.name}</h5>
                                        <p class="text-muted mb-3">
                                            <i class="bi bi-geo-alt"></i> ${hotel.address}
                                        </p>
                                        <p class="card-text">${hotel.description}</p>
                                        
                                        <div class="mb-3">
                                            <strong>Available Rooms (${hotel.availableRooms.size()}):</strong>
                                            <div class="mt-2">
                                                <c:forEach var="room" items="${hotel.availableRooms}">
                                                    <span class="room-tag">
                                                        ${room.roomName} (${room.capacity} people)
                                                    </span>
                                                </c:forEach>
                                            </div>
                                        </div>
                                        
                                        <div class="d-grid">
                                            <a href="${pageContext.request.contextPath}/hotel-detail?id=${hotel.id}&checkin=${search_checkin}&checkout=${search_checkout}&people=${search_people}" 
                                               class="btn btn-primary">
                                                View Details & Book
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row">
                        <div class="col-12">
                            <div class="alert alert-info text-center py-5" role="alert">
                                <h4 class="alert-heading">No Hotels Found</h4>
                                <p>Sorry, no hotels match your search criteria.</p>
                                <hr>
                                <p class="mb-0">Try adjusting your dates or the number of people.</p>
                                <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">
                                    Back to Search
                                </a>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
