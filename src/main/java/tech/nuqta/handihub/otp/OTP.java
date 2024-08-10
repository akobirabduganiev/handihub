package tech.nuqta.handihub.otp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.nuqta.handihub.user.entity.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "otp")
public class OTP {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String OTP;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime validatedAt;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
