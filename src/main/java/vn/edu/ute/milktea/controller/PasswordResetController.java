package vn.edu.ute.milktea.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ute.milktea.service.PasswordResetService;
@Controller public class PasswordResetController {
    private final PasswordResetService service;
    public PasswordResetController(PasswordResetService s){
        service=s;
    }
    @GetMapping("/forgot-password") String page(){
        return "forgot-password";
    }
    @PostMapping("/forgot-password") String send(@RequestParam String email,RedirectAttributes ra){
        try{
            service.send(email);
            ra.addFlashAttribute("success","Nếu email tồn tại, mã OTP sẽ được gửi tới hộp thư.");
        }
        catch(Exception ex){
            ra.addFlashAttribute("error","Không gửi được email. Hãy kiểm tra cấu hình SMTP.");
        }
        return "redirect:/forgot-password";
    }
    @PostMapping("/reset-password") String reset(@RequestParam String email,@RequestParam String otp,@RequestParam String password,RedirectAttributes ra){
        try{
            service.reset(email,otp,password);
            ra.addFlashAttribute("success","Đổi mật khẩu thành công. Hãy đăng nhập.");
            return "redirect:/login";
        }
        catch(IllegalArgumentException ex){
            ra.addFlashAttribute("error",ex.getMessage());
            return "redirect:/forgot-password";
        }
    }
}
