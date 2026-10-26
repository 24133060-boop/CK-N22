package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.time.*;
import java.math.*;
@Entity public class DeliveryProvider {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(columnDefinition="NVARCHAR(150)") public String name;
    public BigDecimal baseFee=BigDecimal.ZERO;
    public BigDecimal perKmFee=BigDecimal.ZERO;
    public boolean active=true;
    public Long getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public BigDecimal getBaseFee(){
        return baseFee;
    }
    public BigDecimal getPerKmFee(){
        return perKmFee;
    }
    public boolean getActive(){
        return active;
    }
}
