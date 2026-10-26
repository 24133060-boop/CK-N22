package vn.edu.ute.milktea.controller;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ute.milktea.repository.OrderRepository;
import vn.edu.ute.milktea.service.VietqrService;
import vn.edu.ute.milktea.service.RealtimeNotificationService;
import java.time.LocalDateTime;
@Controller public class VietqrController {
    private final OrderRepository orders;
    private final vn.edu.ute.milktea.repository.UserRepository users;
    private final VietqrService qr;
    private final RealtimeNotificationService realtime;
    public VietqrController(OrderRepository orders,VietqrService qr,RealtimeNotificationService realtime,vn.edu.ute.milktea.repository.UserRepository users){
        this.orders=orders;
        this.users=users;
        this.qr=qr;
        this.realtime=realtime;
    }
    @GetMapping("/payment/vietqr/{id}")
    public String show(@PathVariable Long id,Authentication auth,Model model){
        var o=orders.findById(id).orElseThrow();
        if(auth==null||!o.customer.email.equalsIgnoreCase(auth.getName()))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if(!"VIETQR".equals(o.payment))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Đơn không chọn VietQR");
        model.addAttribute("order",o);
        model.addAttribute("configured",qr.configured());
        if(qr.configured()&&"UNPAID".equals(o.paymentStatus)&&!"CANCELLED".equals(o.status))model.addAttribute("qrUrl",qr.qrUrl(o.id,o.total));
        model.addAttribute("bank",qr.bank());
        model.addAttribute("account",qr.account());
        model.addAttribute("accountName",qr.accountName());
        model.addAttribute("transferContent",qr.transferContent(o.id));
        return "vietqr";
    }
    @PostMapping("/vendor/orders/{id}/confirm-vietqr") @Transactional
    public String confirmVendor(@PathVariable Long id,Authentication auth,RedirectAttributes flash){
        var o=orders.findById(id).orElseThrow();
        if(auth==null||!auth.getName().equalsIgnoreCase(o.shop.vendorEmail))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        confirm(o);
        flash.addFlashAttribute("success","Đã xác nhận nhận tiền VietQR cho đơn #"+id);
        return "redirect:/vendor/orders/"+o.shop.id;
    }
    @PostMapping("/manager/orders/{id}/confirm-vietqr") @Transactional
    public String confirmManager(@PathVariable Long id,Authentication auth,RedirectAttributes flash){
        var o=orders.findById(id).orElseThrow();
        var user=users.findByEmail(auth.getName()).orElseThrow();
        if(!"ADMIN".equals(user.role)&&!("MANAGER".equals(user.role)&&o.shop.managerEmail!=null&&o.shop.managerEmail.equalsIgnoreCase(user.email)))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Không được xác nhận tiền của chi nhánh khác");
        confirm(o);
        flash.addFlashAttribute("success","Đã xác nhận nhận tiền VietQR cho đơn #"+id);
        return "redirect:/manager";
    }
    private void confirm(vn.edu.ute.milktea.model.CustomerOrder o){
        if(!"VIETQR".equals(o.payment)||!"UNPAID".equals(o.paymentStatus)||"CANCELLED".equals(o.status))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Đơn không thể xác nhận thanh toán");
        o.paymentStatus="PAID";
        o.paidAt=LocalDateTime.now();
        o.paymentTransactionRef="MANUAL-"+o.id;
        orders.save(o);
        realtime.orderChanged();
    }
}
