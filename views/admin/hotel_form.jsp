<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${hotel != null ? 'Edit' : 'Add'} Hotel - Admin</title>
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
    <jsp:include page="sidebar.jsp" />

    <main class="main-content">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
                    <h1 class="h2">${hotel != null ? 'Edit' : 'Add New'} Hotel</h1>
                </div>

                <div class="row">
                    <div class="col-md-10">
                        <div class="card">
                            <div class="card-body">
                                <form action="${pageContext.request.contextPath}/admin/hotels" method="POST">
                                    <!-- Hidden ID field for edit -->
                                    <input type="hidden" name="id" value="${hotel.id}">
                                    
                                    <div class="mb-3">
                                        <label for="name" class="form-label">Hotel Name</label>
                                        <input type="text" class="form-control" id="name" name="name" 
                                               value="${hotel.name}" required>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="address" class="form-label">Address</label>
                                        <input type="text" class="form-control" id="address" name="address" 
                                               value="${hotel.address}" required>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="description" class="form-label">Description</label>
                                        <textarea class="form-control" id="description" name="description" 
                                                  rows="4" required>${hotel.description}</textarea>
                                    </div>
                                    
                                    <div class="d-flex gap-2">
                                        <button type="submit" class="btn btn-primary">Save Hotel</button>
                                        <a href="${pageContext.request.contextPath}/admin/hotels" class="btn btn-secondary">Cancel</a>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
