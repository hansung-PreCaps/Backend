package com.pictalk.message.service;

import com.pictalk.global.vo.Image;
import com.pictalk.infra.S3Uploader;
import com.pictalk.message.domain.Message;
import com.pictalk.message.domain.MessageImage;
import com.pictalk.message.repository.MessageImageRepository;
import com.pictalk.message.repository.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MessageImageService {
    private final S3Uploader uploader;
    private final MessageImageRepository messageImageRepository;
    private final MessageRepository messageRepository;

//    @Transactional
    public void createFiletoImage(Message message, MultipartFile imageFile) {
        final Image image = uploader.uploadImage(imageFile);
        createImage(message, image);
    }

    public void createImage(Message message, Image image) {
        MessageImage messageImage = new MessageImage(message, image);
        messageImageRepository.save(messageImage);
        message.add(messageImage);
    }

    @Transactional
    public void deleteAllByMessage(Message message) {
        messageImageRepository.findAllByMessage(message);
    }

    @Transactional(readOnly = true)
    public List<MessageImage> getImage(Message message) {
        message.getMessageImages();
        List<MessageImage> messageImages = messageImageRepository.findAllByMessage(message);
        return messageImages;
    }

    @Transactional
    public void deleteImage(MessageImage messageImage) {
        messageImageRepository.delete(messageImage);
    }
}
