package vn.edu.ute.milktea.repository;
import vn.edu.ute.milktea.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product,Long>{
    List<Product> findByShopIdAndAvailableTrue(Long shopId);
    List<Product> findByNameContainingIgnoreCaseAndAvailableTrue(String keyword);
}
