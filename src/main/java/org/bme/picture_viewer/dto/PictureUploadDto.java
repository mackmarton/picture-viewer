package org.bme.picture_viewer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PictureUploadDto {

    @NotBlank(message = "A kép neve kötelező")
    @Size(max = 40, message = "A kép neve legfeljebb 40 karakter lehet")
    private String name;
}

