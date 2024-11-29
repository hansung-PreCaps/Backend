package com.pictalk.global.util;

import com.pictalk.infra.S3Uploader;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UrlImageDownloadUtil {

    private final S3Uploader s3Uploader;
    private final ResourceLoader resourceLoader;

    public String convertUrlToS3Url(String imageUrl) throws IOException {
        File file = downloadImage(imageUrl).getFile();
        String s3Url;
        s3Url = s3Uploader.putS3(file, String.valueOf((int) (Math.random() * 1000000)));
        return s3Url;
    }

    private Resource downloadImage(String imageUrl) {
        return resourceLoader.getResource(imageUrl);
    }
}
