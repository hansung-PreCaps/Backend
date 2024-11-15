package com.pictalk.group.controller;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.global.payload.status.SuccessStatus;
import com.pictalk.group.domain.GroupReceiver;
import com.pictalk.group.dto.GroupRequestDto.AddMemberRequest;
import com.pictalk.group.dto.GroupResponseDto;
import com.pictalk.group.service.GroupReceiverService;
import com.pictalk.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group-receivers")
public class GroupReceiverController {
    private final GroupReceiverService groupReceiverService;
    private final GroupService groupService;

    // 그룹 멤버 추가
    @PostMapping
    public CommonResponse<Object> addGroupReceiver(@RequestBody AddMemberRequest addMemberRequest) {
        groupReceiverService.addGroupReceiver(addMemberRequest);
        return CommonResponse.of(SuccessStatus.GROUP_RECEIVER_ADDED, null);
    }

    // 그룹 멤버 삭제
    @DeleteMapping("/{group_receiver_id}")
    public CommonResponse<Object> deleteGroupReceiver(@PathVariable("group_receiver_id") Long groupReceiverId) {
        groupReceiverService.deleteGroupReceiver(groupReceiverId);
        return CommonResponse.of(SuccessStatus.GROUP_RECEIVER_DELETED, null);
    }

    // 그룹 멤버 조회
    @Operation(summary = "그룹 리시버 조회")
    @GetMapping("/{group_id}")
    public CommonResponse<GroupResponseDto.GroupDetailResponse> getGroupReceivers(@PathVariable("group_id") Long groupId) {
        List<GroupReceiver> groupReceivers = groupReceiverService.getGroupReceiversByGroupId(groupId);

        List<GroupResponseDto.GroupReceiverResponse> groupReceiverResponses = groupReceivers.stream()
                .map(groupReceiver -> GroupResponseDto.GroupReceiverResponse.builder()
                        .groupReceiverId(groupReceiver.getId())
                        .nickname(groupReceiver.getReceiver().getNickname()) // receiver 대신 user 사용
                        .phoneNumber(groupReceiver.getReceiver().getPhoneNumber()) // receiver 대신 user 사용
                        .build())
                .collect(Collectors.toList());

        GroupResponseDto.GroupDetailResponse groupDetailResponse = GroupResponseDto.GroupDetailResponse.builder()
                .groupReceivers(groupReceiverResponses)
                .build();

        return CommonResponse.of(SuccessStatus.GROUP_RECEIVERS_FOUND, groupDetailResponse);
    }
}
