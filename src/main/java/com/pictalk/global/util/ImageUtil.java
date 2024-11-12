package com.pictalk.global.util;

import com.google.common.base.Enums;
import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.global.vo.Image;
import com.pictalk.global.vo.ImageType;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class ImageUtil {

    public static Image convertMultipartToImage(MultipartFile file) {

        final String originalName = file.getOriginalFilename();
        final String name = FilenameUtils.getBaseName(originalName);
        final String type = FilenameUtils.getExtension(originalName).toUpperCase();

        if (!Enums.getIfPresent(ImageType.class, type).isPresent()) {
            throw new GeneralException(ErrorStatus.NOT_SUPPORTED_IMAGE_TYPE);
        }

        return Image.builder()
                .imageType(ImageType.valueOf(type))
                .imageName(name)
                .imageUUID(UUID.randomUUID().toString())
                .build();
    }
}
