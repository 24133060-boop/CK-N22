package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
@Entity public class Shop {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(columnDefinition="NVARCHAR(255)") public String name;
    @Column(columnDefinition="NVARCHAR(255)") public String address;
    public Double latitude;
    public Double longitude;
    public boolean active=true;
    public java.time.LocalTime opensAt=java.time.LocalTime.of(8,0);
    public java.time.LocalTime closesAt=java.time.LocalTime.of(22,0);
    @Column(length=120) public String vendorEmail;
    @Column(length=120) public String managerEmail;
    public java.math.BigDecimal commissionPercent;
    @ManyToOne public DeliveryProvider deliveryProvider;
    public Shop(){
    }
    public Shop(String n,String a){
        name=n;
        address=a;
    }
    // JavaBeans accessors for JSP Expression Language
    public Double getLatitude(){
        return latitude;
    }
    public Double getLongitude(){
        return longitude;
    }
    public Long getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getAddress() {
        return this.address;
    }
    public java.time.LocalTime getOpensAt(){
        return opensAt;
    }
    public java.time.LocalTime getClosesAt(){
        return closesAt;
    }
    public String getVendorEmail(){
        return vendorEmail;
    }
    public String getManagerEmail(){
        return managerEmail;
    }
    public java.math.BigDecimal getCommissionPercent(){
        return commissionPercent;
    }
    public DeliveryProvider getDeliveryProvider(){
        return deliveryProvider;
    }
    public boolean getActive() {
        return this.active;
    }
}
