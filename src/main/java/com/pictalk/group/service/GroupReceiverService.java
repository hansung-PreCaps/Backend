package com.pictalk.group.service;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.group.domain.Group;
import com.pictalk.group.domain.GroupReceiver;
import com.pictalk.group.dto.GroupRequestDto.AddMemberRequest;
import com.pictalk.group.repository.GroupReceiverRepository;
import com.pictalk.group.repository.GroupRepository;
import com.pictalk.message.domain.Receiver;
import com.pictalk.message.repository.ReceiverRepository;
import com.pictalk.message.service.ReceiverService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupReceiverService {
    private final GroupReceiverRepository groupReceiverRepository;
    private final GroupRepository groupRepository;
    private final ReceiverRepository receiverRepository;
    private final ReceiverService receiverService;

    @Transactional
    public GroupReceiver addGroupReceiver(AddMemberRequest addMemberRequest) {
        Group group = groupRepository.findById(addMemberRequest.getGroupId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.GROUP_NOT_FOUND));

        // 사용자 조회
        Receiver receiver = receiverService.findOrCreateReceiver(addMemberRequest.getReceiverId(),
                addMemberRequest.getNickname(), addMemberRequest.getPhoneNumber());

        // 이미 추가된 사용자인지 확인 (중복 방지)
        boolean exists = groupReceiverRepository.findByGroupAndReceiver(group, receiver).isPresent();
        if (exists) {
            throw new GeneralException(ErrorStatus.GROUP_RECEIVER_ALREADY_EXISTS);
        }

        // GroupReceiver 생성
        GroupReceiver groupReceiver = GroupReceiver.builder()
                .group(group)
                .receiver(receiver)
                .build();

        // 양방향 관계 설정 (필수는 아님, 하지만 데이터 일관성을 위해 권장)
        group.addGroupReceiver(groupReceiver);

        // 저장
        return groupReceiverRepository.save(groupReceiver);
    }

    @Transactional
    public void deleteGroupReceiver(Long groupReceiverId) {
        groupReceiverRepository.deleteById(groupReceiverId);
    }

    @Transactional
    public void deleteGroupReceiversByGroupId(Long groupId) {
        List<GroupReceiver> groupReceivers = groupReceiverRepository.findAllByGroupId(groupId);
        groupReceiverRepository.deleteAll(groupReceivers);
    }

    public List<GroupReceiver> getGroupReceiversByGroupId(Long groupId) {
        return groupReceiverRepository.findAllByGroupId(groupId);
    }
}
