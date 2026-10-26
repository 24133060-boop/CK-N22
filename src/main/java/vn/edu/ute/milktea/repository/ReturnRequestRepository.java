package vn.edu.ute.milktea.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.ReturnRequest;
import java.util.*;
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest,Long>{
    List<ReturnRequest> findByCustomerEmailOrderByCreatedAtDesc(String email);
    List<ReturnRequest> findByOrderShopIdOrderByCreatedAtDesc(Long shopId);
    Optional<ReturnRequest> findByOrderId(Long orderId);
}
