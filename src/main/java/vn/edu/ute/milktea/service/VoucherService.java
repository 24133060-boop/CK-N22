package vn.edu.ute.milktea.service;
import java.math.*;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import vn.edu.ute.milktea.model.Voucher;
import vn.edu.ute.milktea.repository.VoucherRepository;
@Service public class VoucherService {
    private final VoucherRepository repository;
    public VoucherService(VoucherRepository r){
        repository=r;
    }
    public Voucher findValid(String code){
        if(code==null||code.isBlank())return null;
        Voucher v=repository.findByCodeIgnoreCase(code.trim()).orElseThrow(()->new IllegalArgumentException("Mã giảm giá không tồn tại"));
        if(!v.active||(v.expiresOn!=null&&v.expiresOn.isBefore(LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")))))throw new IllegalArgumentException("Mã giảm giá đã hết hạn hoặc bị tắt");
        return v;
    }
    public BigDecimal discount(Voucher v,BigDecimal subtotal){
        if(v==null)return BigDecimal.ZERO;
        if(v.percent==null||v.percent.signum()<=0||v.percent.compareTo(new BigDecimal("100"))>0||v.maxDiscount==null||v.maxDiscount.signum()<=0||v.minSubtotal==null||v.minSubtotal.signum()<0)throw new IllegalArgumentException("Cấu hình mã giảm giá không hợp lệ");
        if(subtotal.compareTo(v.minSubtotal)<0)throw new IllegalArgumentException("Đơn hàng chưa đạt giá trị tối thiểu của mã "+v.code);
        BigDecimal result=subtotal.multiply(v.percent).divide(new BigDecimal("100"),0,RoundingMode.DOWN);
        return result.min(v.maxDiscount).min(subtotal).max(BigDecimal.ZERO);
    }
}
