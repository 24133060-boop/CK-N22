package vn.edu.ute.milktea.service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
@Service
public class JwtTokenService {
    private final SecretKey key;
    public JwtTokenService(@Value("${app.jwt.secret:}") String secret){
        if(secret==null||secret.getBytes(StandardCharsets.UTF_8).length<32) {
            key=null;
        }
        else key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public boolean configured(){
        return key!=null;
    }
    public String issue(String email){
        if(key==null)throw new IllegalStateException("JWT_SECRET chưa cấu hình (ít nhất 32 byte)");
        Instant now=Instant.now();
        return Jwts.builder().subject(email).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(3600))).signWith(key).compact();
    }
    public String subject(String token){
        if(key==null)return null;
        try{
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
        }
        catch(Exception ex){
            return null;
        }
    }
}
