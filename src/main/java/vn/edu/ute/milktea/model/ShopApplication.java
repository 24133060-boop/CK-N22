package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.time.*;
import java.math.*;
@Entity public class ShopApplication {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) public AppUser applicant;
    @Column(columnDefinition="NVARCHAR(255)") public String shopName;
    @Column(columnDefinition="NVARCHAR(500)") public String address;
    @Column(length=30) public String status="PENDING";
    @Column(columnDefinition="NVARCHAR(500)") public String note;
    public LocalDateTime createdAt=LocalDateTime.now();
    public Long getId(){
        return id;
    }
    public AppUser getApplicant(){
        return applicant;
    }
    public String getShopName(){
        return shopName;
    }
    public String getAddress(){
        return address;
    }
    public String getStatus(){
        return status;
    }
    public String getNote(){
        return note;
    }
}
