<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Client Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container">
    <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/"
      >Hotel Booking</a
    >
    <button
      class="navbar-toggler"
      type="button"
      data-bs-toggle="collapse"
      data-bs-target="#navbarNav"
    >
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <% model.bean.User loggedUser = (model.bean.User)
        session.getAttribute("user"); if (loggedUser != null) { %>
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/profile"
            >View Profile</a
          >
        </li>
        <li class="nav-item">
          <span class="nav-link">Welcome, <%= loggedUser.getFullName() %></span>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/logout"
            >Logout</a
          >
        </li>
        <% } else { %>
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/login"
            >Login</a
          >
        </li>
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/register"
            >Register</a
          >
        </li>
        <% } %>
      </ul>
    </div>
  </div>
</nav>
