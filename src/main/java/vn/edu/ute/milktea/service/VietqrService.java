package vn.edu.ute.milktea.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@Service public class VietqrService {
    @Value("${app.vietqr.bank-id:970436}") private String bank;
    @Value("${app.vietqr.account-no:}") private String account;
    @Value("${app.vietqr.account-name:}") private String name;
    public boolean configured(){
        return !account.isBlank()&&!name.isBlank()&&account.matches("[0-9]{6,20}")&&bank.matches("[0-9]{6}");
    }
    public String account(){
        return account;
    }
    public String accountName(){
        return name;
    }
    public String bank(){
        return bank;
    }
    public String transferContent(long id){
        return "UTEMILKTEA DH"+id;
    }
    public String qrUrl(long id,BigDecimal total){
        if(!configured())throw new IllegalStateException("Chưa cấu hình tài khoản VietQR");
        if(total==null||total.signum()<=0||total.stripTrailingZeros().scale()>0)throw new IllegalArgumentException("Số tiền VietQR phải là số nguyên VND dương");
        String base="https://img.vietqr.io/image/"+bank+"-"+account+"-compact2.png";
        return base+"?amount="+total.toBigIntegerExact()+"&addInfo="+enc(transferContent(id))+"&accountName="+enc(name);
    }
    private String enc(String x){
        return URLEncoder.encode(x,StandardCharsets.UTF_8);
    }
}
