package vn.edu.ute.milktea.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ute.milktea.model.*;
import vn.edu.ute.milktea.repository.*;
import java.math.*;
import java.time.*;
import java.util.*;
@Controller
public class ExtendedFeaturesController {
    private final UserRepository users;
    private final ProductRepository products;
    private final ShopRepository shops;
    private final OrderRepository orders;
    private final DeliveryAddressRepository addresses;
    private final RecentlyViewedRepository history;
    private final ReturnRequestRepository returns;
    private final ShopApplicationRepository applications;
    private final ShopPromotionRepository promotions;
    private final DeliveryProviderRepository carriers;
    private final PlatformSettingRepository settings;
    public ExtendedFeaturesController(UserRepository u,ProductRepository p,ShopRepository s,OrderRepository o,DeliveryAddressRepository a,RecentlyViewedRepository h,ReturnRequestRepository r,ShopApplicationRepository ap,ShopPromotionRepository pr,DeliveryProviderRepository c,PlatformSettingRepository st){
        users=u;
        products=p;
        shops=s;
        orders=o;
        addresses=a;
        history=h;
        returns=r;
        applications=ap;
        promotions=pr;
        carriers=c;
        settings=st;
    }
    private AppUser current(Authentication a){
        return users.findByEmail(a.getName()).orElseThrow();
    }
    private void requireVendor(Authentication a,Shop shop){
        if(shop.vendorEmail==null||!shop.vendorEmail.equalsIgnoreCase(a.getName()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    private void requireAdmin(Authentication a){
        if(!"ADMIN".equals(current(a).role))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    @GetMapping("/profile/addresses") String addresses(Authentication a,Model m){
        m.addAttribute("addresses",addresses.findByUserEmailOrderByIdDesc(a.getName()));
        return "addresses";
    }
    @PostMapping("/profile/addresses") String addAddress(Authentication a,@RequestParam String recipient,@RequestParam String phone,@RequestParam String address,RedirectAttributes ra){
        if(recipient==null||recipient.isBlank()||recipient.length()>100||phone==null||!phone.matches("0[0-9]{9,10}")||address==null||address.isBlank()||address.length()>500)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Thông tin địa chỉ không hợp lệ");
        var entry=new DeliveryAddress();
        entry.user=current(a);
        entry.recipient=recipient.trim();
        entry.phone=phone.trim();
        entry.address=address.trim();
        entry.primaryAddress=addresses.findByUserEmailOrderByIdDesc(a.getName()).isEmpty();
        addresses.save(entry);
        ra.addFlashAttribute("success","Đã lưu địa chỉ");
        return "redirect:/profile/addresses";
    }
    @PostMapping("/profile/addresses/{id}/default") String primary(@PathVariable Long id,Authentication a){
        var address=addresses.findById(id).orElseThrow();
        if(!address.user.email.equalsIgnoreCase(a.getName()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        var all=addresses.findByUserEmailOrderByIdDesc(a.getName());
        for(var item:all){
            item.primaryAddress=item.id.equals(id);
        }
        addresses.saveAll(all);
        return "redirect:/profile/addresses";
    }
    @PostMapping("/profile/addresses/{id}/delete") String delete(@PathVariable Long id,Authentication a){
        var entry=addresses.findById(id).orElseThrow();
        if(!entry.user.email.equalsIgnoreCase(a.getName()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        addresses.delete(entry);
        var all=addresses.findByUserEmailOrderByIdDesc(a.getName());
        if(entry.primaryAddress&&!all.isEmpty()){
            all.get(0).primaryAddress=true;
            addresses.save(all.get(0));
        }
        return "redirect:/profile/addresses";
    }
    @GetMapping("/recently-viewed") String recent(Authentication a,Model m){
        m.addAttribute("history",history.findTop20ByUserEmailOrderByViewedAtDesc(a.getName()));
        return "recently-viewed";
    }
    @PostMapping("/my-orders/{id}/return") String requestReturn(@PathVariable Long id,@RequestParam String reason,Authentication a,RedirectAttributes ra){
        var order=orders.findById(id).orElseThrow();
        if(!order.customer.email.equalsIgnoreCase(a.getName()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if(!"DELIVERED".equals(order.status))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chỉ yêu cầu trả đơn đã giao");
        if(order.deliveredAt==null||order.deliveredAt.isBefore(LocalDateTime.now().minusDays(7)))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chỉ yêu cầu trả trong 7 ngày từ khi giao");
        if(reason==null||reason.trim().length()<10||reason.trim().length()>500)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Lý do từ 10 đến 500 ký tự");
        if(returns.findByOrderId(id).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Đã gửi yêu cầu trước đó");
        var request=new ReturnRequest();
        request.order=order;
        request.customer=current(a);
        request.reason=reason.trim();
        returns.save(request);
        ra.addFlashAttribute("success","Đã gửi yêu cầu trả hàng, chờ cửa hàng xử lý");
        return "redirect:/my-returns";
    }
    @GetMapping("/my-returns") String myReturns(Authentication a,Model m){
        m.addAttribute("returns",returns.findByCustomerEmailOrderByCreatedAtDesc(a.getName()));
        return "my-returns";
    }
    @GetMapping("/vendor/shop/{id}/returns") String shopReturns(@PathVariable Long id,Authentication a,Model m){
        var shop=shops.findById(id).orElseThrow();
        requireVendor(a,shop);
        m.addAttribute("shop",shop);
        m.addAttribute("returns",returns.findByOrderShopIdOrderByCreatedAtDesc(id));
        return "shop-returns";
    }
    @PostMapping("/vendor/returns/{id}/decision") String returnDecision(@PathVariable Long id,@RequestParam String decision,@RequestParam String response,Authentication a){
        var r=returns.findById(id).orElseThrow();
        requireVendor(a,r.order.shop);
        if(!"REQUESTED".equals(r.status)||!Set.of("APPROVED","REJECTED").contains(decision))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if(response==null||response.isBlank()||response.length()>500)throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        r.status=decision;
        r.response=response.trim();
        returns.save(r);
        return "redirect:/vendor/shop/"+r.order.shop.id+"/returns";
    }
    @GetMapping("/vendor/apply") String apply(Authentication a,Model m){
        m.addAttribute("applications",applications.findByApplicantEmailOrderByCreatedAtDesc(a.getName()));
        return "apply-shop";
    }
    @PostMapping("/vendor/apply") String applyPost(@RequestParam String name,@RequestParam String address,Authentication a){
        if(!"USER".equals(current(a).role))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chỉ tài khoản khách hàng có thể đăng ký");
        if(name==null||name.isBlank()||name.length()>255||address==null||address.isBlank()||address.length()>500)throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if(applications.findByApplicantEmailOrderByCreatedAtDesc(a.getName()).stream().anyMatch(x->"PENDING".equals(x.status)))throw new ResponseStatusException(HttpStatus.CONFLICT,"Đang có yêu cầu chờ xét duyệt");
        var ap=new ShopApplication();
        ap.applicant=current(a);
        ap.shopName=name.trim();
        ap.address=address.trim();
        applications.save(ap);
        return "redirect:/vendor/apply";
    }
    @GetMapping("/admin/applications") String applications(Authentication a,Model m){
        requireAdmin(a);
        m.addAttribute("applications",applications.findAll());
        return "applications";
    }
    @PostMapping("/admin/applications/{id}/decision") String approveApplication(@PathVariable Long id,@RequestParam String decision,Authentication a){
        requireAdmin(a);
        var ap=applications.findById(id).orElseThrow();
        if(!"PENDING".equals(ap.status)||!Set.of("APPROVED","REJECTED").contains(decision))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if("APPROVED".equals(decision)){
            var sh=new Shop();
            sh.name=ap.shopName;
            sh.address=ap.address;
            sh.vendorEmail=ap.applicant.email;
            shops.save(sh);
            ap.applicant.role="VENDOR";
            users.save(ap.applicant);
        }
        ap.status=decision;
        applications.save(ap);
        return "redirect:/admin/applications";
    }
    @GetMapping("/vendor/shop/{id}/promotions") String promotions(@PathVariable Long id,Authentication a,Model m){
        var sh=shops.findById(id).orElseThrow();
        requireVendor(a,sh);
        m.addAttribute("shop",sh);
        m.addAttribute("promotions",promotions.findByShopId(id));
        return "shop-promotions";
    }
    @PostMapping("/vendor/shop/{id}/promotions") String promotionCreate(@PathVariable Long id,@RequestParam String title,@RequestParam BigDecimal percent,@RequestParam(defaultValue="PRODUCT_PERCENT") String discountType,@RequestParam LocalDate endDate,Authentication a){
        var sh=shops.findById(id).orElseThrow();
        requireVendor(a,sh);
        if(title==null||title.isBlank()||title.length()>100||percent==null||percent.signum()<=0||percent.compareTo(new BigDecimal("70"))>0||endDate==null||endDate.isBefore(LocalDate.now()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if(!Set.of("PRODUCT_PERCENT","SHIPPING_PERCENT").contains(discountType))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var promotion=new ShopPromotion();
        promotion.shop=sh;
        promotion.title=title.trim();
        promotion.percent=percent;
        promotion.discountType=discountType;
        promotion.endDate=endDate;
        promotions.save(promotion);
        return "redirect:/vendor/shop/"+id+"/promotions";
    }
    @PostMapping("/vendor/shop/{id}/promotions/{promotionId}/toggle") String promotionToggle(@PathVariable Long id,@PathVariable Long promotionId,Authentication a){
        var sh=shops.findById(id).orElseThrow();
        requireVendor(a,sh);
        var pr=promotions.findById(promotionId).orElseThrow();
        if(!pr.shop.id.equals(id))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        pr.active=!pr.active;
        promotions.save(pr);
        return "redirect:/vendor/shop/"+id+"/promotions";
    }
    @GetMapping("/admin/settings") String settingPage(Authentication a,Model m){
        requireAdmin(a);
        m.addAttribute("providers",carriers.findAll());
        m.addAttribute("commission",settings.findBySettingKey("COMMISSION_PERCENT").map(x->x.numericValue).orElse(BigDecimal.ZERO));
        return "platform-settings";
    }
    @PostMapping("/admin/settings/commission") String commission(@RequestParam BigDecimal percent,Authentication a){
        requireAdmin(a);
        if(percent==null||percent.signum()<0||percent.compareTo(new BigDecimal("40"))>0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var s=settings.findBySettingKey("COMMISSION_PERCENT").orElseGet(PlatformSetting::new);
        s.settingKey="COMMISSION_PERCENT";
        s.numericValue=percent;
        settings.save(s);
        return "redirect:/admin/settings";
    }
    @PostMapping("/admin/settings/carriers") String carrierAdd(@RequestParam String name,@RequestParam BigDecimal baseFee,@RequestParam BigDecimal perKmFee,Authentication a){
        requireAdmin(a);
        if(name==null||name.isBlank()||name.length()>150||baseFee==null||baseFee.signum()<0||perKmFee==null||perKmFee.signum()<0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var c=new DeliveryProvider();
        c.name=name.trim();
        c.baseFee=baseFee;
        c.perKmFee=perKmFee;
        carriers.save(c);
        return "redirect:/admin/settings";
    }
    @PostMapping("/admin/settings/carriers/{id}/toggle") String carrierToggle(@PathVariable Long id,Authentication a){
        requireAdmin(a);
        var carrier=carriers.findById(id).orElseThrow();
        carrier.active=!carrier.active;
        carriers.save(carrier);
        return "redirect:/admin/settings";
    }
    @PostMapping("/vendor/shop/{id}/products") String addProduct(@PathVariable Long id,@RequestParam String name,@RequestParam String category,@RequestParam BigDecimal price,Authentication a){
        var shop=shops.findById(id).orElseThrow();
        requireVendor(a,shop);
        if(name==null||name.isBlank()||name.length()>255||category==null||category.isBlank()||category.length()>255||price==null||price.signum()<=0||price.compareTo(new BigDecimal("10000000"))>0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var product=new Product(name.trim(),category.trim(),price,shop);
        products.save(product);
        return "redirect:/vendor/orders/"+id;
    }
    @PostMapping("/admin/shops/{id}/toggle") String toggleShop(@PathVariable Long id,Authentication a){
        requireAdmin(a);
        var shop=shops.findById(id).orElseThrow();
        shop.active=!shop.active;
        shops.save(shop);
        return "redirect:/admin";
    }
}
