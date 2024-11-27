package com.pictalk.group.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class GroupResponseDto {
    @Getter
    @Builder
    public static class SearchGroupResponse {
        private Long groupId;
        private String groupName;
    }

    @Getter
    @Builder
    public static class CreateGroupResponse {
        private Long groupId;
    }

    @Getter
    @Builder
    public static class GroupDetailResponse {
        private List<GroupReceiverResponse> groupReceivers;
    }

    @Getter
    @Builder
    public static class GroupReceiverResponse {
        private Long groupReceiverId;
        private String nickname;
        private String phoneNumber;
    }
}
