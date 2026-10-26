package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.time.*;
import java.math.*;
@Entity public class ReturnRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) public CustomerOrder order;
    @ManyToOne(optional=false) public AppUser customer;
    @Column(columnDefinition="NVARCHAR(500)") public String reason;
    @Column(length=25) public String status="REQUESTED";
    @Column(columnDefinition="NVARCHAR(500)") public String response;
    public LocalDateTime createdAt=LocalDateTime.now();
    public Long getId(){
        return id;
    }
    public CustomerOrder getOrder(){
        return order;
    }
    public String getReason(){
        return reason;
    }
    public String getStatus(){
        return status;
    }
    public String getResponse(){
        return response;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
}
