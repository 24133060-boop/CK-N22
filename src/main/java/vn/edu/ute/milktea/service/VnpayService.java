package vn.edu.ute.milktea.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
/** VNPay sandbox V2.1, all signatures verified server-side. */
@Service
public class VnpayService {
    @Value("${app.vnpay.tmn-code:}") private String tmnCode;
    @Value("${app.vnpay.hash-secret:}") private String secret;
    @Value("${app.vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}") private String payUrl;
    @Value("${app.vnpay.return-url:http://localhost:8080/payment/vnpay/return}") private String returnUrl;
    private static final DateTimeFormatter F=DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ZoneId VN=ZoneId.of("Asia/Ho_Chi_Minh");
    public boolean configured(){
        return !tmnCode.isBlank()&&!secret.isBlank();
    }
    public String createUrl(Long orderId,BigDecimal amount,String ip){
        if(!configured())throw new IllegalStateException("Chưa cấu hình VNPAY_TMN_CODE / VNPAY_HASH_SECRET");
        if(amount==null||amount.signum()<=0)throw new IllegalArgumentException("Số tiền không hợp lệ");
        LocalDateTime now=LocalDateTime.now(VN);
        SortedMap<String,String> p=new TreeMap<>();
        p.put("vnp_Version","2.1.0");
        p.put("vnp_Command","pay");
        p.put("vnp_TmnCode",tmnCode);
        p.put("vnp_Amount",amount.multiply(BigDecimal.valueOf(100)).toBigIntegerExact().toString());
        p.put("vnp_CurrCode","VND");
        p.put("vnp_TxnRef",orderId+"-"+UUID.randomUUID().toString().replace("-","").substring(0,16));
        p.put("vnp_OrderInfo","Thanh toan don hang "+orderId);
        p.put("vnp_OrderType","other");
        p.put("vnp_Locale","vn");
        p.put("vnp_ReturnUrl",returnUrl);
        p.put("vnp_IpAddr",ip==null||ip.isBlank()?"127.0.0.1":ip);
        p.put("vnp_CreateDate",now.format(F));
        p.put("vnp_ExpireDate",now.plusMinutes(15).format(F));
        String query=canonical(p);
        return payUrl+"?"+query+"&vnp_SecureHash="+sign(query);
    }
    public boolean verify(Map<String,String> data){
        if(!configured())return false;
        String hash=data.get("vnp_SecureHash");
        if(hash==null||hash.isBlank())return false;
        SortedMap<String,String> p=new TreeMap<>();
        data.forEach((k,v)->{
            if(k.startsWith("vnp_")&&!k.equals("vnp_SecureHash")&&!k.equals("vnp_SecureHashType")&&v!=null&&!v.isBlank())p.put(k,v);
        }
        );
        return java.security.MessageDigest.isEqual(sign(canonical(p)).getBytes(StandardCharsets.US_ASCII),hash.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII));
    }
    public long orderId(String ref){
        if(ref==null||!ref.matches("[0-9]+-[a-f0-9]{16}"))throw new IllegalArgumentException("Mã giao dịch không hợp lệ");
        return Long.parseLong(ref.substring(0,ref.indexOf('-')));
    }
    public boolean matchesAmount(BigDecimal total,String amount){
        try{
            return total.multiply(BigDecimal.valueOf(100)).toBigIntegerExact().toString().equals(amount);
        }
        catch(Exception ex){
            return false;
        }
    }
    private String canonical(Map<String,String> map){
        StringJoiner join=new StringJoiner("&");
        map.forEach((k,v)->join.add(enc(k)+"="+enc(v)));
        return join.toString();
    }
    private String enc(String s){
        return URLEncoder.encode(s,StandardCharsets.UTF_8).replace("+","%20");
    }
    private String sign(String input){
        try{
            Mac mac=Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA512"));
            return HexFormat.of().formatHex(mac.doFinal(input.getBytes(StandardCharsets.UTF_8)));
        }
        catch(Exception e){
            throw new IllegalStateException("Không ký được VNPay",e);
        }
    }
}
