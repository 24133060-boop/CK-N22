package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="product_reviews",uniqueConstraints=@UniqueConstraint(columnNames={
    "user_id","product_id"}
))
public class ProductReview {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) @JoinColumn(name="user_id") public AppUser user;
    @ManyToOne(optional=false) @JoinColumn(name="product_id") public Product product;
    public int rating;
    @Column(columnDefinition="NVARCHAR(500)") public String comment;
    @Column(length=600) public String mediaUrl;
    @Column(length=15) public String mediaType;
    public String getMediaUrl(){
        return mediaUrl;
    }
    public String getMediaType(){
        return mediaType;
    }
    public LocalDateTime createdAt=LocalDateTime.now();
    public Long getId(){
        return id;
    }
    public AppUser getUser(){
        return user;
    }
    public Product getProduct(){
        return product;
    }
    public int getRating(){
        return rating;
    }
    public String getComment(){
        return comment;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
}
