package vn.edu.ute.milktea.service;
import java.util.Map;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
@Service public class RealtimeNotificationService {
    private final SimpMessagingTemplate messaging;
    public RealtimeNotificationService(SimpMessagingTemplate messaging){
        this.messaging=messaging;
    }
    // Public notifications contain no customer/order information.
    public void orderChanged(){
        messaging.convertAndSend("/topic/order-updates",Map.of("type","ORDER_CHANGED"));
    }
}
