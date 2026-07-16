package com.ex.learninghub.modules.content.dto.response;

import com.ex.learninghub.modules.content.entity.Announcement;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnnouncementResponse {

    private Long id;
    private Long classId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static AnnouncementResponse from(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .classId(announcement.getClazz() != null ? announcement.getClazz().getId() : null)
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .createdAt(announcement.getCreatedAt())
                .build();
    }
}
