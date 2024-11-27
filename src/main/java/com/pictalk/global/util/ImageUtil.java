package com.pictalk.global.util;

import com.google.common.base.Enums;
import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.global.vo.Image;
import com.pictalk.global.vo.ImageType;
import com.pictalk.message.dto.MessageRequestDto.FileDto;
import com.pictalk.message.dto.MessageRequestDto.Resend.ResendFile;
import java.util.Base64;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class ImageUtil {

    public static FileDto convertMultipartFileToFileDto(MultipartFile file) {
        String name = file.getOriginalFilename();
        Long size = getSize(file);
        String data = encodeImageToBase64(getBytes(file));

        return FileDto.builder()
                .name(name)
                .size(size)
                .data(data)
                .build();
    }

    public static Long getSize(MultipartFile file) {
        return file.getSize();
    }

    public static byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FILE_READ_ERROR);
        }
    }

    private static String encodeImageToBase64(byte[] fileBytes) {
        // Base64 인코딩
        return Base64.getEncoder().encodeToString(fileBytes);

    }

    public static Image convertMultipartToImage(MultipartFile multipartFile) {
        final String originalName = multipartFile.getOriginalFilename();
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

    public static ResendFile convertMultipartFileToResendFile(Image image) {
        return ResendFile.builder()
                .fileKey(image.getImageUUID())
                .name(image.getImageName())
                .fileType(image.getImageType().toString())
                .fileUrl(image.getImageUrl())
                .build();
    }
}
