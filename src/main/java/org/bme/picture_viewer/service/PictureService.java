package org.bme.picture_viewer.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.bme.picture_viewer.entity.AppUser;
import org.bme.picture_viewer.entity.Picture;
import org.bme.picture_viewer.repository.PictureRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PictureService {

    private final Cloudinary cloudinary;
    private final PictureRepository pictureRepository;
    private final ApplicationContext applicationContext;

    public List<Picture> findAllSortedByName() {
        return pictureRepository.findAllByOrderByNameAsc();
    }

    public List<Picture> findAllSortedByDate() {
        return pictureRepository.findAllByOrderByUploadedAtDesc();
    }

    public Picture findById(Long id) {
        return pictureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kép nem található: " + id));
    }

    @Transactional
    public Picture upload(String name, MultipartFile file, AppUser owner) throws IOException {
        Map<String, Object> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "picture-viewer")
        );

        String publicId = (String) result.get("public_id");
        String secureUrl = (String) result.get("secure_url");

        Picture picture = Picture.builder()
                .name(name)
                .cloudinaryPublicId(publicId)
                .imageUrl(secureUrl)
                .owner(owner)
                .build();

        return pictureRepository.save(picture);
    }

    @Transactional
    public void delete(Long id) throws IOException {
        Picture picture = findById(id);

        cloudinary.uploader().destroy(
                picture.getCloudinaryPublicId(),
                ObjectUtils.emptyMap()
        );

        pictureRepository.delete(picture);
    }

    public Picture findByName(String name) {
        return pictureRepository.findByName(name);
    }

    @Transactional
    public void deleteAll() throws IOException {
        List<Picture> pictures = pictureRepository.findAll();
        for (Picture picture : pictures) {
            cloudinary.uploader().destroy(
                    picture.getCloudinaryPublicId(),
                    ObjectUtils.emptyMap()
            );
        }
        pictureRepository.deleteAllInBatch(pictures);
    }
}
