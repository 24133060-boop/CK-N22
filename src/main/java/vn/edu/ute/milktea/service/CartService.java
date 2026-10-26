package vn.edu.ute.milktea.service;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import vn.edu.ute.milktea.model.*;
import vn.edu.ute.milktea.repository.*;
@Service
public class CartService {
    public static class Item {
        private Long productId;
        private String size,sugar,ice,topping;
        private int quantity;
        public Item(){
        }
        public Item(Long id,String size,String sugar,String ice,String topping,int quantity){
            this.productId=id;
            this.size=size;
            this.sugar=sugar;
            this.ice=ice;
            this.topping=topping;
            this.quantity=quantity;
        }
        public Long getProductId(){
            return productId;
        }
        public String getSize(){
            return size;
        }
        public String getSugar(){
            return sugar;
        }
        public String getIce(){
            return ice;
        }
        public String getTopping(){
            return topping;
        }
        public int getQuantity(){
            return quantity;
        }
        public void setQuantity(int quantity){
            this.quantity=quantity;
        }
        public void setProductId(Long id){
            this.productId=id;
        }
        public void setSize(String value){
            this.size=value;
        }
        public void setSugar(String value){
            this.sugar=value;
        }
        public void setIce(String value){
            this.ice=value;
        }
        public void setTopping(String value){
            this.topping=value;
        }
    }
    public static class DisplayItem {
        private final Item item;
        private final Product product;
        private final OptionPricingService pricing;
        public DisplayItem(Item i,Product p,OptionPricingService pricing){
            item=i;
            product=p;
            this.pricing=pricing;
        }
        public Item getItem(){
            return item;
        }
        public Product getProduct(){
            return product;
        }
        public java.math.BigDecimal getUnitPrice(){
            return pricing.unitPrice(product.price,item.size,item.topping);
        }
        public java.math.BigDecimal getSubtotal(){
            return getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.quantity));
        }
    }
    private final ProductRepository products;
    private final OptionPricingService optionPricing;
    private final OrderService orderService;
    private final CartSnapshotRepository snapshots;
    private final ObjectMapper mapper;
    public CartService(ProductRepository products,OrderService orderService,CartSnapshotRepository snapshots,ObjectMapper mapper,OptionPricingService optionPricing){
        this.optionPricing=optionPricing;
        this.products=products;
        this.orderService=orderService;
        this.snapshots=snapshots;
        this.mapper=mapper;
    }
    private String currentEmail(){
        var auth=SecurityContextHolder.getContext().getAuthentication();
        return auth==null||!auth.isAuthenticated()||"anonymousUser".equals(auth.getName())?null:auth.getName().toLowerCase(java.util.Locale.ROOT);
    }
    private void persist(HttpSession session){
        String email=currentEmail();
        if(email==null)return;
        try{
            snapshots.save(new CartSnapshot(email,mapper.writeValueAsString(items(session))));
        }
        catch(com.fasterxml.jackson.core.JsonProcessingException e){
            throw new IllegalStateException("Không thể lưu giỏ hàng",e);
        }
    }
    public void remove(HttpSession session,int index){
        var current=items(session);
        if(index>=0&&index<current.size()){
            current.remove(index);
            persist(session);
        }
    }
    @SuppressWarnings("unchecked")
    public List<Item> items(HttpSession session){
        Object value=session.getAttribute("utemilktea_cart");
        if(value instanceof List<?>) return (List<Item>)value;
        List<Item> cart=new ArrayList<>();
        String email=currentEmail();
        if(email!=null){
            var saved=snapshots.findById(email);
            if(saved.isPresent()&&saved.get().payload!=null){
                try{
                    cart=mapper.readValue(saved.get().payload,new TypeReference<List<Item>>(){
                    }
                    );
                }
                catch(Exception e){
                    throw new IllegalStateException("Dữ liệu giỏ hàng không hợp lệ",e);
                }
            }
        }
        session.setAttribute("utemilktea_cart",cart);
        return cart;
    }
    public void add(HttpSession session,Long productId,String size,String sugar,String ice,String topping,int quantity){
        if(quantity<1||quantity>30)throw new IllegalArgumentException("Số lượng không hợp lệ");
        Product p=products.findById(productId).orElseThrow(()->new IllegalArgumentException("Không tìm thấy món"));
        if(!p.available||!p.shop.active)throw new IllegalArgumentException("Món không còn bán");
        optionPricing.unitPrice(p.price,size,topping);
        if(items(session).size()>=50)throw new IllegalArgumentException("Giỏ hàng đã đạt giới hạn");
        items(session).add(new Item(productId,size,sugar,ice,topping,quantity));
        persist(session);
    }
    public void update(HttpSession session,int index,int quantity){
        List<Item> cart=items(session);
        if(index<0||index>=cart.size()||quantity<1||quantity>30)throw new IllegalArgumentException("Số lượng không hợp lệ");
        cart.get(index).setQuantity(quantity);
        persist(session);
    }
    public List<DisplayItem> display(HttpSession session){
        List<DisplayItem> result=new ArrayList<>();
        for(Item i:items(session)) products.findById(i.productId).ifPresent(p->result.add(new DisplayItem(i,p,optionPricing)));
        return result;
    }
    @Transactional
    public int checkout(HttpSession session,String email,String fulfillment,String address,Double deliveryLatitude,Double deliveryLongitude,String voucherCode,String paymentMethod){
        if(!java.util.Set.of("COD","VIETQR").contains(paymentMethod))throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ");
        List<Item> current=items(session);
        if(current.isEmpty())throw new IllegalArgumentException("Giỏ hàng trống");
        java.util.Map<Long,java.util.List<OrderService.Selection>> groups=new java.util.LinkedHashMap<>();
        for(Item item:current){
            Product p=products.findById(item.productId).orElseThrow(()->new IllegalArgumentException("Món không tồn tại"));
            groups.computeIfAbsent(p.shop.id,k->new java.util.ArrayList<>())
            .add(new OrderService.Selection(item.productId,item.size,item.sugar,item.ice,item.topping,item.quantity));
        }
        for(java.util.List<OrderService.Selection> group:groups.values())
        {
            var placed=orderService.placeGroup(email,group,fulfillment,address,deliveryLatitude,deliveryLongitude,voucherCode);
            placed.payment=paymentMethod;
            placed.paymentStatus="UNPAID";
            orderService.savePaymentChoice(placed);
        }
        int count=groups.size();
        session.removeAttribute("utemilktea_cart");
        snapshots.deleteById(email.toLowerCase(java.util.Locale.ROOT));
        return count;
    }
}
