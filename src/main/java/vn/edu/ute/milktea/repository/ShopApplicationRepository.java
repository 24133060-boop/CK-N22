package vn.edu.ute.milktea.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.ShopApplication;
import java.util.*;
public interface ShopApplicationRepository extends JpaRepository<ShopApplication,Long>{
    List<ShopApplication> findByApplicantEmailOrderByCreatedAtDesc(String email);
}
