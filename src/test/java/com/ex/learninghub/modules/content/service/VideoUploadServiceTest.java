package com.ex.learninghub.modules.content.service;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Chapter;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.ChapterRepository;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.storage.service.CloudinaryUploadResult;
import com.ex.learninghub.modules.storage.service.FileStorageRouterService;
import com.ex.learninghub.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.unit.DataSize;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoUploadServiceTest {

    @Mock private LessonRepository lessonRepository;
    @Mock private ClazzRepository clazzRepository;
    @Mock private ChapterRepository chapterRepository;
    @Mock private FileStorageRouterService storageRouter;

    @InjectMocks private VideoUploadService service;

    private Lesson lesson;
    private MockMultipartFile file;
    private UserPrincipal lecturerPrincipal;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "maxVideoSize", DataSize.ofMegabytes(200));
        lesson = Lesson.builder()
                .chapterId(3L)
                .videoUrl("https://cdn.example/old.mp4")
                .duration(90)
                .build();
        lesson.setId(2L);
        file = new MockMultipartFile("file", "lesson.mp4", "video/mp4", new byte[]{1, 2, 3});

        User lecturer = User.builder().role(Role.LECTURER).build();
        lecturer.setId(5L);
        lecturerPrincipal = new UserPrincipal(lecturer);

        Chapter chapter = Chapter.builder().clazzId(4L).build();
        chapter.setId(3L);
        Clazz clazz = Clazz.builder().lecturer(lecturer).build();
        clazz.setId(4L);
        when(lessonRepository.findById(2L)).thenReturn(Optional.of(lesson));
        when(chapterRepository.findById(3L)).thenReturn(Optional.of(chapter));
        when(clazzRepository.findById(4L)).thenReturn(Optional.of(clazz));
    }

    @Test
    void uploadFailureDoesNotChangeOrSaveLessonVideo() {
        when(storageRouter.uploadVideo(file)).thenThrow(new IllegalStateException("Cloudinary unavailable"));

        assertThatThrownBy(() -> service.uploadLessonVideo(2L, file, lecturerPrincipal))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.VIDEO_UPLOAD_FAILED);

        assertThat(lesson.getVideoUrl()).isEqualTo("https://cdn.example/old.mp4");
        assertThat(lesson.getDuration()).isEqualTo(90);
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void successfulVideoUploadStoresUrlAndDurationInSeconds() {
        when(storageRouter.uploadVideo(file))
                .thenReturn(new CloudinaryUploadResult("https://cdn.example/new.mp4", 121));

        String result = service.uploadLessonVideo(2L, file, lecturerPrincipal);

        assertThat(result).isEqualTo("https://cdn.example/new.mp4");
        assertThat(lesson.getVideoUrl()).isEqualTo(result);
        assertThat(lesson.getDuration()).isEqualTo(121);
        verify(lessonRepository).save(lesson);
    }
}
