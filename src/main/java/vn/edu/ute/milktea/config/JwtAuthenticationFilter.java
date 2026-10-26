package vn.edu.ute.milktea.config;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.edu.ute.milktea.service.JwtTokenService;
import java.io.IOException;
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwt;
    private final UserDetailsService users;
    public JwtAuthenticationFilter(JwtTokenService jwt,UserDetailsService users){
        this.jwt=jwt;
        this.users=users;
    }
    @Override protected boolean shouldNotFilter(HttpServletRequest request){
        return !request.getServletPath().startsWith("/api/");
    }
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
        String header=request.getHeader("Authorization");
        if(header!=null&&header.startsWith("Bearer ")&&SecurityContextHolder.getContext().getAuthentication()==null){
            String email=jwt.subject(header.substring(7));
            if(email!=null){
                try{
                    var user=users.loadUserByUsername(email);
                    var auth=new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                catch(Exception ignored){
                }
            }
        }
        chain.doFilter(request,response);
    }
}
