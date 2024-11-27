package com.pictalk.group.controller;

import com.pictalk.global.payload.response.CommonResponse;
import com.pictalk.global.payload.status.SuccessStatus;
import com.pictalk.group.domain.Group;
import com.pictalk.group.dto.GroupRequestDto.CreateGroupRequest;
import com.pictalk.group.dto.GroupRequestDto.UpdateGroupRequest;
import com.pictalk.group.dto.GroupResponseDto.CreateGroupResponse;
import com.pictalk.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Operation(summary = "그룹 생성")
    @PostMapping
    public CommonResponse<Object> createGroup(@AuthenticationPrincipal UserDetails authenticatedPrincipal,
                                              @RequestBody CreateGroupRequest createGroupRequest) {
        String userEmail = authenticatedPrincipal.getUsername();
        Group group = groupService.createGroup(createGroupRequest, userEmail);

        return CommonResponse.of(SuccessStatus.GROUP_CREATED, CreateGroupResponse.builder()
                .groupId(group.getId())
                .build());
    }

    @Operation(summary = "그룹 삭제")
    @DeleteMapping("/{group_id}")
    public CommonResponse<Object> deleteGroup(@PathVariable("group_id") Long groupId) {
        groupService.deleteGroup(groupId);
        return CommonResponse.of(SuccessStatus.GROUP_DELETED, null);
    }

    @Operation(summary = "그룹 이름 수정")
    @PatchMapping("/{group_id}")
    public CommonResponse<Object> updateGroup(@PathVariable("group_id") Long groupId,
                                              @RequestBody UpdateGroupRequest updateGroupRequest) {
        groupService.updateGroup(groupId, updateGroupRequest);
        return CommonResponse.of(SuccessStatus.GROUP_UPDATED, null);
    }
}
