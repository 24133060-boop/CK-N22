package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
@Entity @Table(name="cart_snapshots") public class CartSnapshot {
    @Id @Column(length=120) public String email;
    @Lob @Column(columnDefinition="NVARCHAR(MAX)") public String payload;
    public CartSnapshot(){
    }
    public CartSnapshot(String e,String p){
        email=e;
        payload=p;
    }
}
