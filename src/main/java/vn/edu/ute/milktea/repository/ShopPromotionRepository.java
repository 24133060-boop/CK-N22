package vn.edu.ute.milktea.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.ShopPromotion;
import java.util.*;
public interface ShopPromotionRepository extends JpaRepository<ShopPromotion,Long>{
    List<ShopPromotion> findByShopId(Long shopId);
}
