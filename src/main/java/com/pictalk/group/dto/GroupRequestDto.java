package com.pictalk.group.dto;

import java.util.List;
import lombok.Getter;

public class GroupRequestDto {
    @Getter
    public static class CreateGroupRequest {
        private String groupName;
    }

    @Getter
    public static class UpdateGroupRequest {
        private String groupName;
    }

    @Getter
    public static class AddMemberRequest {
        private Long groupId;
        private List<GroupReceiverDto> groupReceivers;
    }

    @Getter
    public static class GroupReceiverDto {
        private String nickname;
        private String phoneNumber;
    }

}
