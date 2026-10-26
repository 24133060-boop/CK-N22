package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.time.*;
import java.math.*;
@Entity public class PlatformSetting {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(unique=true,length=60) public String settingKey;
    public BigDecimal numericValue;
    public Long getId(){
        return id;
    }
    public String getSettingKey(){
        return settingKey;
    }
    public BigDecimal getNumericValue(){
        return numericValue;
    }
}
