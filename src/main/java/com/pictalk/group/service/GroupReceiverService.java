package com.pictalk.group.service;

import com.pictalk.group.domain.GroupReceiver;
import com.pictalk.group.repository.GroupReceiverRepository;
import com.pictalk.group.repository.GroupRepository;
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
