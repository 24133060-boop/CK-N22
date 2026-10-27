<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!doctype html>
<html lang="vi">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Quản lý chi nhánh · UTEMILKTEA</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
<main class="container py-4">
<div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">
<h2>Quản lý theo chi nhánh</h2>
<a class="btn btn-outline-success" href="${administrator ? '/admin' : '/manager'}">← Tổng quan</a>
</div>
<p class="text-muted">MANAGER chỉ thao tác với chi nhánh được ADMIN phân công. ADMIN có quyền quản lý tất cả.</p>
<form method="get" action="/manager/catalog" class="card p-3 mb-4">
<label class="form-label">Chọn chi nhánh</label>
<div class="d-flex gap-2">
<select name="shopId" class="form-select">
<c:forEach var="s" items="${permittedShops}">
<option value="${s.id}" ${selected.id eq s.id ? 'selected' : ''}>
<c:out value="${s.name}"/>
</option>
</c:forEach>
</select>
<button class="btn btn-success">Mở</button>
</div>
</form>
<c:choose>
<c:when test="${empty selected}">
<div class="alert alert-warning">Chưa có chi nhánh được phân công. Liên hệ ADMIN để cấp quyền.</div>
</c:when>
<c:otherwise>
<c:if test="${administrator}">
<section class="card p-3 mb-4">
<h4>Thiết lập chi nhánh (ADMIN)</h4>
<form method="post" action="/admin/shops/${selected.id}/edit" class="row g-2 mb-3">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<div class="col-md-5">
<input required maxlength="255" name="name" class="form-control" value="<c:out value='${selected.name}'/>" placeholder="Tên chi nhánh">
</div>
<div class="col-md-5">
<input required maxlength="255" name="address" class="form-control" value="<c:out value='${selected.address}'/>" placeholder="Địa chỉ">
</div>
<div class="col-md-2">
<button class="btn btn-primary w-100">Lưu</button>
</div>
</form>
<form method="post" action="/admin/shops/${selected.id}/assign-manager" class="row g-2">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<div class="col-md-8">
<select name="managerEmail" class="form-select">
<option value="">Chưa phân công / gỡ phân công</option>
<c:forEach var="u" items="${allManagers}">
<option value="${u.email}" ${selected.managerEmail eq u.email ? 'selected' : ''}>
<c:out value="${u.displayName}"/> (<c:out value="${u.email}"/>)</option>
</c:forEach>
</select>
</div>
<div class="col-md-4">
<button class="btn btn-outline-primary w-100">Phân công MANAGER</button>
</div>
</form>
</section>
</c:if>
<section class="card p-3 mb-4">
<h4>Chiết khấu và nhà vận chuyển của chi nhánh</h4>
<p class="small text-muted">Manager chỉ được chỉnh chi nhánh được phân công; Admin có thể chỉnh mọi chi nhánh.</p>
<form method="post" action="/manager/shops/${selected.id}/commercial" class="row g-2 mt-3">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<div class="col-md-4">
<label>Chiết khấu nền tảng (%)</label>
<input type="number" required min="0" max="40" step="0.01" name="commissionPercent" class="form-control" value="${empty selected.commissionPercent ? 0 : selected.commissionPercent}">
</div>
<div class="col-md-5">
<label>Nhà vận chuyển</label>
<select name="carrierId" class="form-select">
<option value="">Phí giao hàng mặc định</option>
<c:forEach var="c" items="${carriers}">
<c:if test="${c.active}">
<option value="${c.id}" ${not empty selected.deliveryProvider and selected.deliveryProvider.id eq c.id ? 'selected' : ''}>
<c:out value="${c.name}"/>
</option>
</c:if>
</c:forEach>
</select>
</div>
<div class="col-md-3 d-flex align-items-end">
<button class="btn btn-outline-success w-100">Lưu cấu hình</button>
</div>
</form>
</section>
<section class="card p-3 mb-4">
<h4>Sản phẩm của chi nhánh</h4>
<div class="table-responsive">
<table class="table table-striped align-middle">
<thead>
<tr>
<th>ID</th>
<th>Tên / Danh mục</th>
<th>Giá</th>
<th>Đang bán</th>
<th>
</th>
</tr>
</thead>
<tbody>
<c:forEach var="p" items="${shopProducts}">
<tr>
<td colspan="5">
<form method="post" action="/manager/shops/${selected.id}/products/${p.id}/edit" class="row g-2 align-items-center">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<div class="col-md-1">${p.id}</div>
<div class="col-md-5">
<input name="name" required maxlength="255" class="form-control mb-1" value="<c:out value='${p.name}'/>">
<input name="category" required maxlength="255" class="form-control form-control-sm" value="<c:out value='${p.category}'/>">
</div>
<div class="col-md-2">
<input name="price" type="number" min="1" max="10000000" step="1" required class="form-control" value="<fmt:formatNumber value='${p.price}' pattern='0' groupingUsed='false'/>">
</div>
<div class="col-md-2">
<label class="small">Đang bán </label> <input class="form-check-input" type="checkbox" name="available" value="true" ${p.available ? 'checked' : ''}>
</div>
<div class="col-md-2">
<button class="btn btn-sm btn-outline-success">Lưu</button>
</div>
</form>
</td>
</tr>
</c:forEach>
</tbody>
</table>
</div>
<h5>Thêm sản phẩm</h5>
<form method="post" action="/manager/shops/${selected.id}/products/new" class="row g-2">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<div class="col-md-4">
<input class="form-control" name="name" required maxlength="255" placeholder="Tên sản phẩm">
</div>
<div class="col-md-3">
<input class="form-control" name="category" required maxlength="255" placeholder="Danh mục">
</div>
<div class="col-md-3">
<input class="form-control" name="price" required type="number" min="1" max="10000000" placeholder="Giá">
</div>
<div class="col-md-2">
<button class="btn btn-success w-100">Thêm</button>
</div>
</form>
</section>
<section class="card p-3 mb-4">
<h4>Quản lý danh mục</h4>
<p class="text-muted small">Đổi tên danh mục cho tất cả sản phẩm thuộc chi nhánh đang chọn; không ảnh hưởng chi nhánh khác.</p>
<form method="post" action="/manager/shops/${selected.id}/categories/rename" class="row g-2">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<div class="col-md-5">
<input name="previous" required maxlength="255" class="form-control" placeholder="Tên danh mục hiện tại">
</div>
<div class="col-md-5">
<input name="next" required maxlength="255" class="form-control" placeholder="Tên danh mục mới">
</div>
<div class="col-md-2">
<button class="btn btn-outline-primary w-100">Đổi tên</button>
</div>
</form>
</section>
<section class="card p-3 mb-4">
<h4>Khuyến mãi chi nhánh</h4>
<p class="small text-muted">Khuyến mãi hoạt động được áp dụng tự động khi đặt hàng; chọn mức cao nhất trong từng loại.</p>
<c:forEach var="pr" items="${promotions}">
<div class="d-flex justify-content-between align-items-center border-bottom py-2">
<span>
<c:out value="${pr.title}"/> · ${pr.percent}% · ${pr.discountType eq 'SHIPPING_PERCENT' ? 'phí vận chuyển' : 'sản phẩm'} · đến ${pr.endDate} · ${pr.active ? 'Bật' : 'Tắt'}</span>
<form method="post" action="/manager/shops/${selected.id}/promotions/${pr.id}/toggle">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<button class="btn btn-sm btn-outline-warning">Bật/tắt</button>
</form>
</div>
</c:forEach>
<form method="post" action="/manager/shops/${selected.id}/promotions/new" class="row g-2 mt-2">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<div class="col-md-5">
<input class="form-control" name="title" required maxlength="100" placeholder="Tên chương trình">
</div>
<div class="col-md-3">
<select class="form-select mb-1" name="discountType">
<option value="PRODUCT_PERCENT">Giá sản phẩm</option>
<option value="SHIPPING_PERCENT">Phí vận chuyển</option>
</select>
<input class="form-control" name="percent" required type="number" min="0.01" max="70" step="0.01" placeholder="% giảm">
</div>
<div class="col-md-3">
<input class="form-control" name="endDate" required type="date">
</div>
<div class="col-md-1">
<button class="btn btn-success w-100">+</button>
</div>
</form>
</section>
<section class="card p-3 mb-4">
<h4>Khách hàng đã đặt ở chi nhánh</h4>
<div class="table-responsive">
<table class="table">
<thead>
<tr>
<th>Email</th>
<th>Họ tên</th>
<th>Đặt hàng tại chi nhánh</th>
</tr>
</thead>
<tbody>
<c:forEach var="u" items="${shopUsers}">
<tr>
<td>
<c:out value="${u.email}"/>
</td>
<td>
<c:out value="${u.displayName}"/>
</td>
<td>
<form method="post" action="/manager/shops/${selected.id}/customers/${u.id}/block">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<button class="btn btn-sm ${blockedCustomers.contains(u.id) ? 'btn-success' : 'btn-outline-danger'}">${blockedCustomers.contains(u.id) ? 'Bỏ chặn' : 'Chặn đặt hàng'}</button>
</form>
</td>
</tr>
</c:forEach>
</tbody>
</table>
</div>
</section>
</c:otherwise>
</c:choose>
</main>
</body>
</html>
