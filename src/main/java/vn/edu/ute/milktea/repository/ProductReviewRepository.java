package vn.edu.ute.milktea.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.ProductReview;
import java.util.*;
public interface ProductReviewRepository extends JpaRepository<ProductReview,Long> {
    List<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId);
    Optional<ProductReview> findByUserEmailAndProductId(String email,Long productId);
}
