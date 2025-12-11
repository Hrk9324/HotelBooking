<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Booking System - Home</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .hero-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 80px 0;
            min-height: 500px;
            display: flex;
            align-items: center;
        }
        .search-card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }
        .search-card h2 {
            color: #333;
            margin-bottom: 25px;
        }
        .featured-section {
            padding: 60px 0;
        }
        .feature-card {
            transition: transform 0.3s;
        }
        .feature-card:hover {
            transform: translateY(-10px);
        }
        footer {
            background-color: #343a40;
            color: white;
            padding: 30px 0;
            margin-top: 60px;
        }
    </style>
</head>
<body>
    <!-- Include Navbar -->
    <jsp:include page="client/navbar.jsp" />

    <!-- Hero Section with Search Form -->
    <section class="hero-section">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <div class="text-center mb-4">
                        <h1 class="display-4 fw-bold">Find Your Perfect Stay</h1>
                        <p class="lead">Search and book hotels for your next adventure</p>
                    </div>
                    
                    <!-- Search Form -->
                    <div class="search-card">
                        <h2 class="text-center">Search Hotels</h2>
                        
                        <% if (request.getParameter("error") != null) { %>
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <% 
                                String error = request.getParameter("error");
                                if ("invalid_dates".equals(error)) {
                                    out.print("Check-out date must be after check-in date.");
                                } else if ("invalid_format".equals(error)) {
                                    out.print("Invalid date or number format.");
                                } else {
                                    out.print("Please fill in all required fields.");
                                }
                                %>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        <% } %>
                        
                        <form action="search" method="GET">
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <label for="checkIn" class="form-label">Check-in Date</label>
                                    <input type="date" class="form-control" id="checkIn" name="checkIn" required>
                                </div>
                                <div class="col-md-4">
                                    <label for="checkOut" class="form-label">Check-out Date</label>
                                    <input type="date" class="form-control" id="checkOut" name="checkOut" required>
                                </div>
                                <div class="col-md-4">
                                    <label for="people" class="form-label">Number of People</label>
                                    <input type="number" class="form-control" id="people" name="people" 
                                           min="1" value="1" required>
                                </div>
                            </div>
                            <div class="d-grid mt-4">
                                <button type="submit" class="btn btn-primary btn-lg">Search Now</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Featured Section -->
    <section class="featured-section bg-light">
        <div class="container">
            <div class="row">
                <div class="col-12 text-center mb-5">
                    <h2 class="display-5">Why Choose Us?</h2>
                    <p class="lead text-muted">Book your perfect accommodation with ease</p>
                </div>
            </div>
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card feature-card h-100 border-0 shadow-sm">
                        <div class="card-body text-center p-4">
                            <div class="mb-3">
                                <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="#667eea" viewBox="0 0 16 16">
                                    <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
                                </svg>
                            </div>
                            <h4>Easy Search</h4>
                            <p class="text-muted">Find available hotels based on your dates and group size</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card feature-card h-100 border-0 shadow-sm">
                        <div class="card-body text-center p-4">
                            <div class="mb-3">
                                <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="#667eea" viewBox="0 0 16 16">
                                    <path d="M4 .5a.5.5 0 0 0-1 0V1H2a2 2 0 0 0-2 2v1h16V3a2 2 0 0 0-2-2h-1V.5a.5.5 0 0 0-1 0V1H4V.5zM16 14V5H0v9a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2zm-5.146-5.146-3 3a.5.5 0 0 1-.708 0l-1.5-1.5a.5.5 0 0 1 .708-.708L7.5 10.793l2.646-2.647a.5.5 0 0 1 .708.708z"/>
                                </svg>
                            </div>
                            <h4>Quick Booking</h4>
                            <p class="text-muted">Book your rooms in just a few clicks</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card feature-card h-100 border-0 shadow-sm">
                        <div class="card-body text-center p-4">
                            <div class="mb-3">
                                <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="#667eea" viewBox="0 0 16 16">
                                    <path d="M5.338 1.59a61.44 61.44 0 0 0-2.837.856.481.481 0 0 0-.328.39c-.554 4.157.726 7.19 2.253 9.188a10.725 10.725 0 0 0 2.287 2.233c.346.244.652.42.893.533.12.057.218.095.293.118a.55.55 0 0 0 .101.025.615.615 0 0 0 .1-.025c.076-.023.174-.061.294-.118.24-.113.547-.29.893-.533a10.726 10.726 0 0 0 2.287-2.233c1.527-1.997 2.807-5.031 2.253-9.188a.48.48 0 0 0-.328-.39c-.651-.213-1.75-.56-2.837-.855C9.552 1.29 8.531 1.067 8 1.067c-.53 0-1.552.223-2.662.524zM5.072.56C6.157.265 7.31 0 8 0s1.843.265 2.928.56c1.11.3 2.229.655 2.887.87a1.54 1.54 0 0 1 1.044 1.262c.596 4.477-.787 7.795-2.465 9.99a11.775 11.775 0 0 1-2.517 2.453 7.159 7.159 0 0 1-1.048.625c-.28.132-.581.24-.829.24s-.548-.108-.829-.24a7.158 7.158 0 0 1-1.048-.625 11.777 11.777 0 0 1-2.517-2.453C1.928 10.487.545 7.169 1.141 2.692A1.54 1.54 0 0 1 2.185 1.43 62.456 62.456 0 0 1 5.072.56z"/>
                                    <path d="M10.854 5.146a.5.5 0 0 1 0 .708l-3 3a.5.5 0 0 1-.708 0l-1.5-1.5a.5.5 0 1 1 .708-.708L7.5 7.793l2.646-2.647a.5.5 0 0 1 .708 0z"/>
                                </svg>
                            </div>
                            <h4>Secure & Reliable</h4>
                            <p class="text-muted">Your bookings are safe and secure with us</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer>
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h5>Hotel Booking System</h5>
                    <p class="text-white-50">Your trusted partner for finding the perfect accommodation.</p>
                </div>
                <div class="col-md-3">
                    <h6>Quick Links</h6>
                    <ul class="list-unstyled">
                        <li><a href="${pageContext.request.contextPath}/" class="text-white-50 text-decoration-none">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/login" class="text-white-50 text-decoration-none">Login</a></li>
                        <li><a href="${pageContext.request.contextPath}/register" class="text-white-50 text-decoration-none">Register</a></li>
                    </ul>
                </div>
                <div class="col-md-3">
                    <h6>Contact</h6>
                    <p class="text-white-50 mb-0">Email: info@hotelbooking.com</p>
                    <p class="text-white-50">Phone: +1 234 567 8900</p>
                </div>
            </div>
            <hr class="bg-light">
            <div class="row">
                <div class="col-12 text-center">
                    <p class="text-white-50 mb-0">&copy; 2025 Hotel Booking System. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Set minimum date to today
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('checkIn').setAttribute('min', today);
        document.getElementById('checkOut').setAttribute('min', today);
        
        // Update checkout min date when checkin changes
        document.getElementById('checkIn').addEventListener('change', function() {
            const checkInDate = this.value;
            document.getElementById('checkOut').setAttribute('min', checkInDate);
        });
    </script>
</body>
</html>
