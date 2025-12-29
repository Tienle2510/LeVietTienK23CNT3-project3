// Order.java
package K23CNT3.LeVietTien.project3.TcoffeeT.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String customerNote;

    private Double totalAmount;
    private String status; // PENDING, PROCESSING, COMPLETED, CANCELLED

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    private LocalDateTime orderDate;
    private LocalDateTime createdAt;

    // Getters và Setters
    // Constructor
}

