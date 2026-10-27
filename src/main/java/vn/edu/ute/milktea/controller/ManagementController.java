package vn.edu.ute.milktea.controller;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ute.milktea.model.*;
import vn.edu.ute.milktea.repository.*;
/** Administration by scope: MANAGER only assigned shops; ADMIN all shops. */
@Controller
public class ManagementController {
    private final ShopRepository shops;
    private final ProductRepository products;
    private final UserRepository users;
    private final OrderRepository orders;
    private final ShopPromotionRepository promotions;
    private final DeliveryProviderRepository carriers;
    private final ShopCustomerBlockRepository blocks;
    public ManagementController(ShopRepository s, ProductRepository p, UserRepository u, OrderRepository o,ShopPromotionRepository pr,DeliveryProviderRepository carriers,ShopCustomerBlockRepository blocks){
        shops=s;
        products=p;
        users=u;
        orders=o;
        promotions=pr;
        this.carriers=carriers;
        this.blocks=blocks;
    }
    private AppUser actor(Authentication a){
        return users.findByEmail(a.getName()).orElseThrow();
    }
    private boolean isAdmin(Authentication a){
        return "ADMIN".equals(actor(a).role);
    }
    private void admin(Authentication a){
        if(!isAdmin(a))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    private void scope(Authentication a,Shop shop){
        AppUser u=actor(a);
        if(!"ADMIN".equals(u.role)&&(!"MANAGER".equals(u.role)||shop.managerEmail==null||!shop.managerEmail.equalsIgnoreCase(u.email)))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Không có quyền với chi nhánh này");
    }
    private Shop authorizedShop(Authentication a,Long shopId){
        Shop s=shops.findById(shopId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        scope(a,s);
        return s;
    }
    private void validateName(String s,int max){
        if(s==null||s.isBlank()||s.trim().length()>max)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tên không hợp lệ");
    }
    private void validatePrice(BigDecimal price){
        if(price==null||price.signum()<=0||price.compareTo(new BigDecimal("10000000"))>0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Giá không hợp lệ");
    }
    @GetMapping("/manager/catalog") String catalog(Authentication a,@RequestParam(required=false) Long shopId,Model m){
        AppUser u=actor(a);
        if(!Set.of("MANAGER","ADMIN").contains(u.role))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        List<Shop> permitted=shops.findAll().stream().filter(s->"ADMIN".equals(u.role)||(s.managerEmail!=null&&s.managerEmail.equalsIgnoreCase(u.email))).toList();
        Shop selected=null;
        if(shopId!=null){
            selected=authorizedShop(a,shopId);
        }
        else if(!permitted.isEmpty()){
            selected=permitted.get(0);
        }
        m.addAttribute("permittedShops",permitted);
        m.addAttribute("selected",selected);
        if(selected!=null){
            Long id=selected.id;
            m.addAttribute("shopProducts",products.findAll().stream().filter(p->p.shop!=null&&id.equals(p.shop.id)).toList());
            m.addAttribute("shopUsers",orders.findByShopIdOrderByCreatedAtDesc(id).stream().map(o->o.customer).filter(Objects::nonNull).collect(java.util.stream.Collectors.toMap(x->x.id,x->x,(x,y)->x,LinkedHashMap::new)).values());
            m.addAttribute("promotions",promotions.findByShopId(id));
        }
        Long activeShopId=selected==null?null:selected.id;
        m.addAttribute("carriers",carriers.findAll());
        m.addAttribute("blockedCustomers",blocks.findAll().stream().filter(b->b.shop!=null&&activeShopId!=null&&activeShopId.equals(b.shop.id)).map(b->b.customer.id).collect(java.util.stream.Collectors.toSet()));
        m.addAttribute("allManagers",users.findAll().stream().filter(x->"MANAGER".equals(x.role)).toList());
        m.addAttribute("administrator",isAdmin(a));
        return "management-catalog";
    }
    @PostMapping("/manager/shops/{shopId}/products/{id}/edit") String updateProduct(Authentication a,@PathVariable Long shopId,@PathVariable Long id,@RequestParam String name,@RequestParam String category,@RequestParam BigDecimal price,@RequestParam(defaultValue="false") boolean available){
        authorizedShop(a,shopId);
        Product p=products.findById(id).orElseThrow();
        if(p.shop==null||!shopId.equals(p.shop.id))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        validateName(name,255);
        validateName(category,255);
        validatePrice(price);
        p.name=name.trim();
        p.category=category.trim();
        p.price=price;
        p.available=available;
        products.save(p);
        return "redirect:/manager/catalog?shopId="+shopId;
    }
    @PostMapping("/manager/shops/{shopId}/products/new") String newProduct(Authentication a,@PathVariable Long shopId,@RequestParam String name,@RequestParam String category,@RequestParam BigDecimal price){
        Shop shop=authorizedShop(a,shopId);
        validateName(name,255);
        validateName(category,255);
        validatePrice(price);
        products.save(new Product(name.trim(),category.trim(),price,shop));
        return "redirect:/manager/catalog?shopId="+shopId;
    }
    @PostMapping("/manager/shops/{shopId}/categories/rename") @Transactional String renameCategory(Authentication a,@PathVariable Long shopId,@RequestParam String previous,@RequestParam String next){
        authorizedShop(a,shopId);
        validateName(previous,255);
        validateName(next,255);
        var affected=products.findAll().stream().filter(p->p.shop!=null&&shopId.equals(p.shop.id)&&p.category!=null&&p.category.equalsIgnoreCase(previous.trim())).toList();
        if(affected.isEmpty())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Danh mục không có sản phẩm tại chi nhánh");
        affected.forEach(p->p.category=next.trim());
        products.saveAll(affected);
        return "redirect:/manager/catalog?shopId="+shopId;
    }
    @PostMapping("/manager/shops/{shopId}/promotions/{id}/toggle") String togglePromotion(Authentication a,@PathVariable Long shopId,@PathVariable Long id){
        authorizedShop(a,shopId);
        ShopPromotion promo=promotions.findById(id).orElseThrow();
        if(promo.shop==null||!shopId.equals(promo.shop.id))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        promo.active=!promo.active;
        promotions.save(promo);
        return "redirect:/manager/catalog?shopId="+shopId;
    }
    @PostMapping("/manager/shops/{shopId}/promotions/new") String addPromotion(Authentication a,@PathVariable Long shopId,@RequestParam String title,@RequestParam BigDecimal percent,@RequestParam(defaultValue="PRODUCT_PERCENT") String discountType,@RequestParam java.time.LocalDate endDate){
        Shop shop=authorizedShop(a,shopId);
        validateName(title,100);
        if(percent==null||percent.signum()<=0||percent.compareTo(new BigDecimal("70"))>0||endDate==null||endDate.isBefore(java.time.LocalDate.now()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chương trình khuyến mãi không hợp lệ");
        if(!Set.of("PRODUCT_PERCENT","SHIPPING_PERCENT").contains(discountType))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        ShopPromotion promo=new ShopPromotion();
        promo.shop=shop;
        promo.title=title.trim();
        promo.percent=percent;
        promo.discountType=discountType;
        promo.endDate=endDate;
        promotions.save(promo);
        return "redirect:/manager/catalog?shopId="+shopId;
    }
    @PostMapping("/admin/shops/{shopId}/assign-manager") String assignManager(Authentication a,@PathVariable Long shopId,@RequestParam(required=false) String managerEmail){
        admin(a);
        Shop shop=shops.findById(shopId).orElseThrow();
        String email=managerEmail==null?"":managerEmail.trim();
        if(email.isEmpty())shop.managerEmail=null;
        else {
            AppUser user=users.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Không tìm thấy người quản lý"));
            if(!"MANAGER".equals(user.role))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tài khoản phải có vai trò MANAGER");
            shop.managerEmail=user.email;
        }
        shops.save(shop);
        return "redirect:/manager/catalog?shopId="+shopId;
    }
    @PostMapping("/admin/shops/{shopId}/edit") String editShop(Authentication a,@PathVariable Long shopId,@RequestParam String name,@RequestParam String address){
        admin(a);
        validateName(name,255);
        validateName(address,255);
        Shop shop=shops.findById(shopId).orElseThrow();
        shop.name=name.trim();
        shop.address=address.trim();
        shops.save(shop);
        return "redirect:/manager/catalog?shopId="+shopId;
    }
    @PostMapping("/manager/shops/{shopId}/commercial") String commercial(Authentication a,@PathVariable Long shopId,@RequestParam BigDecimal commissionPercent,@RequestParam(required=false) Long carrierId){
        // Manager controls only the shops assigned to them; Admin controls all.
        Shop shop=authorizedShop(a,shopId);
        if(commissionPercent==null||commissionPercent.signum()<0||commissionPercent.compareTo(new BigDecimal("40"))>0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chiết khấu phải từ 0 đến 40%");
        shop.commissionPercent=commissionPercent;
        if(carrierId==null){
            shop.deliveryProvider=null;
        }
        else {
            DeliveryProvider carrier=carriers.findById(carrierId).orElseThrow();
            if(!carrier.active)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Đơn vị vận chuyển đang tắt");
            shop.deliveryProvider=carrier;
        }
        shops.save(shop);
        return "redirect:/manager/catalog?shopId="+shopId;
    }
    @PostMapping("/manager/shops/{shopId}/customers/{userId}/block") String toggleCustomer(Authentication a,@PathVariable Long shopId,@PathVariable Long userId,@RequestParam(required=false,defaultValue="Vi phạm quy định cửa hàng") String reason){
        authorizedShop(a,shopId);
        AppUser user=users.findById(userId).orElseThrow();
        boolean customer=orders.findByShopIdOrderByCreatedAtDesc(shopId).stream().anyMatch(o->o.customer!=null&&userId.equals(o.customer.id));
        if(!customer||!"USER".equals(user.role))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Khách hàng không thuộc chi nhánh");
        var existing=blocks.findByShopIdAndCustomerId(shopId,userId);
        if(existing.isPresent())blocks.delete(existing.get());
        else{
            ShopCustomerBlock block=new ShopCustomerBlock();
            block.shop=shops.findById(shopId).orElseThrow();
            block.customer=user;
            block.reason=reason==null?"":reason.substring(0,Math.min(reason.length(),200));
            blocks.save(block);
        }
        return "redirect:/manager/catalog?shopId="+shopId;
    }
}
