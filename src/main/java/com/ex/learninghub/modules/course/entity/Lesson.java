package com.ex.learninghub.modules.course.entity;

import jakarta.persistence.*;
import lombok.*;
import com.ex.learninghub.common.model.BaseEntity;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "video_url")
    private String videoUrl;

    private Integer duration;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
