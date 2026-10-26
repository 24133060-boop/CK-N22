package vn.edu.ute.milktea.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ute.milktea.model.PlatformSetting;
import java.util.*;
public interface PlatformSettingRepository extends JpaRepository<PlatformSetting,Long>{
    Optional<PlatformSetting> findBySettingKey(String key);
}
