package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity @Table(name="vouchers",uniqueConstraints=@UniqueConstraint(columnNames="code"))
public class Voucher {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false,length=40,unique=true) public String code;
    @Column(name="discount_percent",nullable=false,precision=5,scale=2) public BigDecimal percent;
    @Column(nullable=false,precision=14,scale=2) public BigDecimal minSubtotal=BigDecimal.ZERO;
    @Column(nullable=false,precision=14,scale=2) public BigDecimal maxDiscount=BigDecimal.ZERO;
    public boolean active=true;
    public LocalDate expiresOn;
    public Long getId(){
        return id;
    }
    public String getCode(){
        return code;
    }
    public BigDecimal getPercent(){
        return percent;
    }
    public BigDecimal getMinSubtotal(){
        return minSubtotal;
    }
    public BigDecimal getMaxDiscount(){
        return maxDiscount;
    }
    public boolean getActive(){
        return active;
    }
    public LocalDate getExpiresOn(){
        return expiresOn;
    }
}
