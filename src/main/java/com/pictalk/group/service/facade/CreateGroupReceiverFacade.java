package com.pictalk.group.service.facade;

import com.pictalk.global.exception.GeneralException;
import com.pictalk.global.payload.status.ErrorStatus;
import com.pictalk.group.domain.Group;
import com.pictalk.group.domain.GroupReceiver;
import com.pictalk.group.dto.GroupRequestDto.AddMemberRequest;
import com.pictalk.group.repository.GroupRepository;
import com.pictalk.message.domain.Receiver;
import com.pictalk.message.service.ReceiverService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateGroupReceiverFacade {
    private final ReceiverService receiverService;
    private final GroupRepository groupRepository;

    @Transactional
    public void addGroupReceiver(AddMemberRequest addMemberRequest) {

        // Group 조회
        Group group = groupRepository.findById(addMemberRequest.getGroupId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.GROUP_NOT_FOUND));

        // GroupReceiver 생성
        List<Receiver> receivers = receiverService
                .createReceivers(addMemberRequest.getGroupReceivers());

        // 양방향 관계 설정 (필수는 아님, 하지만 데이터 일관성을 위해 권장)
        receivers.forEach(receiver -> {
            GroupReceiver groupReceiver = new GroupReceiver(group, receiver);
            group.addGroupReceiver(groupReceiver);
        });
    }
}
