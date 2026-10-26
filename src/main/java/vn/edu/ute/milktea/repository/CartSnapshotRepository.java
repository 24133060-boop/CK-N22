package vn.edu.ute.milktea.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.CartSnapshot;
public interface CartSnapshotRepository extends JpaRepository<CartSnapshot,String>{
}
