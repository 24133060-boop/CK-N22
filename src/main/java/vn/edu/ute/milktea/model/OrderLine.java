package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="order_lines")
public class OrderLine {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="order_id") public CustomerOrder order;
    @Column(columnDefinition="NVARCHAR(255)") public String productName;
    public Long productId;
    public String size;
    @Column(columnDefinition="NVARCHAR(255)") public String sugar;
    @Column(columnDefinition="NVARCHAR(255)") public String ice;
    @Column(columnDefinition="NVARCHAR(255)") public String topping;
    public int quantity;
    @Column(precision=14,scale=2) public BigDecimal unitPrice;
    @Column(precision=14,scale=2) public BigDecimal subtotal;
    public Long getId(){
        return id;
    }
    public String getProductName(){
        return productName;
    }
    public Long getProductId(){
        return productId;
    }
    public String getSize(){
        return size;
    }
    public String getSugar(){
        return sugar;
    }
    public String getIce(){
        return ice;
    }
    public String getTopping(){
        return topping;
    }
    public int getQuantity(){
        return quantity;
    }
    public BigDecimal getUnitPrice(){
        return unitPrice;
    }
    public BigDecimal getSubtotal(){
        return subtotal;
    }
}
