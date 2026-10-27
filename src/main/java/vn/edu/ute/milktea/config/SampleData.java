package vn.edu.ute.milktea.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import vn.edu.ute.milktea.model.*;
import vn.edu.ute.milktea.repository.*;
@Configuration public class SampleData {
    @Bean CommandLineRunner seed(UserRepository users,ShopRepository shops,ProductRepository products,PasswordEncoder encoder){
        return args->{
            if(users.count()==0){
                for(String role:new String[]{
                    "USER","VENDOR","MANAGER","ADMIN","SHIPPER"}
                )users.save(new AppUser(role.toLowerCase()+"@utemilktea.local",encoder.encode("Demo@12345"),"Demo "+role,role));
            }
            if(shops.count()==0){
                String[] names={
                    "UTEMILKTEA Thủ Đức","UTEMILKTEA Quận 1","UTEMILKTEA Bình Thạnh","UTEMILKTEA Gò Vấp","UTEMILKTEA Dĩ An"}
                ;
                String[] drinks={
                    "Trà sữa truyền thống","Trà sữa matcha","Trà đào cam sả","Trà sữa ô long","Trà sữa khoai môn","Trà vải hoa hồng"}
                ;
                for(String n:names){
                    Shop s=shops.save(new Shop(n,"Địa chỉ minh họa — cập nhật trước khi demo"));
                    s.vendorEmail="vendor@utemilktea.local";
                    shops.save(s);
                    for(int i=0;i<drinks.length;i++)products.save(new Product(drinks[i],"Trà sữa",BigDecimal.valueOf(29000+i*4000),s));
                }
            }
            // Coordinates are approximate demo locations, not verified store addresses.
            String[] demoNames={
                "Thủ Đức","Quận 1","Bình Thạnh","Gò Vấp","Dĩ An"}
            ;
            String[] demoAddresses={
                "Khu vực Võ Văn Ngân, Thủ Đức","Khu vực Nguyễn Huệ, Quận 1","Khu vực Phan Đăng Lưu, Bình Thạnh","Khu vực Quang Trung, Gò Vấp","Khu vực Trần Hưng Đạo, Dĩ An"}
            ;
            double[] lats={
                10.8505,10.7745,10.8025,10.8290,10.9060}
            ;
            double[] lons={
                106.7715,106.7030,106.6960,106.6660,106.7680}
            ;
            for(Shop s:shops.findAll()){
                for(int i=0;i<demoNames.length;i++)if(s.name!=null&&s.name.contains(demoNames[i])){
                    if(s.latitude==null||s.longitude==null){
                        s.latitude=lats[i];
                        s.longitude=lons[i];
                    }
                    if(s.address==null||s.address.contains("minh họa"))s.address=demoAddresses[i];
                    // Existing SQL Server records keep their address when restarting.
                    // Remove only the old display suffix without changing the location.
                    else if(s.address.endsWith(" (demo)"))s.address=s.address.substring(0,s.address.length()-7);
                    shops.save(s);
                    break;
                }
            }
            for(Shop s:shops.findAll()){
                if(s.vendorEmail==null){
                    s.vendorEmail="vendor@utemilktea.local";
                    shops.save(s);
                }
            }
        }
        ;
    }
}
