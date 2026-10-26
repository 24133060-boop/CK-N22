package vn.edu.ute.milktea.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.ShopCustomerBlock;
public interface ShopCustomerBlockRepository extends JpaRepository<ShopCustomerBlock,Long> {
    Optional<ShopCustomerBlock> findByShopIdAndCustomerId(Long shopId,Long customerId);
}
