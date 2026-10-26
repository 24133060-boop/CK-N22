package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.time.*;
import java.math.*;
@Entity public class DeliveryAddress {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) public AppUser user;
    @Column(columnDefinition="NVARCHAR(100)") public String recipient;
    @Column(length=20) public String phone;
    @Column(columnDefinition="NVARCHAR(500)") public String address;
    public boolean primaryAddress=false;
    public Long getId(){
        return id;
    }
    public String getRecipient(){
        return recipient;
    }
    public String getPhone(){
        return phone;
    }
    public String getAddress(){
        return address;
    }
    public boolean getPrimaryAddress(){
        return primaryAddress;
    }
}
