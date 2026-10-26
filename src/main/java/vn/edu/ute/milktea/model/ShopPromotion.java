package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.time.*;
import java.math.*;
@Entity public class ShopPromotion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) public Shop shop;
    @Column(columnDefinition="NVARCHAR(100)") public String title;
    @Column(name="`percent`") public BigDecimal percent;
    @Column(length=25) public String discountType="PRODUCT_PERCENT";
    public LocalDate endDate;
    public boolean active=true;
    public Long getId(){
        return id;
    }
    public Shop getShop(){
        return shop;
    }
    public String getTitle(){
        return title;
    }
    public BigDecimal getPercent(){
        return percent;
    }
    public String getDiscountType(){
        return discountType;
    }
    public LocalDate getEndDate(){
        return endDate;
    }
    public boolean getActive(){
        return active;
    }
}
