package vn.edu.ute.milktea.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.DeliveryAddress;
import java.util.*;
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress,Long>{
    List<DeliveryAddress> findByUserEmailOrderByIdDesc(String email);
}
