package com.pictalk.group.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.group.domain.Group;
import com.pictalk.group.dto.GroupRequestDto.CreateGroupRequest;
import com.pictalk.group.dto.GroupRequestDto.UpdateGroupRequest;
import com.pictalk.group.repository.GroupRepository;
import com.pictalk.user.domain.User;
import com.pictalk.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public Group createGroup(CreateGroupRequest createGroupRequest, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Group group = Group.builder()
                .user(user)
                .name(createGroupRequest.getGroupName())
                .build();

        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GROUP_NOT_FOUND));

        group.softDelete();
    }

    public List<Group> getAllGroupsByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return groupRepository.findAllByUser(user);
    }

    public void updateGroup(Long groupId, UpdateGroupRequest updateGroupRequest) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GROUP_NOT_FOUND));

        group.update(updateGroupRequest.getGroupName());
    }
}
