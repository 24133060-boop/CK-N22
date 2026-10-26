package vn.edu.ute.milktea.service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ute.milktea.model.PasswordResetOtp;
import vn.edu.ute.milktea.repository.*;
@Service public class PasswordResetService {
    private final PasswordResetOtpRepository otps;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JavaMailSender mail;
    private final SecureRandom random=new SecureRandom();
    @Value("${app.mail.from:}") private String sender;
    public PasswordResetService(PasswordResetOtpRepository otps,UserRepository users,PasswordEncoder encoder,JavaMailSender mail){
        this.otps=otps;
        this.users=users;
        this.encoder=encoder;
        this.mail=mail;
    }
    @Transactional public void send(String rawEmail){
        String email=rawEmail.trim().toLowerCase(Locale.ROOT);
        if(users.findByEmail(email).isEmpty())return;
        var old=otps.findById(email);
        var now=LocalDateTime.now();
        if(old.isPresent()&&old.get().lastSentAt!=null&&old.get().lastSentAt.plusMinutes(2).isAfter(now))return;
        String code=String.format("%06d",random.nextInt(1000000));
        var otp=new PasswordResetOtp();
        otp.email=email;
        otp.otpHash=encoder.encode(code);
        otp.expiresAt=now.plusMinutes(10);
        otp.lastSentAt=now;
        otp.attempts=0;
        var message=new SimpleMailMessage();
        message.setTo(email);
        if(sender!=null&&!sender.isBlank())message.setFrom(sender);
        message.setSubject("UTEMILKTEA - Ma khoi phuc mat khau");
        message.setText("Ma OTP cua ban: "+code+". Co hieu luc 10 phut. Khong chia se ma nay.");
        mail.send(message);
        otps.save(otp);
    }
    public void reset(String rawEmail,String code,String newPassword){
        String email=rawEmail.trim().toLowerCase(Locale.ROOT);
        if(newPassword==null||newPassword.length()<8||newPassword.length()>72)throw new IllegalArgumentException("Mật khẩu phải dài từ 8 đến 72 ký tự");
        var otp=otps.findById(email).orElseThrow(()->new IllegalArgumentException("OTP không hợp lệ hoặc đã hết hạn"));
        if(otp.expiresAt.isBefore(LocalDateTime.now())||otp.attempts>=5){
            otps.delete(otp);
            throw new IllegalArgumentException("OTP đã hết hạn hoặc vượt số lần thử");
        }
        otp.attempts++;
        otps.saveAndFlush(otp);
        if(code==null||!encoder.matches(code,otp.otpHash))throw new IllegalArgumentException("OTP không hợp lệ");
        var user=users.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Tài khoản không tồn tại"));
        user.password=encoder.encode(newPassword);
        users.save(user);
        otps.delete(otp);
    }
}
