package com.ex.learninghub.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // General Errors
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    KEY_INVALID(999, "Invalid message key", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1000, "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1001, "You do not have permission to access this resource", HttpStatus.FORBIDDEN),
    
    // Validation Errors
    EMAIL_REQUIRED(1002, "Email is required", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS(1003, "Email already exists", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(1004, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1005, "User not found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(1006, "Invalid email or password", HttpStatus.BAD_REQUEST),
    
    // Module specific errors
    COURSE_NOT_FOUND(2001, "Course not found", HttpStatus.NOT_FOUND),
    CHAPTER_NOT_FOUND(2002, "Chapter not found", HttpStatus.NOT_FOUND),
    LESSON_NOT_FOUND(2003, "Lesson not found", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_EXISTS(2006, "Course code already exists", HttpStatus.BAD_REQUEST),
    CLAZZ_NOT_FOUND(2004, "Class not found", HttpStatus.NOT_FOUND),
    CLAZZ_ALREADY_EXISTS(2005, "Class code already exists", HttpStatus.BAD_REQUEST),
    ADMIN_CLASS_NOT_FOUND(2007, "Administrative class not found", HttpStatus.NOT_FOUND),
    ADMIN_CLASS_ALREADY_EXISTS(2008, "Administrative class already exists", HttpStatus.BAD_REQUEST),
    
    ENROLLMENT_EXISTS(3001, "User is already enrolled in this course", HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_FOUND(3002, "Enrollment not found", HttpStatus.NOT_FOUND),
    PROGRESS_NOT_FOUND(3003, "Progress record not found", HttpStatus.NOT_FOUND),
    ASSIGNMENT_NOT_FOUND(3004, "Assignment not found", HttpStatus.NOT_FOUND),
    SUBMISSION_EXISTS(3005, "You have already submitted this assignment", HttpStatus.BAD_REQUEST),
    SUBMISSION_NOT_FOUND(3006, "Submission not found", HttpStatus.NOT_FOUND),
    ANNOUNCEMENT_NOT_FOUND(3007, "Announcement not found", HttpStatus.NOT_FOUND),
    USER_NOT_ENROLLED(3008, "User is not enrolled in this course", HttpStatus.BAD_REQUEST),
    QUIZ_NOT_FOUND(3010, "Quiz not found", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND(3011, "Question not found", HttpStatus.NOT_FOUND),
    QUIZ_ALREADY_ATTEMPTED(3012, "You have already attempted this quiz", HttpStatus.BAD_REQUEST),
    SCORE_EXCEEDS_MAX(3013, "Score exceeds the maximum allowed score", HttpStatus.BAD_REQUEST),
    QUIZ_TIME_EXCEEDED(3014, "Quiz submission time limit exceeded", HttpStatus.BAD_REQUEST),
    LESSON_NOT_IN_ENROLLMENT(3016, "Lesson does not belong to the clazz of this enrollment", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_FOUND(3017, "Notification not found", HttpStatus.NOT_FOUND),
    FORUM_POST_NOT_FOUND(3018, "Forum post not found", HttpStatus.NOT_FOUND),
    VIDEO_FILE_EMPTY(3019, "Video file is empty", HttpStatus.BAD_REQUEST),
    VIDEO_TOO_LARGE(3020, "Video file exceeds maximum allowed size", HttpStatus.PAYLOAD_TOO_LARGE),
    VIDEO_INVALID_FORMAT(3021, "Unsupported video format", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    VIDEO_UPLOAD_FAILED(3022, "Failed to upload video to storage", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REFRESH_TOKEN(4033, "Invalid or expired refresh token", HttpStatus.UNAUTHORIZED),
    
    ATTENDANCE_NOT_QUALIFIED(4030, "Student does not meet attendance requirement", HttpStatus.BAD_REQUEST),
    CLAZZ_FULL(4031, "Class has reached maximum student capacity", HttpStatus.BAD_REQUEST),
    INVALID_RESET_TOKEN(4032, "Invalid or expired password reset token", HttpStatus.BAD_REQUEST),

    SCHEDULE_NOT_FOUND(4001, "Schedule not found", HttpStatus.NOT_FOUND),
    SCHEDULE_CONFLICT(4002, "Schedule conflicts with existing entry", HttpStatus.BAD_REQUEST),
    ROOM_CONFLICT(4006, "Room is already booked for another class at this time slot", HttpStatus.BAD_REQUEST),
    REGISTRATION_CLOSED(4003, "Registration period is not open", HttpStatus.BAD_REQUEST),
    CREDIT_LIMIT_EXCEEDED(4004, "Total credits exceed registration limit", HttpStatus.BAD_REQUEST),
    PREREQUISITE_NOT_MET(4005, "Prerequisite course has not been completed", HttpStatus.BAD_REQUEST),
    GRADING_POLICY_WEIGHTS_INVALID(5001, "Grading policy weights must sum to 1.000", HttpStatus.BAD_REQUEST),
    GPA_SCALE_INVALID(5002, "GPA scale rules are invalid", HttpStatus.BAD_REQUEST),
    EXCEL_PARSE_ERROR(7001, "Failed to parse Excel file. Please check the file format", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
