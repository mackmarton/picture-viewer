package org.bme.picture_viewer.repository;

import org.bme.picture_viewer.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findAllByOrderByNameAsc();
    List<Picture> findAllByOrderByUploadedAtDesc();
    Picture findByName(String name);
}

