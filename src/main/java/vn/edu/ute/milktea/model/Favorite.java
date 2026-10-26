package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
@Entity @Table(name="favorites",uniqueConstraints=@UniqueConstraint(columnNames={
    "user_id","product_id"}
))
public class Favorite {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) @JoinColumn(name="user_id") public AppUser user;
    @ManyToOne(optional=false) @JoinColumn(name="product_id") public Product product;
    public Long getId(){
        return id;
    }
    public Product getProduct(){
        return product;
    }
}
