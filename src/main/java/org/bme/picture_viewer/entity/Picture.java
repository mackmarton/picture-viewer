package org.bme.picture_viewer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pictures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    @NotBlank
    @Size(max = 40)
    private String name;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private String cloudinaryPublicId;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser owner;

    @PrePersist
    public void prePersist() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}

