package vn.edu.ute.milktea.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import vn.edu.ute.milktea.model.*;
import vn.edu.ute.milktea.repository.*;
@Service public class OrderService {
    private final OrderRepository orders;
    private final ProductRepository products;
    private final UserRepository users;
    private final DeliveryMapService deliveryMap;
    private final VoucherService voucherService;
    private final OptionPricingService optionPricing;
    private final ShopPromotionRepository shopPromotions;
    private final ShopCustomerBlockRepository blockedCustomers;
    public OrderService(OrderRepository o,ProductRepository p,UserRepository u,DeliveryMapService d,VoucherService vouchers,OptionPricingService pricing,ShopPromotionRepository shopPromotions,ShopCustomerBlockRepository blockedCustomers){
        orders=o;
        products=p;
        users=u;
        deliveryMap=d;
        voucherService=vouchers;
        optionPricing=pricing;
        this.shopPromotions=shopPromotions;
        this.blockedCustomers=blockedCustomers;
    }
    @Transactional public CustomerOrder place(String email,Long productId,String size,String sugar,String ice,String topping,int qty,String fulfillment,String address){
        if("DELIVERY".equals(fulfillment))throw new IllegalArgumentException("Để giao tận nơi, hãy thêm món vào giỏ và chọn vị trí trên bản đồ.");
        if(qty<1||qty>30)throw new IllegalArgumentException("Số lượng không hợp lệ");
        Product p=products.findById(productId).orElseThrow();
        if(!p.available||!p.shop.active)throw new IllegalArgumentException("Sản phẩm ngừng bán");
        if(blockedCustomers.findByShopIdAndCustomerId(p.shop.id,users.findByEmail(email).orElseThrow().id).isPresent())throw new IllegalArgumentException("Bạn không thể đặt hàng tại chi nhánh này. Vui lòng liên hệ cửa hàng.");
        java.time.LocalTime now=java.time.LocalTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        java.time.LocalTime open=p.shop.opensAt==null?java.time.LocalTime.of(8,0):p.shop.opensAt;
        java.time.LocalTime close=p.shop.closesAt==null?java.time.LocalTime.of(22,0):p.shop.closesAt;
        boolean isOpen=open.equals(close)|| (open.isBefore(close)?(!now.isBefore(open)&&now.isBefore(close)):(!now.isBefore(open)||now.isBefore(close)));
        if(!isOpen)throw new IllegalArgumentException("Cửa hàng đang ngoài giờ nhận đơn");
        if(!"PICKUP".equals(fulfillment)&&!"DELIVERY".equals(fulfillment))throw new IllegalArgumentException("Phương thức nhận không hợp lệ");
        if("DELIVERY".equals(fulfillment)&&(address==null||address.isBlank()))throw new IllegalArgumentException("Vui lòng nhập địa chỉ giao hàng");
        CustomerOrder o=new CustomerOrder();
        o.customer=users.findByEmail(email).orElseThrow();
        o.shop=p.shop;
        o.productName=p.name;
        o.size=size;
        o.sugar=sugar;
        o.ice=ice;
        o.topping=topping;
        o.quantity=qty;
        o.fulfillment=fulfillment;
        o.address=address;
        o.total=optionPricing.unitPrice(p.price,size,topping).multiply(BigDecimal.valueOf(qty));
        var today=java.time.LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        var promotion=shopPromotions.findByShopId(p.shop.id).stream().filter(pr->(pr.discountType==null||"PRODUCT_PERCENT".equals(pr.discountType))&&pr.active&&pr.percent!=null&&pr.percent.signum()>0&&pr.percent.compareTo(new BigDecimal("70"))<=0&&pr.endDate!=null&&!pr.endDate.isBefore(today)).max(java.util.Comparator.comparing(pr->pr.percent)).orElse(null);
        if(promotion!=null){
            o.appliedShopPromotion=promotion.title;
            o.shopPromotionDiscount=o.total.multiply(promotion.percent).divide(new BigDecimal("100"),0,java.math.RoundingMode.DOWN);
            o.discountAmount=o.shopPromotionDiscount;
            o.total=o.total.subtract(o.discountAmount);
        }
        return orders.save(o);
    }
    public CustomerOrder savePaymentChoice(CustomerOrder o){
        return orders.save(o);
    }
    public record Selection(Long productId,String size,String sugar,String ice,String topping,int quantity){
    }
    @Transactional public CustomerOrder placeGroup(String email,java.util.List<Selection> selections,String fulfillment,String address,Double deliveryLatitude,Double deliveryLongitude,String voucherCode){
        if(selections==null||selections.isEmpty())throw new IllegalArgumentException("Không có món để đặt");
        if("DELIVERY".equals(fulfillment) && (deliveryLatitude==null||deliveryLongitude==null||!Double.isFinite(deliveryLatitude)||!Double.isFinite(deliveryLongitude)||deliveryLatitude < -90||deliveryLatitude > 90||deliveryLongitude < -180||deliveryLongitude > 180))throw new IllegalArgumentException("Vui lòng chọn vị trí giao hàng hợp lệ trên bản đồ");
        CustomerOrder order=null;
        java.math.BigDecimal sum=java.math.BigDecimal.ZERO;
        for(Selection selection:selections){
            if(selection.quantity()<1||selection.quantity()>30)throw new IllegalArgumentException("Số lượng không hợp lệ");
            Product p=products.findById(selection.productId()).orElseThrow(()->new IllegalArgumentException("Không tìm thấy món"));
            if(!p.available||!p.shop.active)throw new IllegalArgumentException("Sản phẩm ngừng bán");
            if(blockedCustomers.findByShopIdAndCustomerId(p.shop.id,users.findByEmail(email).orElseThrow().id).isPresent())throw new IllegalArgumentException("Bạn không thể đặt hàng tại chi nhánh này. Vui lòng liên hệ cửa hàng.");
            java.time.LocalTime now=java.time.LocalTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
            java.time.LocalTime open=p.shop.opensAt==null?java.time.LocalTime.of(8,0):p.shop.opensAt;
            java.time.LocalTime close=p.shop.closesAt==null?java.time.LocalTime.of(22,0):p.shop.closesAt;
            boolean isOpen=open.equals(close)||(open.isBefore(close)?(!now.isBefore(open)&&now.isBefore(close)):(!now.isBefore(open)||now.isBefore(close)));
            if(!isOpen)throw new IllegalArgumentException("Cửa hàng đang ngoài giờ nhận đơn");
            if(!"DELIVERY".equals(fulfillment)&&!"PICKUP".equals(fulfillment))throw new IllegalArgumentException("Hình thức nhận không hợp lệ");
            if("DELIVERY".equals(fulfillment)&&(address==null||address.isBlank()))throw new IllegalArgumentException("Vui lòng nhập địa chỉ giao hàng");
            if(order==null){
                order=new CustomerOrder();
                order.customer=users.findByEmail(email).orElseThrow();
                order.shop=p.shop;
                order.fulfillment=fulfillment;
                order.address=address;
                order.productName=p.name;
                order.size=selection.size();
                order.sugar=selection.sugar();
                order.ice=selection.ice();
                order.topping=selection.topping();
                order.quantity=0;
            }
            else if(!order.shop.id.equals(p.shop.id))throw new IllegalArgumentException("Không thể gộp các chi nhánh khác nhau");
            OrderLine line=new OrderLine();
            line.order=order;
            line.productId=p.id;
            line.productName=p.name;
            line.size=selection.size();
            line.sugar=selection.sugar();
            line.ice=selection.ice();
            line.topping=selection.topping();
            line.quantity=selection.quantity();
            line.unitPrice=optionPricing.unitPrice(p.price,selection.size(),selection.topping());
            line.subtotal=line.unitPrice.multiply(java.math.BigDecimal.valueOf(selection.quantity()));
            order.lines.add(line);
            order.quantity+=selection.quantity();
            sum=sum.add(line.subtotal);
        }
        order.productName=order.lines.size()==1?order.lines.get(0).productName:order.lines.get(0).productName+" + "+(order.lines.size()-1)+" món khác";
        var voucher=voucherService.findValid(voucherCode);
        // Use the single strongest active shop promotion, then calculate voucher on the remaining subtotal.
        var today=java.time.LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        var shopPromo=shopPromotions.findByShopId(order.shop.id).stream()
        .filter(pr->(pr.discountType==null||"PRODUCT_PERCENT".equals(pr.discountType))&&pr.active&&pr.percent!=null&&pr.percent.signum()>0&&pr.percent.compareTo(new BigDecimal("70"))<=0&&pr.endDate!=null&&!pr.endDate.isBefore(today))
        .max(java.util.Comparator.comparing(pr->pr.percent)).orElse(null);
        if(shopPromo!=null){
            order.shopPromotionDiscount=sum.multiply(shopPromo.percent).divide(new BigDecimal("100"),0,java.math.RoundingMode.DOWN);
            order.appliedShopPromotion=shopPromo.title;
        }
        BigDecimal afterPromotion=sum.subtract(order.shopPromotionDiscount);
        BigDecimal voucherDiscount=voucherService.discount(voucher,afterPromotion);
        order.discountAmount=order.shopPromotionDiscount.add(voucherDiscount);
        order.voucherCode=voucher==null?null:voucher.code;
        order.total=sum.subtract(order.discountAmount);
        if("DELIVERY".equals(fulfillment)){
            DeliveryMapService.Quote quote=deliveryMap.quote(order.shop,deliveryLatitude,deliveryLongitude);
            order.deliveryLatitude=deliveryLatitude;
            order.deliveryLongitude=deliveryLongitude;
            order.distanceKm=quote.distanceKm();
            order.etaMinutes=quote.etaMinutes();
            order.shippingFee=quote.shippingFee();
            var shippingPromo=shopPromotions.findByShopId(order.shop.id).stream()
            .filter(pr->"SHIPPING_PERCENT".equals(pr.discountType)&&pr.active&&pr.percent!=null&&pr.percent.signum()>0&&pr.percent.compareTo(new BigDecimal("100"))<=0&&pr.endDate!=null&&!pr.endDate.isBefore(today))
            .max(java.util.Comparator.comparing(pr->pr.percent)).orElse(null);
            if(shippingPromo!=null)order.shippingDiscountAmount=quote.shippingFee().multiply(shippingPromo.percent).divide(new BigDecimal("100"),0,java.math.RoundingMode.DOWN);
            order.total=sum.subtract(order.discountAmount).add(quote.shippingFee()).subtract(order.shippingDiscountAmount);
        }
        return orders.save(order);
    }
}
