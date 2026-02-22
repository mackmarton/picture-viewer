package org.bme.picture_viewer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bme.picture_viewer.dto.PictureResponseDto;
import org.bme.picture_viewer.dto.PictureUploadDto;
import org.bme.picture_viewer.entity.AppUser;
import org.bme.picture_viewer.entity.Picture;
import org.bme.picture_viewer.service.AppUserService;
import org.bme.picture_viewer.service.PictureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pictures")
@RequiredArgsConstructor
public class PictureController {

    private final PictureService pictureService;
    private final AppUserService userService;

    @GetMapping
    public List<PictureResponseDto> listPictures(@RequestParam(defaultValue = "name") String sort) {
        List<Picture> pictures = "date".equals(sort)
                ? pictureService.findAllSortedByDate()
                : pictureService.findAllSortedByName();
        return pictures.stream().map(PictureResponseDto::from).toList();
    }

    @GetMapping("/{id}")
    public PictureResponseDto getPicture(@PathVariable Long id) {
        return PictureResponseDto.from(pictureService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PictureResponseDto> uploadPicture(
            @Valid @ModelAttribute PictureUploadDto dto,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        AppUser owner = userService.findByUsername(userDetails.getUsername());
        Picture saved = pictureService.upload(dto.getName(), file, owner);
        return ResponseEntity.status(HttpStatus.CREATED).body(PictureResponseDto.from(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePicture(@PathVariable Long id) throws IOException {
        pictureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
