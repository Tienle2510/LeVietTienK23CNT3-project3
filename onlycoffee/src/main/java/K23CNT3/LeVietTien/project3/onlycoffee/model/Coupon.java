package K23CNT3.LeVietTien.project3.onlycoffee.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "usage_limit")
    private Integer usageLimit = 1;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "per_user_limit")
    private Integer perUserLimit = 1;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }

    // Helper methods
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive &&
                now.isAfter(startDate) &&
                now.isBefore(endDate) &&
                (usageLimit == null || usageCount < usageLimit);
    }

    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!isValid() || orderAmount.compareTo(minOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (discountType == DiscountType.PERCENTAGE) {
            discount = orderAmount.multiply(discountValue.divide(BigDecimal.valueOf(100)));
            if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
                discount = maxDiscountAmount;
            }
        } else if (discountType == DiscountType.FIXED_AMOUNT) {
            discount = discountValue;
        }

        return discount;
    }
}
