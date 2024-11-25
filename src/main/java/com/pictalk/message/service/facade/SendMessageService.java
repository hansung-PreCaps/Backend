package com.pictalk.message.service.facade;

import com.pictalk.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

public interface SendMessageService<T, R> {
    R sendMessage(T request, User user, MultipartFile image);
}
