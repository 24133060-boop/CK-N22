package vn.edu.ute.milktea.service;
import org.junit.jupiter.api.Test;
import vn.edu.ute.milktea.model.Voucher;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
class VoucherServiceTest {
    private final VoucherService service = new VoucherService(null);
    @Test void capsDiscountAtMaxDiscount() {
        Voucher v = new Voucher();
        v.code = "SAVE20";
        v.percent = new BigDecimal("20");
        v.minSubtotal = new BigDecimal("100000");
        v.maxDiscount = new BigDecimal("15000");
        assertEquals(new BigDecimal("15000"), service.discount(v, new BigDecimal("200000")));
    }
    @Test void rejectsBelowMinimum() {
        Voucher v = new Voucher();
        v.code = "SAVE20";
        v.percent = new BigDecimal("20");
        v.minSubtotal = new BigDecimal("100000");
        v.maxDiscount = new BigDecimal("15000");
        assertThrows(IllegalArgumentException.class, () -> service.discount(v, new BigDecimal("99999")));
    }
    @Test void rejectsInvalidPercentAndNegativeMinimum() {
        Voucher v = new Voucher();
        v.code = "BAD";
        v.percent = new BigDecimal("101");
        v.minSubtotal = BigDecimal.ZERO;
        v.maxDiscount = BigDecimal.ONE;
        assertThrows(IllegalArgumentException.class, () -> service.discount(v, new BigDecimal("100")));
        v.percent = new BigDecimal("10");
        v.minSubtotal = new BigDecimal("-1");
        assertThrows(IllegalArgumentException.class, () -> service.discount(v, new BigDecimal("100")));
    }
    @Test void neverDiscountsMoreThanSubtotal() {
        Voucher v = new Voucher();
        v.code = "SAVE100";
        v.percent = new BigDecimal("100");
        v.minSubtotal = BigDecimal.ZERO;
        v.maxDiscount = new BigDecimal("999999");
        assertEquals(new BigDecimal("10000"), service.discount(v, new BigDecimal("10000")));
    }
}
