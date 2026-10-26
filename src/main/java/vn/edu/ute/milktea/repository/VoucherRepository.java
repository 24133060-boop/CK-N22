package vn.edu.ute.milktea.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.Voucher;
public interface VoucherRepository extends JpaRepository<Voucher,Long> {
    Optional<Voucher> findByCodeIgnoreCase(String code);
}
