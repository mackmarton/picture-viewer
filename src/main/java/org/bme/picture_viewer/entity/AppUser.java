package org.bme.picture_viewer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String username;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @Column(nullable = false)
    @NotBlank
    private String email;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Picture> pictures = new ArrayList<>();
}

