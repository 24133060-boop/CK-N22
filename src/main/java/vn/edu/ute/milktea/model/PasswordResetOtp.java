package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="password_reset_otps") public class PasswordResetOtp {
    @Id @Column(length=120) public String email;
    @Column(nullable=false,length=100) public String otpHash;
    public LocalDateTime expiresAt;
    public LocalDateTime lastSentAt;
    public int attempts;
    public PasswordResetOtp(){
    }
}
