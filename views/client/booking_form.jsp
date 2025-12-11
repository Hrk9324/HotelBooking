<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="model.bean.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Booking Confirmation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .summary-card {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .total-price {
            font-size: 2rem;
            color: #28a745;
            font-weight: bold;
        }
        .guest-input {
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
    <jsp:include page="navbar.jsp" />

    <div class="container mt-4 mb-5">
        <h2 class="mb-4">Complete Your Booking</h2>

        <!-- Booking Summary -->
        <div class="summary-card">
            <h4 class="mb-3">Booking Summary</h4>
            
            <div class="row mb-3">
                <div class="col-md-4">
                    <p><strong>Check-in:</strong> ${checkin}</p>
                </div>
                <div class="col-md-4">
                    <p><strong>Check-out:</strong> ${checkout}</p>
                </div>
                <div class="col-md-4">
                    <p><strong>Duration:</strong> <span class="badge bg-info text-dark">${nights} Night(s)</span></p>
                </div>
            </div>

            <hr>

            <!-- Selected Rooms -->
            <h5 class="mt-3">Selected Rooms:</h5>
            <ul class="list-group mb-3">
                <c:forEach var="room" items="${selectedRooms}">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        ${room.roomName} (Capacity: ${room.capacity})
                        <span class="badge bg-primary rounded-pill">$${room.price}/night</span>
                    </li>
                </c:forEach>
            </ul>

            <!-- Selected Services -->
            <c:if test="${not empty selectedServices}">
                <h5 class="mt-3">Selected Services:</h5>
                <ul class="list-group mb-3">
                    <c:forEach var="service" items="${selectedServices}">
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            ${service.name}
                            <span class="badge bg-success rounded-pill">$${service.price}</span>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>

            <hr>

            <!-- Price Breakdown -->
            <div class="mt-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span>Room Cost:</span>
                    <span>$${roomTotalPerNight} × ${nights} night(s) = <strong>$${roomTotalPerNight * nights}</strong></span>
                </div>
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span>Service Cost:</span>
                    <span><strong>$${serviceTotal}</strong></span>
                </div>
                <hr>
                <div class="d-flex justify-content-between align-items-center mt-3">
                    <h4 class="mb-0">Grand Total:</h4>
                    <h4 class="mb-0"><span class="total-price">$${totalPrice}</span></h4>
                </div>
            </div>
        </div>

        <!-- Guest Information Form -->
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h4 class="mb-0">Guest Information</h4>
            </div>
            <div class="card-body">
                <p class="mb-4">Please enter the name of each guest (Total Capacity: ${totalCapacity})</p>

                <form method="POST" action="confirm-booking" id="guestForm">
                    <!-- Hidden fields to pass booking data -->
                    <input type="hidden" name="hotel_id" value="${hotel_id}">
                    <input type="hidden" name="checkin" value="${checkin}">
                    <input type="hidden" name="checkout" value="${checkout}">
                    
                    <!-- Pass selected room IDs -->
                    <c:forEach var="room" items="${selectedRooms}">
                        <input type="hidden" name="room_ids" value="${room.id}">
                    </c:forEach>

                    <!-- Pass selected service IDs -->
                    <c:forEach var="service" items="${selectedServices}">
                        <input type="hidden" name="service_ids" value="${service.id}">
                    </c:forEach>

                    <!-- Guest name inputs -->
                    <div class="row">
                        <c:forEach var="i" begin="1" end="${totalCapacity}">
                            <div class="col-md-6">
                                <div class="guest-input">
                                    <label for="guest${i}" class="form-label">Guest ${i} Name <span class="text-danger">*</span></label>
                                    <input type="text" 
                                           class="form-control" 
                                           id="guest${i}" 
                                           name="guest_names" 
                                           placeholder="Enter full name" 
                                           required>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Submit Button -->
                    <div class="text-center mt-4">
                        <button type="button" class="btn btn-secondary btn-lg me-3" onclick="history.back()">
                            Back to Hotel Details
                        </button>
                        <button type="submit" class="btn btn-success btn-lg px-5">
                            Confirm Payment
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Form validation
        document.getElementById('guestForm').addEventListener('submit', function(e) {
            const inputs = document.querySelectorAll('input[name="guest_names"]');
            let allFilled = true;

            inputs.forEach(input => {
                if (!input.value.trim()) {
                    allFilled = false;
                    input.classList.add('is-invalid');
                } else {
                    input.classList.remove('is-invalid');
                }
            });

            if (!allFilled) {
                e.preventDefault();
                alert('Please fill in all guest names');
            }
        });

        // Remove invalid class on input
        document.querySelectorAll('input[name="guest_names"]').forEach(input => {
            input.addEventListener('input', function() {
                if (this.value.trim()) {
                    this.classList.remove('is-invalid');
                }
            });
        });
    </script>
</body>
</html>
