package vn.edu.ute.milktea.repository;
import vn.edu.ute.milktea.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrderRepository extends JpaRepository<CustomerOrder,Long>{
    List<CustomerOrder> findByCustomerEmailOrderByCreatedAtDesc(String email);
    List<CustomerOrder> findByShopIdOrderByCreatedAtDesc(Long shopId);
    List<CustomerOrder> findByShipperEmailOrderByCreatedAtDesc(String email);
}
