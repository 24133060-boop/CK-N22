package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="registration_otps") public class RegistrationOtp {
    @Id @Column(length=120) public String email;
    @Column(nullable=false) public String passwordHash;
    @Column(nullable=false,columnDefinition="NVARCHAR(255)") public String displayName;
    @Column(nullable=false) public String otpHash;
    public LocalDateTime expiresAt;
    public LocalDateTime lastSentAt;
    public int attempts;
}
