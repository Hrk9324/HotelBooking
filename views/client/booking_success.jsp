<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Booking Confirmed</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .success-container {
            min-height: 80vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .success-card {
            max-width: 600px;
            text-align: center;
            padding: 40px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }
        .success-icon {
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 30px;
        }
        .success-icon svg {
            width: 60px;
            height: 60px;
            fill: white;
        }
        .booking-id {
            font-size: 2rem;
            color: #28a745;
            font-weight: bold;
            margin: 20px 0;
        }
    </style>
</head>
<body>
    <jsp:include page="navbar.jsp" />

    <div class="success-container">
        <div class="success-card">
            <!-- Success Icon -->
            <div class="success-icon">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
            </div>

            <!-- Success Message -->
            <h1 class="text-success mb-3">Booking Confirmed!</h1>
            <p class="text-muted mb-4">Your reservation has been successfully processed and payment confirmed.</p>

            <!-- Booking ID -->
            <div class="mb-4">
                <p class="mb-2">Your Booking ID:</p>
                <div class="booking-id">#${param.id}</div>
            </div>

            <!-- Information -->
            <div class="alert alert-info mb-4">
                <p class="mb-0">
                    <strong>Important:</strong> Please save this booking ID for your records. 
                    A confirmation email has been sent to your registered email address with your check-in codes.
                </p>
            </div>

            <!-- Action Buttons -->
            <div class="d-grid gap-2">
                <a href="home" class="btn btn-primary btn-lg">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-house-door me-2" viewBox="0 0 16 16" style="display: inline-block; vertical-align: middle;">
                        <path d="M8.354 1.146a.5.5 0 0 0-.708 0l-6 6A.5.5 0 0 0 1.5 7.5v7a.5.5 0 0 0 .5.5h4.5a.5.5 0 0 0 .5-.5v-4h2v4a.5.5 0 0 0 .5.5H14a.5.5 0 0 0 .5-.5v-7a.5.5 0 0 0-.146-.354L13 5.793V2.5a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1.293L8.354 1.146zM2.5 14V7.707l5.5-5.5 5.5 5.5V14H10v-4a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5v4H2.5z"/>
                    </svg>
                    Back to Home
                </a>
                <a href="search" class="btn btn-outline-secondary btn-lg">
                    Search More Hotels
                </a>
            </div>

            <!-- Additional Info -->
            <div class="mt-4 pt-4 border-top">
                <p class="text-muted small mb-0">
                    Need help? Contact our customer support at <strong>support@hotelbooking.com</strong>
                </p>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
