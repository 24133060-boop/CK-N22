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
import vn.edu.ute.milktea.model.*;
import vn.edu.ute.milktea.repository.*;
@Service public class RegistrationService {
    private final RegistrationOtpRepository pending;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JavaMailSender mail;
    private final SecureRandom random=new SecureRandom();
    @Value("${app.mail.from:}") private String from;
    public RegistrationService(RegistrationOtpRepository p,UserRepository u,PasswordEncoder e,JavaMailSender m){
        pending=p;
        users=u;
        encoder=e;
        mail=m;
    }
    public void start(String rawEmail,String password,String name){
        String email=rawEmail==null?"":rawEmail.trim().toLowerCase(Locale.ROOT);
        if(!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")||email.length()>120)throw new IllegalArgumentException("Email không hợp lệ");
        if(password==null||password.length()<8||password.length()>72)throw new IllegalArgumentException("Mật khẩu phải có 8-72 ký tự");
        if(name==null||name.isBlank()||name.trim().length()>100)throw new IllegalArgumentException("Họ tên không hợp lệ");
        if(users.findByEmail(email).isPresent())throw new IllegalArgumentException("Email đã được đăng ký");
        var now=LocalDateTime.now();
        var previous=pending.findById(email);
        if(previous.isPresent()&&previous.get().lastSentAt!=null&&previous.get().lastSentAt.plusMinutes(2).isAfter(now))throw new IllegalArgumentException("Vui lòng đợi 2 phút trước khi gửi lại OTP");
        String code=String.format("%06d",random.nextInt(1000000));
        var msg=new SimpleMailMessage();
        msg.setTo(email);
        if(from!=null&&!from.isBlank())msg.setFrom(from);
        msg.setSubject("UTEMILKTEA - Xac minh dang ky");
        msg.setText("Ma OTP xac minh email: "+code+". Het han sau 10 phut. Khong chia se ma nay.");
        mail.send(msg);
        var record=new RegistrationOtp();
        record.email=email;
        record.displayName=name.trim();
        record.passwordHash=encoder.encode(password);
        record.otpHash=encoder.encode(code);
        record.expiresAt=now.plusMinutes(10);
        record.lastSentAt=now;
        record.attempts=0;
        pending.save(record);
    }
    @Transactional public void verify(String rawEmail,String code){
        String email=rawEmail==null?"":rawEmail.trim().toLowerCase(Locale.ROOT);
        var record=pending.findById(email).orElseThrow(()->new IllegalArgumentException("Không tìm thấy yêu cầu xác minh"));
        if(record.expiresAt==null||!record.expiresAt.isAfter(LocalDateTime.now())||record.attempts>=5){
            pending.delete(record);
            throw new IllegalArgumentException("OTP hết hạn hoặc vượt số lần thử");
        }
        record.attempts++;
        pending.saveAndFlush(record);
        if(code==null||!code.matches("\\d{6}")||!encoder.matches(code,record.otpHash))throw new IllegalArgumentException("Mã OTP không đúng");
        if(users.findByEmail(email).isPresent())throw new IllegalArgumentException("Email đã được đăng ký");
        users.save(new AppUser(email,record.passwordHash,record.displayName,"USER"));
        pending.delete(record);
    }
}
