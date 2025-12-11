<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Hotel Booking</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body {
            padding-top: 56px;
        }
        .main-content {
            margin-left: 280px;
            padding: 30px;
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
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="sidebar.jsp" />

    <!-- Main Content -->
    <main class="main-content">
        <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
            <h1 class="h2">Dashboard</h1>
        </div>

        <div class="row">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-body">
                        <h3>Welcome, ${sessionScope.user.fullName}!</h3>
                        <p class="lead">You are logged in as an administrator.</p>
                        <hr>
                        <p>Use the navigation menu to manage hotels, rooms, and other resources.</p>
                        
                        <div class="row mt-4">
                            <div class="col-md-4">
                                <div class="card text-white bg-primary mb-3">
                                    <div class="card-body">
                                        <h5 class="card-title">Hotels</h5>
                                        <p class="card-text">Manage hotel listings</p>
                                        <a href="${pageContext.request.contextPath}/admin/hotels" class="btn btn-light btn-sm">View Hotels</a>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-white bg-success mb-3">
                                    <div class="card-body">
                                        <h5 class="card-title">Rooms</h5>
                                        <p class="card-text">Manage room inventory</p>
                                        <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-light btn-sm">View Rooms</a>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-white bg-info mb-3">
                                    <div class="card-body">
                                        <h5 class="card-title">Check-in</h5>
                                        <p class="card-text">Manage guest check-ins</p>
                                        <a href="${pageContext.request.contextPath}/admin/checkin" class="btn btn-light btn-sm">Go to Check-in</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
