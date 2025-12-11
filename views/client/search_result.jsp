<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results - Hotel Booking</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    
    <style>
        .hotel-card {
            transition: transform 0.2s;
            border: none;
            box-shadow: 0 4px 6px rgba(0,0,0,0.05);
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
            font-size: 0.85rem;
            color: #495057;
        }
        .search-summary {
            /* Gradient Background from Original Design */
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px 0;
            margin-bottom: 30px;
        }
        .btn-primary {
            background-color: #667eea;
            border-color: #667eea;
        }
        .btn-primary:hover {
            background-color: #5a6fd6;
            border-color: #5a6fd6;
        }
    </style>
</head>
<body class="bg-light">

    <jsp:include page="navbar.jsp" />

    <section class="search-summary">
        <div class="container">
            <h2 class="fw-bold mb-3">Search Results</h2>
            <p class="lead mb-0 opacity-75">
                <i class="bi bi-people-fill"></i> ${search_people} people 
                <span class="mx-2">•</span>
                <i class="bi bi-calendar3"></i> ${search_checkin} to ${search_checkout}
            </p>
            <div class="mt-3">
                 <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-search"></i> Change Search
                </a>
            </div>
        </div>
    </section>

    <section class="py-4">
        <div class="container">
            <c:choose>
                <c:when test="${not empty hotels}">
                    <div class="row mb-4">
                        <div class="col-12">
                            <h4 class="text-secondary">Found <span class="text-dark fw-bold">${hotels.size()}</span> hotel(s) matching your criteria</h4>
                        </div>
                    </div>
                    
                    <div class="row g-4">
                        <c:forEach var="hotel" items="${hotels}">
                            <div class="col-md-6 col-lg-4">
                                <div class="card hotel-card h-100">
                                    <div class="card-body d-flex flex-column">
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <h5 class="card-title fw-bold text-primary mb-0">${hotel.name}</h5>
                                            <span class="badge bg-success bg-opacity-75">Available</span>
                                        </div>
                                        
                                        <p class="text-muted mb-3 small">
                                            <i class="bi bi-geo-alt-fill text-danger"></i> ${hotel.address}
                                        </p>
                                        
                                        <p class="card-text text-secondary flex-grow-1">
                                            ${hotel.description}
                                        </p>
                                        
                                        <div class="mt-3 pt-3 border-top">
                                            <small class="fw-bold text-dark d-block mb-2">Available Rooms:</small>
                                            <div class="mb-3">
                                                <c:forEach var="room" items="${hotel.availableRooms}">
                                                    <span class="room-tag">
                                                        <i class="bi bi-door-open"></i> ${room.roomName} (${room.capacity} <i class="bi bi-person"></i>)
                                                    </span>
                                                </c:forEach>
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
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                
                <c:otherwise>
                    <div class="row justify-content-center">
                        <div class="col-md-8">
                            <div class="card border-0 shadow-sm text-center py-5">
                                <div class="card-body">
                                    <div class="display-1 text-muted mb-3"><i class="bi bi-search"></i></div>
                                    <h3 class="text-secondary">No Hotels Found</h3>
                                    <p class="text-muted">We couldn't find any hotels with enough capacity for your group on these dates.</p>
                                    <a href="${pageContext.request.contextPath}/home" class="btn btn-warning mt-3 px-4">
                                        Try Different Dates
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>