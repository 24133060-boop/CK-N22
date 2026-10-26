package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
@Entity @Table(name="shop_customer_blocks",uniqueConstraints=@UniqueConstraint(columnNames={
    "shop_id","customer_id"}
))
public class ShopCustomerBlock {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) public Shop shop;
    @ManyToOne(optional=false) public AppUser customer;
    public String reason;
    public ShopCustomerBlock(){
    }
    public Long getId(){
        return id;
    }
    public Shop getShop(){
        return shop;
    }
    public AppUser getCustomer(){
        return customer;
    }
    public String getReason(){
        return reason;
    }
}
