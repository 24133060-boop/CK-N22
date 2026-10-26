package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
@Entity @Table(name="app_users") public class AppUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false,unique=true,length=120) public String email;
    @Column(nullable=false) public String password;
    @Column(nullable=false, columnDefinition="NVARCHAR(255)") public String displayName;
    @Column(nullable=false) public String role="USER";
    public AppUser(){
    }
    public AppUser(String e,String p,String n,String r){
        email=e;
        password=p;
        displayName=n;
        role=r;
    }
    // JavaBeans accessors for JSP Expression Language
    public Long getId() {
        return this.id;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
    public String getDisplayName() {
        return this.displayName;
    }
    public String getRole() {
        return this.role;
    }
}
