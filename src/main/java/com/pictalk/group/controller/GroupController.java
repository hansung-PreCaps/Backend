package com.pictalk.group.controller;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.global.payload.status.SuccessStatus;
import com.pictalk.group.domain.Group;
import com.pictalk.group.dto.GroupRequestDto.CreateGroupRequest;
import com.pictalk.group.dto.GroupRequestDto.UpdateGroupRequest;
import com.pictalk.group.service.GroupReceiverService;
import com.pictalk.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {
    private final GroupService groupService;
    private final GroupReceiverService groupReceiverService;

    @PostMapping
    public CommonResponse<Object> createGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        Group group = groupService.createGroup(createGroupRequest);
        return CommonResponse.of(SuccessStatus.GROUP_CREATED, null);
    }

    @DeleteMapping("/{group_id}")
    public CommonResponse<Object> deleteGroup(@PathVariable("group_id") Long groupId) {
        groupService.deleteGroup(groupId);
        return CommonResponse.of(SuccessStatus.GROUP_DELETED, null);
    }

    @PatchMapping("/{group_id}")
    public CommonResponse<Object> updateGroup(@PathVariable("group_id") Long groupId, @RequestBody UpdateGroupRequest updateGroupRequest) {
        groupService.updateGroup(groupId, updateGroupRequest);
        return CommonResponse.of(SuccessStatus.GROUP_UPDATED, null);
    }

}
