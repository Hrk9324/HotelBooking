<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${room != null ? 'Edit' : 'Add'} Room - Admin</title>
    <!-- Bootstrap 5 CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid mt-4">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3">
                <jsp:include page="sidebar.jsp" />
            </div>

            <!-- Main Content -->
            <div class="col-md-9">
                <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom">
                    <h1 class="h2">${room != null ? 'Edit' : 'Add New'} Room</h1>
                </div>

                <!-- Error Alert -->
                <c:if test="${error != null}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <div class="row">
                    <div class="col-md-10">
                        <div class="card">
                            <div class="card-body">
                                <form action="${pageContext.request.contextPath}/admin/rooms" method="POST">
                                    <!-- Hidden ID field for edit -->
                                    <input type="hidden" name="id" value="${room.id}">
                                    
                                    <div class="mb-3">
                                        <label for="hotel_id" class="form-label">Hotel</label>
                                        <select class="form-select" id="hotel_id" name="hotel_id" required>
                                            <option value="">Select a hotel...</option>
                                            <c:forEach var="h" items="${hotels}">
                                                <option value="${h.id}" 
                                                    ${room != null && room.hotelId == h.id ? 'selected' : ''}
                                                    ${selectedHotelId == h.id ? 'selected' : ''}>
                                                    ${h.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="name" class="form-label">Room Name</label>
                                        <input type="text" class="form-control" id="name" name="name" 
                                               value="${room != null ? room.roomName : param.name}" required>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="price" class="form-label">Price</label>
                                        <input type="number" class="form-control" id="price" name="price" 
                                               value="${room != null ? room.price : param.price}" 
                                               min="0" step="1000" required>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="capacity" class="form-label">Capacity</label>
                                        <input type="number" class="form-control" id="capacity" name="capacity" 
                                               value="${room != null ? room.capacity : param.capacity}" 
                                               min="1" required>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="status" class="form-label">Status</label>
                                        <select class="form-select" id="status" name="status" required>
                                            <option value="available" ${room.status == 'available' ? 'selected' : ''} ${param.status == 'available' ? 'selected' : ''}>Available</option>
                                            <option value="maintenance" ${room.status == 'maintenance' ? 'selected' : ''} ${param.status == 'maintenance' ? 'selected' : ''}>Maintenance</option>
                                            <option value="occupied" ${room.status == 'occupied' ? 'selected' : ''} ${param.status == 'occupied' ? 'selected' : ''}>Occupied</option>
                                        </select>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="description" class="form-label">Description</label>
                                        <textarea class="form-control" id="description" name="description" 
                                                  rows="4">${room != null ? room.description : param.description}</textarea>
                                    </div>
                                    
                                    <div class="d-flex gap-2">
                                        <button type="submit" class="btn btn-primary">Save Room</button>
                                        <a href="${pageContext.request.contextPath}/admin/rooms" class="btn btn-secondary">Cancel</a>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
