package vn.edu.ute.milktea.service;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Service;
/** Centralized server-side pricing and validation for drink options. */
@Service
public class OptionPricingService {
    private static final Map<String, Integer> SIZES = Map.of("S", 0, "M", 5000, "L", 10000);
    private static final Map<String, Integer> TOPPINGS = Map.of(
    "Không", 0, "Trân châu đen", 5000, "Trân châu trắng", 6000,
    "Thạch dừa", 5000, "Pudding trứng", 7000, "Kem cheese", 10000);
    public BigDecimal unitPrice(BigDecimal base, String size, String topping) {
        if (base == null || base.signum() < 0) throw new IllegalArgumentException("Giá sản phẩm không hợp lệ");
        Integer s = SIZES.get(size);
        Integer t = TOPPINGS.get(topping);
        if (s == null || t == null) throw new IllegalArgumentException("Size hoặc topping không hợp lệ");
        return base.add(BigDecimal.valueOf(s + t));
    }
}
