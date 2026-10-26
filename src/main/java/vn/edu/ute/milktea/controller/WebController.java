package vn.edu.ute.milktea.controller;
import jakarta.servlet.http.HttpSession;
import vn.edu.ute.milktea.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ute.milktea.model.*;
import vn.edu.ute.milktea.repository.*;
import vn.edu.ute.milktea.service.OrderService;
@Controller public class WebController {
    private final ShopRepository shops;
    private final ProductRepository products;
    private final UserRepository users;
    private final OrderRepository orders;
    private final OrderService service;
    private final PasswordEncoder encoder;
    private final CartService cart;
    private final FavoriteRepository favorites;
    private final ProductReviewRepository reviews;
    private final VoucherRepository vouchers;
    private final vn.edu.ute.milktea.service.CloudinaryImageService imageService;
    private final vn.edu.ute.milktea.service.RealtimeNotificationService realtime;
    @org.springframework.beans.factory.annotation.Autowired private DeliveryAddressRepository addressRepository;
    @org.springframework.beans.factory.annotation.Autowired private RecentlyViewedRepository recentRepository;
    public WebController(ShopRepository s,ProductRepository p,UserRepository u,OrderRepository o,OrderService sv,PasswordEncoder e,CartService cart,FavoriteRepository favorites,ProductReviewRepository reviews,VoucherRepository vouchers,vn.edu.ute.milktea.service.CloudinaryImageService imageService,vn.edu.ute.milktea.service.RealtimeNotificationService realtime){
        this.imageService=imageService;
        this.realtime=realtime;
        this.vouchers=vouchers;
        this.favorites=favorites;
        this.reviews=reviews;
        this.cart=cart;
        shops=s;
        products=p;
        users=u;
        orders=o;
        service=sv;
        encoder=e;
    }
    @GetMapping("/") String home(Model m){
        m.addAttribute("shops",shops.findAll());
        // Featured list: favor products with their OWN uploaded image, then fill the remaining
        // slots with the newest active products. A similarly named drink from a different
        // shop is a different Product row and must not reuse its image implicitly.
        var candidates=products.findAll().stream()
        .filter(p->p.available&&p.shop!=null&&p.shop.active)
        .sorted(java.util.Comparator.comparing((Product p)->p.id).reversed()).toList();
        var featured=new java.util.LinkedHashMap<Long,Product>();
        candidates.stream().filter(p->p.imageUrl!=null&&!p.imageUrl.isBlank()).limit(12)
        .forEach(p->featured.put(p.id,p));
        candidates.forEach(p->{
            if(featured.size()<12)featured.putIfAbsent(p.id,p);
        }
        );
        m.addAttribute("products",featured.values().stream()
        .sorted(java.util.Comparator.comparing((Product p)->p.id).reversed()).toList());
        return "home";
    }
    @GetMapping("/shop/{id}") String shop(@PathVariable Long id,Model m){
        m.addAttribute("shop",shops.findById(id).orElseThrow());
        m.addAttribute("products",products.findByShopIdAndAvailableTrue(id));
        return "shop";
    }
    @GetMapping("/products/search") String search(
    @RequestParam(defaultValue="") String q,
    @RequestParam(defaultValue="") String category,
    @RequestParam(required=false) Long shopId,
    @RequestParam(required=false) java.math.BigDecimal minPrice,
    @RequestParam(required=false) java.math.BigDecimal maxPrice,
    @RequestParam(defaultValue="name_asc") String sort,
    @RequestParam(defaultValue="0") int page,Model m){
        if(minPrice!=null&&minPrice.signum()<0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Giá tối thiểu không hợp lệ");
        if(maxPrice!=null&&maxPrice.signum()<0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Giá tối đa không hợp lệ");
        if(minPrice!=null&&maxPrice!=null&&minPrice.compareTo(maxPrice)>0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Giá tối thiểu lớn hơn giá tối đa");
        var catalog=products.findAll().stream().filter(p->p.available&&p.shop!=null&&p.shop.active).toList();
        var categories=catalog.stream().map(p->p.category).filter(java.util.Objects::nonNull).map(String::trim).filter(s->!s.isEmpty()).distinct().sorted(String.CASE_INSENSITIVE_ORDER).toList();
        String keyword=q.trim().toLowerCase(java.util.Locale.ROOT);
        var filtered=catalog.stream().filter(p->keyword.isEmpty()||(p.name!=null&&p.name.toLowerCase(java.util.Locale.ROOT).contains(keyword)))
        .filter(p->category.isBlank()||(p.category!=null&&p.category.equalsIgnoreCase(category.trim())))
        .filter(p->shopId==null||shopId.equals(p.shop.id))
        .filter(p->p.price!=null&&(minPrice==null||p.price.compareTo(minPrice)>=0)&&(maxPrice==null||p.price.compareTo(maxPrice)<=0))
        .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        java.util.Comparator<Product> byName=java.util.Comparator.comparing(p->p.name==null?"":p.name,String.CASE_INSENSITIVE_ORDER);
        switch(sort){
            case "price_asc" -> filtered.sort(java.util.Comparator.comparing((Product p)->p.price).thenComparing(byName));
            case "price_desc" -> filtered.sort(java.util.Comparator.comparing((Product p)->p.price).reversed().thenComparing(byName));
            case "rating_desc" -> {
                var avg=reviews.findAll().stream().collect(java.util.stream.Collectors.groupingBy(r->r.product.id,java.util.stream.Collectors.averagingInt(r->r.rating)));
                filtered.sort(java.util.Comparator.comparingDouble((Product p)->avg.getOrDefault(p.id,0d)).reversed().thenComparing(byName));
            }
            case "favorites_desc" -> {
                var cnt=favorites.findAll().stream().collect(java.util.stream.Collectors.groupingBy(f->f.product.id,java.util.stream.Collectors.counting()));
                filtered.sort(java.util.Comparator.comparingLong((Product p)->cnt.getOrDefault(p.id,0L)).reversed().thenComparing(byName));
            }
            case "popular_desc" -> {
                java.util.Map<Long,Integer> cnt=new java.util.HashMap<>();
                orders.findAll().stream().filter(o->"DELIVERED".equals(o.status)).forEach(o->o.lines.forEach(l->{
                    if(l.productId!=null)cnt.merge(l.productId,l.quantity,Integer::sum);
                }
                ));
                filtered.sort(java.util.Comparator.comparingInt((Product p)->cnt.getOrDefault(p.id,0)).reversed().thenComparing(byName));
            }
            case "newest" -> filtered.sort(java.util.Comparator.comparing((Product p)->p.id).reversed());
            default -> {
                sort="name_asc";
                filtered.sort(byName);
            }
        }
        int pageSize=20,total=filtered.size(),totalPages=Math.max(1,(total+pageSize-1)/pageSize);
        page=Math.max(0,Math.min(page,totalPages-1));
        m.addAttribute("products",filtered.subList(page*pageSize,Math.min(total,(page+1)*pageSize)));
        m.addAttribute("q",q);
        m.addAttribute("category",category);
        m.addAttribute("shopId",shopId);
        m.addAttribute("minPrice",minPrice);
        m.addAttribute("maxPrice",maxPrice);
        m.addAttribute("sort",sort);
        m.addAttribute("categories",categories);
        m.addAttribute("shops",shops.findAll());
        m.addAttribute("page",page);
        m.addAttribute("totalPages",totalPages);
        m.addAttribute("total",total);
        return "search";
    }
    @GetMapping("/login") String login(){
        return "login";
    }
    @GetMapping("/register") String register(){
        return "register";
    }
    @GetMapping("/order/{id}") String orderForm(@PathVariable Long id,Model m,Authentication a){
        if(a!=null&&a.isAuthenticated()&&users.findByEmail(a.getName()).isPresent()&&"USER".equals(users.findByEmail(a.getName()).get().role)){
            var record=recentRepository.findByUserEmailAndProductId(a.getName(),id).orElseGet(RecentlyViewed::new);
            record.user=users.findByEmail(a.getName()).orElseThrow();
            record.product=products.findById(id).orElseThrow();
            record.viewedAt=LocalDateTime.now();
            recentRepository.save(record);
        }
        m.addAttribute("product",products.findById(id).orElseThrow());
        m.addAttribute("shops",shops.findAll());
        m.addAttribute("reviews",reviews.findByProductIdOrderByCreatedAtDesc(id));
        return "order";
    }
    @PostMapping("/order") String place(@RequestParam Long productId,@RequestParam String size,@RequestParam String sugar,@RequestParam String ice,@RequestParam(defaultValue="Không") String topping,@RequestParam int quantity,@RequestParam String fulfillment,@RequestParam(required=false) String address,Authentication auth,RedirectAttributes ra){
        try{
            service.place(auth.getName(),productId,size,sugar,ice,topping,quantity,fulfillment,address);
            realtime.orderChanged();
            ra.addFlashAttribute("success","Đặt hàng thành công!");
            return "redirect:/my-orders";
        }
        catch(Exception ex){
            ra.addFlashAttribute("error",ex.getMessage());
            return "redirect:/order/"+productId;
        }
    }
    @GetMapping("/my-orders") String myOrders(Authentication a,Model m){
        var customerOrders=orders.findByCustomerEmailOrderByCreatedAtDesc(a.getName());
        m.addAttribute("orders",customerOrders);
        var catalog=products.findAll();
        java.util.Map<Long,java.util.List<Product>> reviewProductsByOrder=new java.util.HashMap<>();
        for(var o:customerOrders){
            if(!"DELIVERED".equals(o.status))continue;
            java.util.LinkedHashMap<Long,Product> matches=new java.util.LinkedHashMap<>();
            if(o.lines!=null&&!o.lines.isEmpty()){
                for(var line:o.lines){
                    if(line.productId!=null){
                        for(var p:catalog){
                            if(line.productId.equals(p.id)&&p.shop.id.equals(o.shop.id))matches.put(p.id,p);
                        }
                    }
                }
            }
            else if(o.productName!=null){
                for(var p:catalog){
                    if(p.shop.id.equals(o.shop.id)&&p.name.equalsIgnoreCase(o.productName.trim()))matches.put(p.id,p);
                }
            }
            reviewProductsByOrder.put(o.id,new java.util.ArrayList<>(matches.values()));
        }
        m.addAttribute("reviewProductsByOrder",reviewProductsByOrder);
        return "my-orders";
    }
    @GetMapping("/vendor") String vendor(Authentication a,Model m){
        m.addAttribute("shops",shops.findAll().stream().filter(x->a.getName().equalsIgnoreCase(x.vendorEmail)).toList());
        return "vendor";
    }
    @GetMapping("/vendor/orders/{id}") String vendorOrders(@PathVariable Long id,Authentication a,Model m){
        var sh=shops.findById(id).orElseThrow();
        if(!a.getName().equalsIgnoreCase(sh.vendorEmail))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        m.addAttribute("shop",sh);
        m.addAttribute("orders",orders.findByShopIdOrderByCreatedAtDesc(id));
        m.addAttribute("products",products.findByShopIdAndAvailableTrue(id));
        m.addAttribute("allProducts",products.findAll().stream().filter(p->p.shop.id.equals(id)).toList());
        m.addAttribute("deliveredCount",orders.findByShopIdOrderByCreatedAtDesc(id).stream().filter(o->"DELIVERED".equals(o.status)).count());
        m.addAttribute("shopRevenue",orders.findByShopIdOrderByCreatedAtDesc(id).stream().filter(o->"DELIVERED".equals(o.status)).map(o->o.total==null?java.math.BigDecimal.ZERO:o.total.subtract(o.shippingFee==null?java.math.BigDecimal.ZERO:o.shippingFee)).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add));
        m.addAttribute("shippers",users.findAll().stream().filter(x->"SHIPPER".equals(x.role)).toList());
        return "vendor-orders";
    }
    @PostMapping("/vendor/orders/{id}/status") String status(@PathVariable Long id,@RequestParam String status,@RequestParam(required=false) String reason,Authentication a){
        var o=orders.findById(id).orElseThrow();
        if(!a.getName().equalsIgnoreCase(o.shop.vendorEmail))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        boolean allowed=("NEW".equals(o.status)&&java.util.Set.of("CONFIRMED","CANCELLED").contains(status))||("CONFIRMED".equals(o.status)&&java.util.Set.of("PREPARING","CANCELLED").contains(status))||("PREPARING".equals(o.status)&&"READY".equals(status))||("READY".equals(o.status)&&"PICKUP".equals(o.fulfillment)&&"DELIVERED".equals(status));
        if(!allowed)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chuyển trạng thái không hợp lệ");
        if("CANCELLED".equals(status)){
            if(reason==null||reason.isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cần nhập lý do hủy");
            o.cancelReason=reason.trim();
            o.cancelledBy=a.getName();
            o.cancelledAt=LocalDateTime.now();
        }
        o.status=status;
        if("DELIVERED".equals(status))o.deliveredAt=LocalDateTime.now();
        orders.save(o);
        realtime.orderChanged();
        return "redirect:/vendor/orders/"+o.shop.id;
    }
    @PostMapping("/vendor/orders/{id}/assign") String assign(@PathVariable Long id,@RequestParam String shipperEmail,Authentication a){
        var o=orders.findById(id).orElseThrow();
        if(!a.getName().equalsIgnoreCase(o.shop.vendorEmail))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if(!"DELIVERY".equals(o.fulfillment)||!"READY".equals(o.status))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Đơn chưa sẵn sàng giao");
        var shipper=users.findByEmail(shipperEmail).orElseThrow();
        if(!"SHIPPER".equals(shipper.role))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        o.shipperEmail=shipper.email;
        o.status="ASSIGNED";
        orders.save(o);
        realtime.orderChanged();
        return "redirect:/vendor/orders/"+o.shop.id;
    }
    @PostMapping("/shipper/orders/{id}/status") String shipperStatus(@PathVariable Long id,@RequestParam String status,Authentication a){
        var o=orders.findById(id).orElseThrow();
        if(o.shipperEmail==null||!o.shipperEmail.equalsIgnoreCase(a.getName()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if("ASSIGNED".equals(o.status)&&"SHIPPING".equals(status)){
            o.status="SHIPPING";
            o.pickedUpAt=LocalDateTime.now();
        }
        else if("SHIPPING".equals(o.status)&&"DELIVERED".equals(status)){
            o.status="DELIVERED";
            o.deliveredAt=LocalDateTime.now();
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chuyển trạng thái không hợp lệ");
        orders.save(o);
        realtime.orderChanged();
        return "redirect:/shipper";
    }
    @GetMapping("/cart") String cartPage(HttpSession session,Model m,Authentication a){
        if(a!=null&&a.isAuthenticated())m.addAttribute("savedAddresses",addressRepository.findByUserEmailOrderByIdDesc(a.getName()));
        var items=cart.display(session);
        m.addAttribute("cartItems",items);
        m.addAttribute("cartShops",items.stream().map(x->x.getProduct().shop).distinct().toList());
        m.addAttribute("cartTotal",items.stream().map(CartService.DisplayItem::getSubtotal).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add));
        return "cart";
    }
    @PostMapping("/cart/add") String addCart(@RequestParam Long productId,@RequestParam String size,@RequestParam String sugar,@RequestParam String ice,@RequestParam(defaultValue="Không") String topping,@RequestParam int quantity,HttpSession session,RedirectAttributes ra){
        try{
            cart.add(session,productId,size,sugar,ice,topping,quantity);
            ra.addFlashAttribute("success","Đã thêm món vào giỏ hàng");
            return "redirect:/cart";
        }
        catch(IllegalArgumentException e){
            ra.addFlashAttribute("error",e.getMessage());
            return "redirect:/order/"+productId;
        }
    }
    @PostMapping("/cart/update") String updateCart(@RequestParam int index,@RequestParam int quantity,HttpSession session,RedirectAttributes ra){
        try{
            cart.update(session,index,quantity);
            ra.addFlashAttribute("success","Đã cập nhật số lượng");
        }
        catch(IllegalArgumentException e){
            ra.addFlashAttribute("error",e.getMessage());
        }
        return "redirect:/cart";
    }
    @PostMapping("/cart/remove") String removeCart(@RequestParam int index,HttpSession session,RedirectAttributes ra){
        cart.remove(session,index);
        return "redirect:/cart";
    }
    @PostMapping("/cart/checkout") String checkoutCart(@RequestParam String fulfillment,@RequestParam(required=false) String address,@RequestParam(required=false) Double deliveryLatitude,@RequestParam(required=false) Double deliveryLongitude,@RequestParam(required=false) String voucherCode,@RequestParam(defaultValue="COD") String paymentMethod,Authentication a,HttpSession session,RedirectAttributes ra){
        try{
            int n=cart.checkout(session,a.getName(),fulfillment,address,deliveryLatitude,deliveryLongitude,voucherCode,paymentMethod);
            realtime.orderChanged();
            ra.addFlashAttribute("success","Đã tạo "+n+" đơn hàng từ giỏ hàng");
            return "redirect:/my-orders";
        }
        catch(Exception e){
            ra.addFlashAttribute("error",e.getMessage());
            return "redirect:/cart";
        }
    }
    @GetMapping("/profile") String profile(Authentication a,Model m){
        m.addAttribute("account",users.findByEmail(a.getName()).orElseThrow());
        return "profile";
    }
    @GetMapping("/manager") String manager(Authentication a,Model m){
        String email=a.getName();
        var assigned=shops.findAll().stream().filter(sh->sh.managerEmail!=null&&sh.managerEmail.equalsIgnoreCase(email)).toList();
        var ids=assigned.stream().map(sh->sh.id).collect(java.util.stream.Collectors.toSet());
        var scopedOrders=orders.findAll().stream().filter(o->o.shop!=null&&ids.contains(o.shop.id)).toList();
        m.addAttribute("shops",assigned);
        m.addAttribute("orders",scopedOrders);
        m.addAttribute("products",products.findAll().stream().filter(p->p.shop!=null&&ids.contains(p.shop.id)).toList());
        var delivered=scopedOrders.stream().filter(o->"DELIVERED".equals(o.status)).toList();
        m.addAttribute("deliveredOrders",delivered);
        m.addAttribute("revenue",delivered.stream().map(o->o.total==null?java.math.BigDecimal.ZERO:o.total.subtract(o.shippingFee==null?java.math.BigDecimal.ZERO:o.shippingFee)).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add));
        return "dashboard";
    }
    @GetMapping("/admin") String admin(Model m){
        m.addAttribute("shops",shops.findAll());
        m.addAttribute("users",users.findAll());
        m.addAttribute("orders",orders.findAll());
        m.addAttribute("products",products.findAll());
        m.addAttribute("deliveredOrders",orders.findAll().stream().filter(o->"DELIVERED".equals(o.status)).toList());
        m.addAttribute("revenue",orders.findAll().stream().filter(o->"DELIVERED".equals(o.status)).map(o->o.total==null?java.math.BigDecimal.ZERO:o.total.subtract(o.shippingFee==null?java.math.BigDecimal.ZERO:o.shippingFee)).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add));
        return "dashboard";
    }
    @GetMapping("/shipper") String shipper(Authentication a,Model m){
        var list=orders.findByShipperEmailOrderByCreatedAtDesc(a.getName());
        m.addAttribute("orders",list);
        m.addAttribute("shipperTotal",list.size());
        m.addAttribute("shipperDelivered",list.stream().filter(o->"DELIVERED".equals(o.status)).count());
        m.addAttribute("shipperActive",list.stream().filter(o->"ASSIGNED".equals(o.status)||"SHIPPING".equals(o.status)).count());
        return "shipper";
    }
    @PostMapping("/vendor/products/{id}/availability") String productAvailability(@PathVariable Long id,Authentication a){
        var p=products.findById(id).orElseThrow();
        if(!a.getName().equalsIgnoreCase(p.shop.vendorEmail))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        p.available=!p.available;
        products.save(p);
        return "redirect:/vendor/orders/"+p.shop.id;
    }
    @PostMapping("/vendor/shop/{id}/hours") String hours(@PathVariable Long id,@RequestParam String opensAt,@RequestParam String closesAt,Authentication a){
        var sh=shops.findById(id).orElseThrow();
        if(!a.getName().equalsIgnoreCase(sh.vendorEmail))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        try{
            sh.opensAt=java.time.LocalTime.parse(opensAt);
            sh.closesAt=java.time.LocalTime.parse(closesAt);
        }
        catch(java.time.format.DateTimeParseException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Giờ không hợp lệ");
        }
        shops.save(sh);
        return "redirect:/vendor/orders/"+id;
    }
    // Customer may cancel only a new, unprocessed order.
    @PostMapping("/my-orders/{id}/cancel")
    String customerCancel(@PathVariable Long id,@RequestParam String reason,Authentication a,RedirectAttributes ra){
        var o=orders.findById(id).orElseThrow();
        if(!o.customer.email.equalsIgnoreCase(a.getName()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if(!"NEW".equals(o.status)){
            ra.addFlashAttribute("error","Chỉ có thể hủy đơn đang chờ xác nhận.");
            return "redirect:/my-orders";
        }
        if(reason==null||reason.isBlank()||reason.trim().length()>500){
            ra.addFlashAttribute("error","Lý do hủy phải có từ 1 đến 500 ký tự.");
            return "redirect:/my-orders";
        }
        o.status="CANCELLED";
        o.cancelReason=reason.trim();
        o.cancelledBy=a.getName();
        o.cancelledAt=LocalDateTime.now();
        orders.save(o);
        realtime.orderChanged();
        ra.addFlashAttribute("success","Đã hủy đơn #"+id);
        return "redirect:/my-orders";
    }
    // Vendor can edit only products belonging to their assigned shop.
    @PostMapping("/vendor/products/{id}/edit")
    String editProduct(@PathVariable Long id,@RequestParam String name,@RequestParam String category,@RequestParam java.math.BigDecimal price,Authentication a,RedirectAttributes ra){
        var p=products.findById(id).orElseThrow();
        if(!a.getName().equalsIgnoreCase(p.shop.vendorEmail))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if(name==null||name.isBlank()||name.trim().length()>255||category==null||category.isBlank()||category.trim().length()>255||price==null||price.signum()<=0||price.compareTo(new java.math.BigDecimal("10000000"))>0){
            ra.addFlashAttribute("error","Tên, danh mục hoặc giá bán không hợp lệ.");
            return "redirect:/vendor/orders/"+p.shop.id;
        }
        p.name=name.trim();
        p.category=category.trim();
        p.price=price;
        products.save(p);
        ra.addFlashAttribute("success","Đã cập nhật sản phẩm.");
        return "redirect:/vendor/orders/"+p.shop.id;
    }
    // Admin role assignment: allowlisted roles; do not demote the current admin.
    @PostMapping("/admin/users/{id}/role")
    String changeRole(@PathVariable Long id,@RequestParam String role,Authentication a,RedirectAttributes ra){
        if(!java.util.Set.of("USER","VENDOR","MANAGER","ADMIN","SHIPPER").contains(role))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var user=users.findById(id).orElseThrow();
        if(user.email.equalsIgnoreCase(a.getName())){
            ra.addFlashAttribute("error","Không thể tự đổi vai trò tài khoản đang đăng nhập.");
            return "redirect:/admin";
        }
        user.role=role;
        users.save(user);
        ra.addFlashAttribute("success","Đã cập nhật vai trò. Người dùng cần đăng nhập lại.");
        return "redirect:/admin";
    }
    @GetMapping("/favorites") String favoritePage(Authentication a,Model m){
        m.addAttribute("favorites",favorites.findByUserEmailOrderByIdDesc(a.getName()));
        return "favorites";
    }
    @PostMapping("/favorites/{id}/toggle") String toggleFavorite(@PathVariable Long id,Authentication a,RedirectAttributes ra){
        var user=users.findByEmail(a.getName()).orElseThrow();
        var product=products.findById(id).orElseThrow();
        var existing=favorites.findByUserEmailAndProductId(a.getName(),id);
        if(existing.isPresent()){
            favorites.delete(existing.get());
            ra.addFlashAttribute("success","Đã bỏ khỏi yêu thích.");
        }
        else{
            var f=new Favorite();
            f.user=user;
            f.product=product;
            favorites.save(f);
            ra.addFlashAttribute("success","Đã thêm vào yêu thích.");
        }
        return "redirect:/favorites";
    }
    @PostMapping("/products/{id}/review") String reviewProduct(@PathVariable Long id,@RequestParam int rating,@RequestParam(required=false,defaultValue="") String comment,@RequestParam(value="media",required=false) org.springframework.web.multipart.MultipartFile media,Authentication a,RedirectAttributes ra){
        if(rating<1||rating>5||comment==null||comment.trim().length()<50||comment.trim().length()>500){
            ra.addFlashAttribute("error","Đánh giá phải từ 1 đến 5 sao và nội dung từ 50 đến 500 ký tự.");
            return "redirect:/order/"+id;
        }
        var product=products.findById(id).orElseThrow();
        boolean purchased=orders.findByCustomerEmailOrderByCreatedAtDesc(a.getName()).stream().anyMatch(o->
        "DELIVERED".equals(o.status)&&o.shop!=null&&o.shop.id.equals(product.shop.id)&&(
        (o.lines!=null&&!o.lines.isEmpty()&&o.lines.stream().anyMatch(l->id.equals(l.productId)))||
        ((o.lines==null||o.lines.isEmpty())&&o.productName!=null&&o.productName.trim().equalsIgnoreCase(product.name))
        ));
        if(!purchased){
            ra.addFlashAttribute("error","Chỉ được đánh giá món đã mua trong đơn giao thành công.");
            return "redirect:/order/"+id;
        }
        var review=reviews.findByUserEmailAndProductId(a.getName(),id).orElseGet(ProductReview::new);
        review.user=users.findByEmail(a.getName()).orElseThrow();
        review.product=product;
        review.rating=rating;
        review.comment=comment.trim();
        review.createdAt=LocalDateTime.now();
        if(media!=null&&!media.isEmpty()){
            try {
                review.mediaUrl=imageService.uploadReviewMedia(media);
                review.mediaType=review.mediaUrl.contains("/video/upload/")?"VIDEO":"IMAGE";
            }
            catch(Exception ex){
                org.slf4j.LoggerFactory.getLogger(WebController.class).error("Cloudinary review media upload failed for product {} (type: {})",id,ex.getClass().getSimpleName(),ex);
                String message=(ex instanceof IllegalArgumentException || ex instanceof IllegalStateException)
                ? ex.getMessage() : "Không thể tải tệp lên Cloudinary. Kiểm tra kết nối, quyền upload và Console STS.";
                ra.addFlashAttribute("error",message);
                return "redirect:/order/"+id;
            }
        }
        reviews.save(review);
        ra.addFlashAttribute("success","Đã lưu đánh giá của bạn.");
        return "redirect:/order/"+id;
    }
    @GetMapping("/manager/vouchers") String vouchersPage(Authentication a,Model m){
        if(!"ADMIN".equals(users.findByEmail(a.getName()).orElseThrow().role))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        m.addAttribute("vouchers",vouchers.findAll());
        return "vouchers";
    }
    @PostMapping("/manager/vouchers") String createVoucher(@RequestParam String code,@RequestParam java.math.BigDecimal percent,@RequestParam java.math.BigDecimal minSubtotal,@RequestParam java.math.BigDecimal maxDiscount,@RequestParam(required=false) String expiresOn,Authentication a,RedirectAttributes ra){
        if(!"ADMIN".equals(users.findByEmail(a.getName()).orElseThrow().role))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        String normalized=code==null?"":code.trim().toUpperCase(java.util.Locale.ROOT);
        if(!normalized.matches("[A-Z0-9_-]{3,40}")||percent==null||percent.signum()<=0||percent.compareTo(new java.math.BigDecimal("100"))>0||minSubtotal==null||minSubtotal.signum()<0||maxDiscount==null||maxDiscount.signum()<=0){
            ra.addFlashAttribute("error","Thông tin mã giảm giá không hợp lệ");
            return "redirect:/manager/vouchers";
        }
        if(vouchers.findByCodeIgnoreCase(normalized).isPresent()){
            ra.addFlashAttribute("error","Mã giảm giá đã tồn tại");
            return "redirect:/manager/vouchers";
        }
        var v=new Voucher();
        v.code=normalized;
        v.percent=percent;
        v.minSubtotal=minSubtotal;
        v.maxDiscount=maxDiscount;
        try{
            if(expiresOn!=null&&!expiresOn.isBlank()){
                v.expiresOn=java.time.LocalDate.parse(expiresOn);
                if(v.expiresOn.isBefore(java.time.LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")))){
                    ra.addFlashAttribute("error","Ngày hết hạn không được ở trong quá khứ");
                    return "redirect:/manager/vouchers";
                }
            }
        }
        catch(Exception ex){
            ra.addFlashAttribute("error","Ngày hết hạn không hợp lệ");
            return "redirect:/manager/vouchers";
        }
        vouchers.save(v);
        ra.addFlashAttribute("success","Đã tạo voucher "+normalized);
        return "redirect:/manager/vouchers";
    }
    @PostMapping("/manager/vouchers/{id}/toggle") String toggleVoucher(@PathVariable Long id,Authentication a,RedirectAttributes ra){
        if(!"ADMIN".equals(users.findByEmail(a.getName()).orElseThrow().role))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        var v=vouchers.findById(id).orElseThrow();
        v.active=!v.active;
        vouchers.save(v);
        ra.addFlashAttribute("success","Đã cập nhật voucher");
        return "redirect:/manager/vouchers";
    }
    @GetMapping("/my-orders/{id}/detail") String orderDetail(@PathVariable Long id,Authentication a,Model m){
        var o=orders.findById(id).orElseThrow();
        if(!o.customer.email.equalsIgnoreCase(a.getName()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        m.addAttribute("order",o);
        return "order-detail";
    }
    @PostMapping("/vendor/products/{id}/image")
    String uploadProductImage(@PathVariable Long id,@RequestParam("image") org.springframework.web.multipart.MultipartFile image,Authentication a,RedirectAttributes ra){
        var product=products.findById(id).orElseThrow();
        if(!a.getName().equalsIgnoreCase(product.shop.vendorEmail))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        try{
            product.imageUrl=imageService.upload(image);
            products.save(product);
            ra.addFlashAttribute("success","Đã cập nhật ảnh sản phẩm");
        }
        catch(Exception ex){
            org.slf4j.LoggerFactory.getLogger(WebController.class).error("Cloudinary product image upload failed for product {} (exception type: {})",id,ex.getClass().getSimpleName(),ex);
            String message = (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException)
            ? ex.getMessage() : "Tải ảnh thất bại. Xem Console STS để biết nguyên nhân (không chia sẻ API Secret).";
            ra.addFlashAttribute("error","Không thể tải ảnh: "+message);
        }
        return "redirect:/vendor/orders/"+product.shop.id;
    }
}
