package vn.edu.ute.milktea.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.RecentlyViewed;
import java.util.*;
public interface RecentlyViewedRepository extends JpaRepository<RecentlyViewed,Long>{
    List<RecentlyViewed> findTop20ByUserEmailOrderByViewedAtDesc(String email);
    Optional<RecentlyViewed> findByUserEmailAndProductId(String email,Long productId);
}
