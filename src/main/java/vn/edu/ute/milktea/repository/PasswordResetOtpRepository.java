package vn.edu.ute.milktea.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.PasswordResetOtp;
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp,String>{
}
