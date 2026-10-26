package vn.edu.ute.milktea.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.Favorite;
import java.util.*;
public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    List<Favorite> findByUserEmailOrderByIdDesc(String email);
    Optional<Favorite> findByUserEmailAndProductId(String email,Long productId);
}
