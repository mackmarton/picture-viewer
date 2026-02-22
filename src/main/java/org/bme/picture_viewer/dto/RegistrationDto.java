package org.bme.picture_viewer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationDto {

    @NotBlank(message = "A felhasználónév kötelező")
    private String username;

    @NotBlank(message = "Az e-mail cím kötelező")
    @Email(message = "Érvénytelen e-mail cím")
    private String email;

    @NotBlank(message = "A jelszó kötelező")
    private String password;

    @NotBlank(message = "A jelszó megerősítése kötelező")
    private String confirmPassword;
}

