<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hotel Management - Admin</title>
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
                    <h1 class="h2">Hotel Management</h1>
                    <div class="btn-toolbar mb-2 mb-md-0">
                        <a href="?action=new" class="btn btn-primary">Add New Hotel</a>
                    </div>
                </div>

                <!-- Alert Messages -->
                <c:if test="${param.msg != null}">
                    <c:choose>
                        <c:when test="${param.msg == 'saved'}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                Hotel saved successfully!
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:when>
                        <c:when test="${param.msg == 'deleted'}">
                            <div class="alert alert-info alert-dismissible fade show" role="alert">
                                Hotel deleted successfully!
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:when>
                    </c:choose>
                </c:if>

                <!-- Hotels Table -->
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Address</th>
                                <th>Description</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="h" items="${hotels}">
                                <tr>
                                    <td>${h.id}</td>
                                    <td>${h.name}</td>
                                    <td>${h.address}</td>
                                    <td>${h.description}</td>
                                    <td>
                                        <a href="?action=edit&id=${h.id}" class="btn btn-warning btn-sm">Edit</a>
                                        <a href="?action=delete&id=${h.id}" 
                                           onclick="return confirm('Are you sure you want to delete this hotel?');" 
                                           class="btn btn-danger btn-sm">Delete</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
