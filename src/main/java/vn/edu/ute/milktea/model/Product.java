package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(columnDefinition="NVARCHAR(255)") public String name;
    @Column(columnDefinition="NVARCHAR(255)") public String category;
    public BigDecimal price;
    public String imageUrl;
    public boolean available=true;
    @ManyToOne(optional=false) public Shop shop;
    public Product(){
    }
    public Product(String n,String c,BigDecimal p,Shop s){
        name=n;
        category=c;
        price=p;
        shop=s;
    }
    // JavaBeans accessors for JSP Expression Language
    public Long getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getCategory() {
        return this.category;
    }
    public BigDecimal getPrice() {
        return this.price;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public boolean getAvailable() {
        return this.available;
    }
    public Shop getShop() {
        return this.shop;
    }
}
