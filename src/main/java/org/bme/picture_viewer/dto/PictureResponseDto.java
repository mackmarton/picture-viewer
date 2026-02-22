package org.bme.picture_viewer.dto;

import org.bme.picture_viewer.entity.Picture;

import java.time.LocalDateTime;

public record PictureResponseDto(Long id, String name, LocalDateTime uploadedAt, String imageUrl,
                                 String ownerUsername) {
    public static PictureResponseDto from(Picture picture) {
        return new PictureResponseDto(
                picture.getId(),
                picture.getName(),
                picture.getUploadedAt(),
                picture.getImageUrl(),
                picture.getOwner().getUsername()
        );
    }
}

