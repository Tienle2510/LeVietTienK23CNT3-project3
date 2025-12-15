package K23CNT3.LeVietTien.project3.onlycoffee.repository;

import K23CNT3.LeVietTien.project3.onlycoffee.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByUserId(Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findByOrderStatus(Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'DELIVERED'")
    long countDeliveredOrders();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderStatus = 'DELIVERED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT o FROM Order o WHERE " +
            "(:orderCode IS NULL OR o.orderCode LIKE CONCAT('%', :orderCode, '%')) AND " +
            "(:customerName IS NULL OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', :customerName, '%'))) AND " +
            "(:customerPhone IS NULL OR o.customerPhone LIKE CONCAT('%', :customerPhone, '%')) AND " +
            "(:status IS NULL OR o.orderStatus = :status)")
    Page<Order> searchOrders(
            @Param("orderCode") String orderCode,
            @Param("customerName") String customerName,
            @Param("customerPhone") String customerPhone,
            @Param("status") Order.OrderStatus status,
            Pageable pageable);
}