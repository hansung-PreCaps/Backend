package com.pictalk.infra;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.global.util.ImageUtil;
import com.pictalk.global.vo.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Image uploadImage(MultipartFile multipartFile) {
        final Image image = ImageUtil.convertMultipartToImage(multipartFile);
        final String filename = convertToFilename("images", image);
        final String url = upload(multipartFile, filename);
        image.setUrl(url);
        return image;
    }

    public void deleteImage(Image image, String dirName) {
        if (image.getImageUUID().equals("base-UUID")) {
            return;
        }
        final String filename = convertToFilename(dirName, image);
        deleteS3(filename);
    }

    private String upload(MultipartFile multipartFile, String filename) {
        final File localFile = convertMultipartFileToLocalFile(multipartFile);
        final String uploadImageUrl = putS3(localFile, filename);
        deleteLocalFile(localFile);
        return uploadImageUrl;
    }

    private String convertToFilename(String dirName, Image image) {
        return convertToFilename(dirName, image.getImageUUID(), image.getImageName(), image.getImageType().toString());
    }

    private String convertToFilename(String dirName, String UUID, String name, String type) {
        return dirName + "/" + UUID + "_" + name + "." + type;
    }

    public String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }


    private void deleteS3(String filename) {
        amazonS3Client.deleteObject(bucket, filename);
    }

    private void deleteLocalFile(File targetFile) {
        if (targetFile.delete()) {
            return;
        }
        log.error("Local File delete fail : " + targetFile.getName());
    }

    private File convertMultipartFileToLocalFile(MultipartFile file) {
        try {
            String currentPath = Paths.get("").toAbsolutePath().toString();

            // 현재 경로에 업로드 폴더와 파일 이름 결합
            String pathname = currentPath + "/upload/" + file.getOriginalFilename();
            final File convertFile = new File(pathname);
            if (convertFile.createNewFile()) {
                log.info("create local file: " + pathname);
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(file.getBytes());
                }
                log.info("complete write to local file");
                return convertFile;
            }
            throw new GeneralException(ErrorStatus.FILE_CONVERT_FAIL);
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.FILE_CONVERT_FAIL);
        }
    }

}
