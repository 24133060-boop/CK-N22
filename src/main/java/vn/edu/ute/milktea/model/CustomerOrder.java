package vn.edu.ute.milktea.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "customer_orders")
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(optional = false)
    public AppUser customer;
    @ManyToOne(optional = false)
    public Shop shop;
    @Column(columnDefinition = "NVARCHAR(255)")
    public String productName;
    public String size;
    @Column(columnDefinition = "NVARCHAR(255)")
    public String sugar;
    @Column(columnDefinition = "NVARCHAR(255)")
    public String ice;
    @Column(columnDefinition = "NVARCHAR(255)")
    public String topping;
    public int quantity;
    public String fulfillment;
    @Column(columnDefinition = "NVARCHAR(255)")
    public String address;
    public String status = "NEW";
    public String payment = "COD";
    @Column(length=40) public String paymentStatus = "UNPAID";
    @Column(length=100) public String paymentTransactionRef;
    public LocalDateTime paidAt;
    public String getPaymentStatus(){
        return paymentStatus;
    }
    public LocalDateTime getPaidAt(){
        return paidAt;
    }
    @Column(length = 120)
    public String shipperEmail;
    public LocalDateTime pickedUpAt;
    public LocalDateTime deliveredAt;
    @Column(columnDefinition = "NVARCHAR(500)")
    public String cancelReason;
    public String cancelledBy;
    public LocalDateTime cancelledAt;
    // Danh sách các món trong cùng một đơn hàng
    @OneToMany(
    mappedBy = "order",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.EAGER
    )
    @OrderBy("id ASC")
    public List<OrderLine> lines = new ArrayList<>();
    public BigDecimal total;
    @Column(precision=14,scale=2) public BigDecimal discountAmount=BigDecimal.ZERO;
    @Column(length=40) public String voucherCode;
    @Column(length=100) public String appliedShopPromotion;
    @Column(precision=14,scale=2) public BigDecimal shopPromotionDiscount=BigDecimal.ZERO;
    @Column(precision=14,scale=2) public BigDecimal shippingDiscountAmount=BigDecimal.ZERO;
    public BigDecimal getShippingDiscountAmount(){
        return shippingDiscountAmount;
    }
    public String getAppliedShopPromotion(){
        return appliedShopPromotion;
    }
    public BigDecimal getShopPromotionDiscount(){
        return shopPromotionDiscount;
    }
    public BigDecimal getDiscountAmount(){
        return discountAmount;
    }
    public String getVoucherCode(){
        return voucherCode;
    }
    @Column(precision=14,scale=2) public BigDecimal shippingFee = BigDecimal.ZERO;
    public Double distanceKm;
    public Integer etaMinutes;
    public Double deliveryLatitude;
    public Double deliveryLongitude;
    public LocalDateTime createdAt = LocalDateTime.now();
    // GETTERS
    public Long getId() {
        return id;
    }
    public AppUser getCustomer() {
        return customer;
    }
    public Shop getShop() {
        return shop;
    }
    public String getProductName() {
        return productName;
    }
    public String getSize() {
        return size;
    }
    public String getSugar() {
        return sugar;
    }
    public String getIce() {
        return ice;
    }
    public String getTopping() {
        return topping;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getFulfillment() {
        return fulfillment;
    }
    public String getAddress() {
        return address;
    }
    public String getStatus() {
        return status;
    }
    public String getPayment() {
        return payment;
    }
    public String getShipperEmail() {
        return shipperEmail;
    }
    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    public String getCancelReason() {
        return cancelReason;
    }
    public String getCancelledBy() {
        return cancelledBy;
    }
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    public List<OrderLine> getLines() {
        return lines;
    }
    public BigDecimal getShippingFee(){
        return shippingFee;
    }
    public Double getDistanceKm(){
        return distanceKm;
    }
    public Integer getEtaMinutes(){
        return etaMinutes;
    }
    public Double getDeliveryLatitude(){
        return deliveryLatitude;
    }
    public Double getDeliveryLongitude(){
        return deliveryLongitude;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
