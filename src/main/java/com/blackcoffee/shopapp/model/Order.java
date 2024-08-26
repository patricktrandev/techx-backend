package com.blackcoffee.shopapp.model;

import com.blackcoffee.shopapp.model.BaseEntity;
import com.blackcoffee.shopapp.model.Coupon;
import com.blackcoffee.shopapp.model.DetailsOrder;
import com.blackcoffee.shopapp.model.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "fullname", length = 100)
    private String fullName;
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "note", length = 150)
    private String note;
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    @Column(name = "status")
    private String orderStatus;
    @Column(name = "total_payment")
    private Float totalPayment;
    @Column(name = "shippingMethod", length = 100)
    private String shippingMethod;
    @Column(name = "shippingAddress", length = 100)
    private String shippingAddress;
    @Column(name = "shippingDate")
    private LocalDateTime shippingDate;
    @Column(name = "trackingNumber", length = 100)
    private String trackingNumber;
    @Column(name = "paymentMethod", length = 100)
    private String paymentMethod;
    @Column(name = "isActive")
    private int isActive;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<DetailsOrder> orderDetails;
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    @JsonManagedReference
    private Coupon coupon;
}
