package vn.edu.ute.milktea.service;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
class OptionPricingServiceTest {
    private final OptionPricingService pricing=new OptionPricingService();
    @Test void sizeAndToppingAreAdded(){
        assertEquals(0,pricing.unitPrice(new BigDecimal("25000"),"L","Pudding trứng").compareTo(new BigDecimal("42000")));
    }
    @Test void invalidOptionsAreRejected(){
        assertThrows(IllegalArgumentException.class,()->pricing.unitPrice(new BigDecimal("25000"),"XL","Không"));
    }
}
