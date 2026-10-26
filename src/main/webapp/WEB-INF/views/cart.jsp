<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %><%@ taglib prefix="fn" uri="jakarta.tags.functions" %><%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %><!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>UTEMILKTEA</title><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"><link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet"><link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"><style>#deliveryMap{height:360px;border-radius:12px} .map-hint{font-size:.875rem}</style></head><body><nav class="navbar navbar-expand-lg"><div class="container"><a class="navbar-brand" href="/">🍵 UTEMILKTEA</a><button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#nav"><span class="navbar-toggler-icon"></span></button><div class="collapse navbar-collapse" id="nav"><div class="navbar-nav ms-auto align-items-lg-center"><a class="nav-link" href="/">Trang chủ</a><a class="nav-link" href="/#stores">Chi nhánh</a><a class="nav-link" href="/#menu">Menu</a><sec:authorize access="isAuthenticated()"><sec:authorize access="hasRole('USER')"><a class="nav-link" href="/cart">Giỏ hàng</a><a class="nav-link" href="/my-orders">Đơn hàng</a><a class="nav-link" href="/favorites">Yêu thích</a></sec:authorize><sec:authorize access="hasRole('VENDOR')"><a class="nav-link" href="/vendor">Cửa hàng của tôi</a></sec:authorize><sec:authorize access="hasRole('MANAGER')"><a class="nav-link" href="/manager">Manager</a></sec:authorize><sec:authorize access="hasRole('ADMIN')"><a class="nav-link" href="/admin">Quản trị</a></sec:authorize><sec:authorize access="hasRole('SHIPPER')"><a class="nav-link" href="/shipper">Giao hàng</a></sec:authorize><div class="dropdown ms-lg-2"><button class="btn btn-outline-success dropdown-toggle rounded-pill" data-bs-toggle="dropdown" type="button">👤 <sec:authentication property="name"/></button><ul class="dropdown-menu dropdown-menu-end"><li><a class="dropdown-item" href="/profile">Hồ sơ cá nhân</a></li><sec:authorize access="hasRole('USER')"><li><a class="dropdown-item" href="/my-orders">Đơn hàng của tôi</a></li></sec:authorize><li><hr class="dropdown-divider"></li><li><form method="post" action="/logout"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><button class="dropdown-item text-danger" type="submit">Đăng xuất</button></form></li></ul></div></sec:authorize><sec:authorize access="!isAuthenticated()"><a class="nav-link" href="/login">Đăng nhập</a><a class="btn btn-primary ms-lg-2" href="/register">Đăng ký</a></sec:authorize></div></div></div></nav><main class="container py-4">
<a href="/" class="text-success text-decoration-none">← Tiếp tục mua hàng</a>
<h2 class="my-3">Giỏ hàng của tôi</h2>
<c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>
<c:if test="${not empty success}"><div class="alert alert-success"><c:out value="${success}"/></div></c:if>
<c:choose><c:when test="${empty cartItems}"><div class="alert alert-light border">Giỏ hàng chưa có món nào. <a href="/#menu">Xem menu</a></div></c:when>
<c:otherwise>
<div class="alert alert-info">Các món cùng chi nhánh sẽ được gộp thành một đơn; món thuộc chi nhánh khác được tách thành đơn riêng.</div>
<c:forEach var="entry" items="${cartItems}" varStatus="loop">
<div class="card p-3 mb-2"><div class="d-flex justify-content-between gap-3"><div><strong><c:out value="${entry.product.name}"/></strong>
<div class="text-muted small">Chi nhánh: <c:out value="${entry.product.shop.name}"/></div>
<div class="small">Size <c:out value="${entry.item.size}"/> · Đường <c:out value="${entry.item.sugar}"/> · Đá <c:out value="${entry.item.ice}"/> · <c:out value="${entry.item.topping}"/></div>
<div class="small">Đơn giá (gồm tùy chọn): ${entry.unitPrice}đ · Số lượng: ${entry.item.quantity} · Tạm tính: ${entry.subtotal}đ</div></div>
<form method="post" action="/cart/update" class="d-flex gap-2 align-items-center ms-auto">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<input type="hidden" name="index" value="${loop.index}">
<input type="number" name="quantity" min="1" max="30" value="${entry.item.quantity}" class="form-control form-control-sm" style="width:75px" aria-label="Số lượng">
<button class="btn btn-outline-success btn-sm" type="submit">Cập nhật</button>
</form><form method="post" action="/cart/remove"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><input type="hidden" name="index" value="${loop.index}"><button class="btn btn-outline-danger btn-sm" type="submit">Xóa</button></form>
</div></div></c:forEach>
<div class="card p-4 mt-3"><h5>Tổng tiền món: ${cartTotal}đ</h5>
<div class="alert alert-info small">Phí demo: 12.000đ cơ bản + 4.000đ mỗi km đường đi (làm tròn lên). Mỗi chi nhánh tính phí riêng. Dịch vụ định tuyến công cộng có thể tạm thời không khả dụng.</div>
<form method="post" action="/cart/checkout"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
<label class="form-label">Hình thức nhận hàng</label><select name="fulfillment" id="fulfillment" class="form-select mb-3"><option value="DELIVERY">Giao tận nơi</option><option value="PICKUP">Nhận tại cửa hàng</option></select>
<div id="deliveryPanel">
<label class="form-label fw-semibold">Tìm địa chỉ giao hàng</label>
<div class="input-group mb-2"><input id="addressSearch" class="form-control" maxlength="180" placeholder="Ví dụ: 1 Võ Văn Ngân, Thủ Đức, TP.HCM" autocomplete="street-address"><button id="searchAddressButton" class="btn btn-outline-success" type="button">Tìm địa chỉ</button></div>
<div id="addressSearchMessage" class="small text-muted mb-2" aria-live="polite">Tìm địa chỉ rồi chọn kết quả, hoặc nhấn trực tiếp lên bản đồ.</div>
<div id="addressResults" class="list-group mb-3" style="max-height:190px;overflow:auto"></div>
<label class="form-label">Chọn điểm giao hàng trên bản đồ (bắt buộc)</label>
<p class="map-hint text-muted">Nhấn vào bản đồ để chọn vị trí nhận. Sau đó nhập địa chỉ chi tiết để Shipper tìm đúng nhà.</p>
<div id="deliveryMap" class="mb-2"></div>
<label class="form-label">Địa chỉ chi tiết / số nhà</label><input name="address" id="deliveryAddress" maxlength="250" class="form-control mb-2" placeholder="Số nhà, đường, phường, thành phố">
<input type="hidden" name="deliveryLatitude" id="deliveryLatitude"><input type="hidden" name="deliveryLongitude" id="deliveryLongitude">
<div id="mapQuote" class="small alert alert-secondary">Chưa chọn điểm giao hàng.</div>
<c:forEach var="sh" items="${cartShops}"><div class="route-shop" data-id="${sh.id}" data-lat="${sh.latitude}" data-lon="${sh.longitude}" data-name="${sh.name}"></div></c:forEach>
</div>
<label class="form-label mt-3" for="voucherCode">Mã giảm giá (không bắt buộc)</label>
<input id="voucherCode" name="voucherCode" maxlength="40" class="form-control mb-2" placeholder="Nhập voucher, ví dụ: WELCOME10">
<p class="small text-muted">Voucher được kiểm tra khi đặt hàng, áp dụng riêng cho mỗi đơn theo chi nhánh.</p>
<label class="form-label fw-semibold" for="paymentMethod">Phương thức thanh toán</label><select class="form-select mb-2" id="paymentMethod" name="paymentMethod"><option value="COD">Thanh toán khi nhận hàng (COD)</option><option value="VIETQR">Chuyển khoản VietQR (Vietcombank)</option></select><p class="small text-muted">Nếu chọn VietQR, sau khi đặt hàng hãy mở chi tiết từng đơn để quét QR. Chỉ nhân viên mới có thể xác nhận đã nhận tiền.</p><button type="submit" class="btn btn-primary w-100" id="checkoutButton" onclick="return confirm('Xác nhận đặt hàng?')">Xác nhận đặt hàng</button>
</form></div></c:otherwise></c:choose><c:if test="${not empty savedAddresses}"><div class="card p-3 my-3"><label>Địa chỉ đã lưu (có thể chọn và sao chép vào ô địa chỉ khi thanh toán)</label><select class="form-select" id="savedAddress"><option value="">-- Chọn địa chỉ --</option><c:forEach var="addr" items="${savedAddresses}"><option value="${fn:escapeXml(addr.address)}"><c:out value="${addr.recipient}"/> – <c:out value="${addr.address}"/></option></c:forEach></select><button type="button" class="btn btn-outline-success mt-2" onclick="const v=document.getElementById('savedAddress').value;const box=document.querySelector('textarea[name=address],input[name=address]');if(box && v){box.value=v;box.dispatchEvent(new Event('input',{bubbles:true}));}">Dùng địa chỉ này</button></div></c:if></main><footer class="footer"><div class="container">UTEMILKTEA · Dự án học tập</div></footer><script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script><script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script>
(function(){
 const method=document.getElementById('fulfillment');if(!method)return;
 const panel=document.getElementById('deliveryPanel'),latInput=document.getElementById('deliveryLatitude'),lonInput=document.getElementById('deliveryLongitude');
 const address=document.getElementById('deliveryAddress'),quote=document.getElementById('mapQuote'),btn=document.getElementById('checkoutButton');
 const shops=[...document.querySelectorAll('.route-shop')].map(el=>({name:el.dataset.name,lat:Number(el.dataset.lat),lon:Number(el.dataset.lon)}));
 let map,marker,routes=[],selected=null,requestVersion=0,quoteReady=false;
 function money(v){return new Intl.NumberFormat('vi-VN').format(v)+'đ';}
 function toggle(){let delivery=method.value==='DELIVERY';panel.style.display=delivery?'block':'none';address.required=delivery;btn.disabled=delivery&&!quoteReady;if(delivery&&map)setTimeout(()=>map.invalidateSize(),80);}
 method.addEventListener('change',toggle);
 if(typeof L==='undefined'){quote.textContent='Không tải được bản đồ. Vui lòng kiểm tra kết nối mạng hoặc chọn nhận tại cửa hàng.';toggle();return;}
 map=L.map('deliveryMap').setView([10.82,106.72],11);
 L.tileLayer('https://a.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png',{maxZoom:19,attribution:'&copy; OpenStreetMap contributors, HOT'}).addTo(map);
 shops.forEach(s=>{if(Number.isFinite(s.lat)&&Number.isFinite(s.lon))L.circleMarker([s.lat,s.lon],{radius:8,color:'#177245'}).addTo(map).bindPopup('Chi nhánh: '+s.name);});
 async function selectDeliveryPoint(e){
  const lat=e.latlng.lat,lon=e.latlng.lng;selected={lat,lon};latInput.value=lat.toFixed(6);lonInput.value=lon.toFixed(6);
  if(marker)map.removeLayer(marker);marker=L.marker([lat,lon]).addTo(map).bindPopup('Điểm giao hàng').openPopup();
  routes.forEach(r=>map.removeLayer(r));routes=[];quoteReady=false;btn.disabled=true;
  const version=++requestVersion;quote.textContent='Đang tính đường đi và phí giao hàng...';
  try{
   let total=0,details=[];
   for(const sh of shops){
    if(!Number.isFinite(sh.lat)||!Number.isFinite(sh.lon))throw Error('Chi nhánh thiếu tọa độ');
    const url='https://router.project-osrm.org/route/v1/driving/'+sh.lon+','+sh.lat+';'+lon+','+lat+'?overview=full&geometries=geojson';
    const response=await fetch(url);if(!response.ok)throw Error('Dịch vụ tuyến đường chưa sẵn sàng');
    const data=await response.json();const route=data.routes&&data.routes[0];if(!route)throw Error('Không tìm thấy tuyến đường');
    if(route.distance>25000)throw Error('Một chi nhánh cách điểm giao hơn 25 km');
    const fee=12000+4000*Math.ceil(route.distance/1000);total+=fee;
    details.push(sh.name+': '+(Math.ceil(route.distance/100)/10).toFixed(1)+' km · '+money(fee)+' · khoảng '+(10+Math.ceil(route.duration/60))+' phút');
    if(version!==requestVersion)return;
    routes.push(L.geoJSON(route.geometry,{style:{color:'#2d8b57',weight:4,opacity:.6}}).addTo(map));
   }
   if(version!==requestVersion)return;
   quote.textContent=details.join(' | ')+' | Tổng phí giao hàng dự kiến: '+money(total);
   quoteReady=true;btn.disabled=false;
  }catch(err){if(version===requestVersion){quote.textContent='Không thể tính phí: '+err.message+'. Bạn có thể chọn nhận tại cửa hàng.';btn.disabled=true;}}
 }
 map.on('click',selectDeliveryPoint);

 // Geocoding only after a deliberate button click (no automatic requests while typing).
 const searchInput=document.getElementById('addressSearch');
 const searchButton=document.getElementById('searchAddressButton');
 const searchMessage=document.getElementById('addressSearchMessage');
 const searchResults=document.getElementById('addressResults');
 let searchBusy=false;
 async function searchAddress(){
  const q=searchInput.value.trim();
  if(q.length<3){searchMessage.textContent='Vui lòng nhập địa chỉ ít nhất 3 ký tự.';return;}
  if(searchBusy)return;
  searchBusy=true;searchButton.disabled=true;searchResults.replaceChildren();
  searchMessage.textContent='Đang tìm địa chỉ...';
  try{
   // Photon: GeoJSON, coordinates are [longitude, latitude].
   // Bias results toward Ho Chi Minh City, without restricting other areas.
   const endpoint='https://photon.komoot.io/api/?q='+encodeURIComponent(q)+'&limit=5&lat=10.82&lon=106.72';
   const response=await fetch(endpoint,{headers:{'Accept':'application/json'}});
   if(!response.ok)throw Error('Dịch vụ tìm địa chỉ chưa sẵn sàng (HTTP '+response.status+')');
   const data=await response.json();
   const features=Array.isArray(data.features)?data.features:[];
   if(!features.length){searchMessage.textContent='Không tìm thấy địa chỉ. Thử tên đường và quận/thành phố hoặc chọn trực tiếp trên bản đồ.';return;}
   searchMessage.textContent='Chọn một kết quả phù hợp (có thể điều chỉnh ghim trên bản đồ):';
   let count=0;
   features.forEach(item=>{
    const coords=item.geometry&&item.geometry.coordinates;
    if(!Array.isArray(coords)||coords.length<2)return;
    const lon=Number(coords[0]),lat=Number(coords[1]);
    if(!Number.isFinite(lat)||!Number.isFinite(lon)||Math.abs(lat)>90||Math.abs(lon)>180)return;
    const p=item.properties||{};
    const parts=[p.name,p.housenumber,p.street,p.locality,p.district,p.city,p.state,p.country].filter(Boolean);
    const label=[...new Set(parts)].join(', ')||q;
    const choice=document.createElement('button');
    choice.type='button';choice.className='list-group-item list-group-item-action small';
    choice.textContent=label;
    choice.addEventListener('click',()=>{
     map.setView([lat,lon],16);
     // Preserve the customer's entered house number: Photon may only resolve the street.
     address.value=q.slice(0,250);
     searchResults.replaceChildren();
     searchMessage.textContent='Đã chọn vị trí gần địa chỉ tìm kiếm. Kiểm tra lại ghim trên bản đồ và nhập số nhà/địa chỉ chi tiết trước khi đặt.';
     selectDeliveryPoint({latlng:L.latLng(lat,lon)});
    });
    searchResults.appendChild(choice);count++;
   });
   if(!count)searchMessage.textContent='Không có kết quả tọa độ hợp lệ. Vui lòng chọn trên bản đồ.';
  }catch(err){searchMessage.textContent='Không thể tìm địa chỉ: '+err.message+'. Hãy chọn trực tiếp trên bản đồ.';}
  finally{searchBusy=false;searchButton.disabled=false;}
 }
 searchButton.addEventListener('click',searchAddress);
 searchInput.addEventListener('keydown',e=>{if(e.key==='Enter'){e.preventDefault();searchAddress();}});

 document.querySelector('form[action="/cart/checkout"]').addEventListener('submit',function(e){if(method.value==='DELIVERY'&&(!selected||!quoteReady)){e.preventDefault();alert('Vui lòng chọn điểm trên bản đồ và đợi tính phí giao hàng.');}});
 toggle();
})();
</script></body></html>