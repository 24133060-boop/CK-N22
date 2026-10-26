package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.time.*;
import java.math.*;
@Entity public class RecentlyViewed {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) public AppUser user;
    @ManyToOne(optional=false) public Product product;
    public LocalDateTime viewedAt=LocalDateTime.now();
    public Long getId(){
        return id;
    }
    public Product getProduct(){
        return product;
    }
    public LocalDateTime getViewedAt(){
        return viewedAt;
    }
}
