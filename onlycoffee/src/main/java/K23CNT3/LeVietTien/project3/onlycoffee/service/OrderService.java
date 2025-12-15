package K23CNT3.LeVietTien.project3.onlycoffee.service;

import K23CNT3.LeVietTien.project3.onlycoffee.model.Order;
import K23CNT3.LeVietTien.project3.onlycoffee.model.OrderItem;
import K23CNT3.LeVietTien.project3.onlycoffee.model.User;
import K23CNT3.LeVietTien.project3.onlycoffee.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderByCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode);
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    @Transactional
    public Order createOrder(Order order, User user) {
        // Set user và thông tin khách hàng
        order.setUser(user);
        if (order.getCustomerName() == null) {
            order.setCustomerName(user.getFullName());
        }
        if (order.getCustomerEmail() == null) {
            order.setCustomerEmail(user.getEmail());
        }
        if (order.getCustomerPhone() == null) {
            order.setCustomerPhone(user.getPhone());
        }

        // Tính toán tổng tiền từ order items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : order.getOrderItems()) {
            item.calculateTotalPrice();
            subtotal = subtotal.add(item.getTotalPrice());

            // Cập nhật số lượng sản phẩm
            productService.updateProductQuantity(item.getProduct().getId(), -item.getQuantity());
        }

        order.setSubtotal(subtotal);

        // Tính tổng tiền cuối cùng
        BigDecimal total = subtotal
                .add(order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO)
                .subtract(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO)
                .add(order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO);

        order.setTotalAmount(total);

        // Mã đơn hàng sẽ được trigger tự động tạo
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus, User confirmedBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setOrderStatus(newStatus);

        if (newStatus == Order.OrderStatus.CONFIRMED) {
            order.setConfirmedBy(confirmedBy);
            order.setConfirmedAt(LocalDateTime.now());
        } else if (newStatus == Order.OrderStatus.DELIVERED) {
            order.setCompletedAt(LocalDateTime.now());
            // Cập nhật số lượng đã bán
            for (OrderItem item : order.getOrderItems()) {
                productService.updateProductQuantity(item.getProduct().getId(), -item.getQuantity());
            }
        } else if (newStatus == Order.OrderStatus.CANCELLED) {
            // Hoàn trả số lượng sản phẩm
            for (OrderItem item : order.getOrderItems()) {
                productService.updateProductQuantity(item.getProduct().getId(), item.getQuantity());
            }
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getOrderStatus() != Order.OrderStatus.PENDING &&
                order.getOrderStatus() != Order.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getOrderStatus());
        }

        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        order.setCancelledReason(reason);

        // Hoàn trả số lượng sản phẩm
        for (OrderItem item : order.getOrderItems()) {
            productService.updateProductQuantity(item.getProduct().getId(), item.getQuantity());
        }

        return orderRepository.save(order);
    }

    public List<Order> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrdersBetweenDates(startDate, endDate);
    }

    public Page<Order> searchOrders(String orderCode, String customerName, String customerPhone,
                                    Order.OrderStatus status, Pageable pageable) {
        return orderRepository.searchOrders(orderCode, customerName, customerPhone, status, pageable);
    }

    public long countDeliveredOrders() {
        return orderRepository.countDeliveredOrders();
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
}