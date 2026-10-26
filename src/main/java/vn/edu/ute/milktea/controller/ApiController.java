package vn.edu.ute.milktea.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.ute.milktea.dto.UserProfileDto;
import vn.edu.ute.milktea.mapper.UserProfileMapper;
import vn.edu.ute.milktea.repository.UserRepository;
import vn.edu.ute.milktea.service.JwtTokenService;
import java.util.Map;
@RestController @RequestMapping("/api")
public class ApiController {
    private final AuthenticationManager manager;
    private final JwtTokenService jwt;
    private final UserRepository users;
    private final UserProfileMapper mapper;
    public ApiController(AuthenticationManager manager,JwtTokenService jwt,UserRepository users,UserProfileMapper mapper){
        this.manager=manager;
        this.jwt=jwt;
        this.users=users;
        this.mapper=mapper;
    }
    public record LoginRequest(String email,String password){
    }
    @PostMapping("/auth/login") public ResponseEntity<?> login(@RequestBody LoginRequest request){
        if(!jwt.configured())return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error","JWT_SECRET chưa được cấu hình"));
        try{
            manager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
            return ResponseEntity.ok(Map.of("token",jwt.issue(request.email()),"type","Bearer","expiresIn",3600));
        }
        catch(Exception ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Thông tin đăng nhập không đúng"));
        }
    }
    @GetMapping("/me") public ResponseEntity<UserProfileDto> me(Authentication authentication){
        return users.findByEmail(authentication.getName()).map(u->ResponseEntity.ok(mapper.toDto(u))).orElseGet(()->ResponseEntity.notFound().build());
    }
}
