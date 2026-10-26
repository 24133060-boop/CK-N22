package vn.edu.ute.milktea.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ute.milktea.service.RegistrationService;
@Controller public class RegistrationController {
    private final RegistrationService service;
    public RegistrationController(RegistrationService s){
        service=s;
    }
    @PostMapping("/register") String register(@RequestParam String email,@RequestParam String password,@RequestParam String name,RedirectAttributes ra){
        try{
            service.start(email,password,name);
            ra.addFlashAttribute("success","Đã gửi OTP đến email. Vui lòng kiểm tra hộp thư.");
            ra.addFlashAttribute("email",email);
            return "redirect:/verify-registration";
        }
        catch(IllegalArgumentException ex){
            ra.addFlashAttribute("error",ex.getMessage());
            return "redirect:/register";
        }
        catch(Exception ex){
            ra.addFlashAttribute("error","Không thể gửi OTP. Kiểm tra cấu hình SMTP.");
            return "redirect:/register";
        }
    }
    @GetMapping("/verify-registration") String verifyPage(){
        return "verify-registration";
    }
    @PostMapping("/verify-registration") String verify(@RequestParam String email,@RequestParam String otp,RedirectAttributes ra){
        try{
            service.verify(email,otp);
            ra.addFlashAttribute("success","Xác minh thành công. Hãy đăng nhập.");
            return "redirect:/login";
        }
        catch(IllegalArgumentException ex){
            ra.addFlashAttribute("error",ex.getMessage());
            ra.addFlashAttribute("email",email);
            return "redirect:/verify-registration";
        }
    }
}
