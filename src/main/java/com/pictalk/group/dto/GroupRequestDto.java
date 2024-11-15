package com.pictalk.group.dto;

import lombok.Getter;

public class GroupRequestDto {
    @Getter
    public static class CreateGroupRequest {
        private Long userId;
        private String groupName;
    }

    @Getter
    public static class UpdateGroupRequest {
        private String groupName;
    }

    @Getter
    public static class AddMemberRequest {
        private Long groupId;
        private Long receiverId;
        private String nickname;
        private String phoneNumber;
    }

}
