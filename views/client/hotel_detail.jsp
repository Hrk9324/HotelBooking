<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Details - ${hotel.name}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .room-card, .service-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 15px;
            transition: all 0.3s;
        }
        .room-card:hover, .service-card:hover {
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .room-card input[type="checkbox"], .service-card input[type="checkbox"] {
            width: 20px;
            height: 20px;
            margin-right: 10px;
        }
        .price-tag {
            color: #28a745;
            font-weight: bold;
            font-size: 1.2rem;
        }
        .capacity-tag {
            background-color: #007bff;
            color: white;
            padding: 3px 10px;
            border-radius: 15px;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
    <jsp:include page="navbar.jsp" />

    <div class="container mt-4 mb-5">
        <!-- Hotel Information -->
        <div class="card mb-4">
            <div class="card-header bg-primary text-white">
                <h3>${hotel.name}</h3>
            </div>
            <div class="card-body">
                <p><strong>Address:</strong> ${hotel.address}</p>
                <p><strong>Description:</strong> ${hotel.description}</p>
                <p><strong>Check-in:</strong> ${checkin} | <strong>Check-out:</strong> ${checkout}</p>
                <p><strong>Guests:</strong> ${people} ${people > 1 ? 'people' : 'person'}</p>
            </div>
        </div>

        <!-- Booking Form -->
        <form method="POST" action="booking-form" id="bookingForm">
            <input type="hidden" name="hotel_id" value="${hotel.id}">
            <input type="hidden" name="checkin" value="${checkin}">
            <input type="hidden" name="checkout" value="${checkout}">

            <!-- Available Rooms -->
            <h4 class="mb-3">Available Rooms</h4>
            <c:if test="${empty rooms}">
                <div class="alert alert-warning">
                    No rooms available for the selected dates.
                </div>
            </c:if>

            <div class="row mb-4">
                <c:forEach var="room" items="${rooms}">
                    <div class="col-md-6">
                        <div class="room-card">
                            <div class="d-flex align-items-start">
                                <input type="checkbox" name="room_ids" value="${room.id}" class="room-checkbox">
                                <div class="flex-grow-1">
                                    <h5>${room.roomName}</h5>
                                    <p class="text-muted mb-2">${room.description}</p>
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="price-tag">$${room.price}</span>
                                        <span class="capacity-tag">Capacity: ${room.capacity}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Available Services -->
            <h4 class="mb-3">Optional Services</h4>
            <c:if test="${empty services}">
                <p class="text-muted">No additional services available.</p>
            </c:if>

            <div class="row mb-4">
                <c:forEach var="service" items="${services}">
                    <div class="col-md-6">
                        <div class="service-card">
                            <div class="d-flex align-items-start">
                                <input type="checkbox" name="service_ids" value="${service.id}">
                                <div class="flex-grow-1">
                                    <h6>${service.name}</h6>
                                    <p class="text-muted mb-2 small">${service.description}</p>
                                    <span class="text-success fw-bold">+$${service.price}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Submit Button -->
            <div class="text-center">
                <button type="submit" class="btn btn-primary btn-lg px-5" id="submitBtn" disabled>
                    Proceed to Booking
                </button>
                <p class="text-danger mt-2 small" id="errorMsg" style="display: none;">Please select at least one room</p>
            </div>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Enable submit button when at least one room is selected
        const roomCheckboxes = document.querySelectorAll('.room-checkbox');
        const submitBtn = document.getElementById('submitBtn');
        const errorMsg = document.getElementById('errorMsg');

        function updateSubmitButton() {
            const anyChecked = Array.from(roomCheckboxes).some(cb => cb.checked);
            submitBtn.disabled = !anyChecked;
            errorMsg.style.display = anyChecked ? 'none' : 'block';
        }

        roomCheckboxes.forEach(cb => {
            cb.addEventListener('change', updateSubmitButton);
        });

        // Form validation
        document.getElementById('bookingForm').addEventListener('submit', function(e) {
            const anyChecked = Array.from(roomCheckboxes).some(cb => cb.checked);
            if (!anyChecked) {
                e.preventDefault();
                errorMsg.style.display = 'block';
            }
        });
    </script>
</body>
</html>
