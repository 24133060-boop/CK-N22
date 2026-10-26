package vn.edu.ute.milktea.service;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.edu.ute.milktea.model.Shop;
/** Demo: OSRM public routing service, not guaranteed SLA. */
@Service
public class DeliveryMapService {
    private final HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build();
    private final ObjectMapper json=new ObjectMapper();
    public record Quote(double distanceKm, int etaMinutes, BigDecimal shippingFee) {
    }
    public Quote quote(Shop shop, Double lat, Double lon){
        if(shop.latitude==null||shop.longitude==null)throw new IllegalArgumentException("Chi nhánh chưa có tọa độ demo");
        if(lat==null||lon==null||!Double.isFinite(lat)||!Double.isFinite(lon)||lat<10.3||lat>11.4||lon<106.2||lon>107.3)
        throw new IllegalArgumentException("Vui lòng chọn điểm giao hàng trong khu vực hỗ trợ trên bản đồ");
        String path=String.format(Locale.US,"https://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=false",shop.longitude,shop.latitude,lon,lat);
        try {
            HttpRequest request=HttpRequest.newBuilder(URI.create(path)).timeout(Duration.ofSeconds(7)).header("User-Agent","UTEMILKTEA-university-demo/1.1").GET().build();
            HttpResponse<String> response=http.send(request,HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()!=200)throw new IllegalStateException("Dịch vụ định tuyến tạm thời không khả dụng");
            JsonNode route=json.readTree(response.body()).path("routes").path(0);
            double meters=route.path("distance").asDouble(-1);
            double seconds=route.path("duration").asDouble(-1);
            if(meters<0||seconds<0||meters>25000)throw new IllegalArgumentException("Địa chỉ nằm ngoài phạm vi giao hàng 25 km");
            double km=Math.ceil(meters/100.0)/10.0;
            int eta=10+(int)Math.ceil(seconds/60.0);
            long fee=12000+4000*(long)Math.ceil(meters/1000.0);
            BigDecimal shipping=BigDecimal.valueOf(fee);
            if(shop.deliveryProvider!=null && shop.deliveryProvider.active){
                BigDecimal base=shop.deliveryProvider.baseFee==null?BigDecimal.ZERO:shop.deliveryProvider.baseFee;
                BigDecimal rate=shop.deliveryProvider.perKmFee==null?BigDecimal.ZERO:shop.deliveryProvider.perKmFee;
                shipping=base.add(rate.multiply(BigDecimal.valueOf((long)Math.ceil(meters/1000.0))));
            }
            return new Quote(km,eta,shipping);
        }
        catch(IllegalArgumentException e){
            throw e;
        }
        catch(Exception e){
            throw new IllegalArgumentException("Không tính được quãng đường lúc này. Vui lòng thử lại hoặc chọn nhận tại cửa hàng.",e);
        }
    }
}
