package vn.edu.ute.milktea.controller;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ute.milktea.repository.OrderRepository;
import vn.edu.ute.milktea.service.VnpayService;
import java.time.LocalDateTime;
import java.util.Map;
@Controller
public class VnpayController {
    private final VnpayService pay;
    private final OrderRepository orders;
    public VnpayController(VnpayService pay,OrderRepository orders){
        this.pay=pay;
        this.orders=orders;
    }
    @PostMapping("/payment/vnpay/{id}")
    public String start(@PathVariable Long id,Authentication auth,HttpServletRequest request,RedirectAttributes flash){
        var o=orders.findById(id).orElseThrow();
        if(auth==null||!o.customer.email.equalsIgnoreCase(auth.getName()))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN);
        if("CANCELLED".equals(o.status)||"PAID".equals(o.paymentStatus)){
            flash.addFlashAttribute("error","Đơn hàng không thể thanh toán.");
            return "redirect:/my-orders/"+id+"/detail";
        }
        try{
            String url=pay.createUrl(o.id,o.total,request.getRemoteAddr());
            o.paymentTransactionRef=java.net.URLDecoder.decode(url.split("vnp_TxnRef=")[1].split("&")[0],java.nio.charset.StandardCharsets.UTF_8);
            orders.save(o);
            return "redirect:"+url;
        }
        catch(Exception ex){
            flash.addFlashAttribute("error",ex.getMessage());
            return "redirect:/my-orders/"+id+"/detail";
        }
    }
    // Browser redirect is only informational; never trust it to mark payment successful.
    @GetMapping("/payment/vnpay/return")
    public String result(@RequestParam Map<String,String> params,Model model){
        boolean verified=pay.verify(params);
        model.addAttribute("verified",verified);
        model.addAttribute("success",verified&&"00".equals(params.get("vnp_ResponseCode")));
        model.addAttribute("transaction",params.getOrDefault("vnp_TxnRef",""));
        return "payment-result";
    }
    @GetMapping("/payment/vnpay/ipn")
    @ResponseBody @Transactional
    public ResponseEntity<Map<String,String>> ipn(@RequestParam Map<String,String> params){
        if(!pay.verify(params))return ResponseEntity.ok(Map.of("RspCode","97","Message","Invalid signature"));
        try{
            long id=pay.orderId(params.get("vnp_TxnRef"));
            var opt=orders.findById(id);
            if(opt.isEmpty())return ResponseEntity.ok(Map.of("RspCode","01","Message","Order not found"));
            var o=opt.get();
            if(o.paymentTransactionRef==null||!o.paymentTransactionRef.equals(params.get("vnp_TxnRef")))return ResponseEntity.ok(Map.of("RspCode","02","Message","Unknown transaction"));
            if(!pay.matchesAmount(o.total,params.get("vnp_Amount")))return ResponseEntity.ok(Map.of("RspCode","04","Message","Invalid amount"));
            if("PAID".equals(o.paymentStatus))return ResponseEntity.ok(Map.of("RspCode","02","Message","Already updated"));
            if("CANCELLED".equals(o.status))return ResponseEntity.ok(Map.of("RspCode","02","Message","Cancelled order"));
            if("00".equals(params.get("vnp_ResponseCode"))&&"00".equals(params.get("vnp_TransactionStatus"))){
                o.payment="VNPAY";
                o.paymentStatus="PAID";
                o.paymentTransactionRef=params.get("vnp_TxnRef");
                o.paidAt=LocalDateTime.now();
                orders.save(o);
            }
            return ResponseEntity.ok(Map.of("RspCode","00","Message","Confirm success"));
        }
        catch(Exception ex){
            return ResponseEntity.ok(Map.of("RspCode","99","Message","Invalid request"));
        }
    }
}
