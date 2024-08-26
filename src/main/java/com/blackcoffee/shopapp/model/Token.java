package com.blackcoffee.shopapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token", nullable = false, length =500)
    private String token;
    @Column(name = "token_type", length =50)
    private String tokenType;
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    private int revoked;
    private int expired;


    @Column(name = "device_type")
    private String deviceType;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
