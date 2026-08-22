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
    ENROLLMENT_EXISTS(3001, "User is already enrolled in this course", HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_FOUND(3002, "Enrollment not found", HttpStatus.NOT_FOUND),
    PROGRESS_NOT_FOUND(3003, "Progress record not found", HttpStatus.NOT_FOUND),
    MENTOR_REQUEST_NOT_FOUND(4001, "Mentor request not found", HttpStatus.NOT_FOUND),
    MENTOR_REQUEST_PENDING(4002, "There is already a pending request to become a mentor", HttpStatus.BAD_REQUEST),
    REVIEW_EXISTS(5001, "You have already reviewed this course", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND(5002, "Review not found", HttpStatus.NOT_FOUND),
    CHAT_ROOM_NOT_FOUND(6001, "Chat room not found", HttpStatus.NOT_FOUND),
    EXCEL_PARSE_ERROR(7001, "Failed to parse Excel file. Please check the file format", HttpStatus.BAD_REQUEST),
    CLAZZ_NOT_FOUND(2004, "Class not found", HttpStatus.NOT_FOUND),
    CLAZZ_ALREADY_EXISTS(2005, "Class code already exists", HttpStatus.BAD_REQUEST),
    COURSE_ALREADY_EXISTS(2006, "Course code already exists", HttpStatus.BAD_REQUEST),
    ASSIGNMENT_NOT_FOUND(3004, "Assignment not found", HttpStatus.NOT_FOUND),
    SUBMISSION_EXISTS(3005, "You have already submitted this assignment", HttpStatus.BAD_REQUEST),
    SUBMISSION_NOT_FOUND(3006, "Submission not found", HttpStatus.NOT_FOUND),
    ANNOUNCEMENT_NOT_FOUND(3007, "Announcement not found", HttpStatus.NOT_FOUND),
    ADMIN_CLASS_NOT_FOUND(2007, "Administrative class not found", HttpStatus.NOT_FOUND),
    ADMIN_CLASS_ALREADY_EXISTS(2008, "Administrative class already exists", HttpStatus.BAD_REQUEST),
    
    // SRS Online Learning Platform Errors
    COURSE_NOT_PUBLISHED(2009, "Course is not published yet", HttpStatus.BAD_REQUEST),
    LESSON_LOCKED(2010, "Lesson is locked. Complete previous lessons first", HttpStatus.BAD_REQUEST),
    COURSE_NOT_OWNED_BY_MENTOR(2011, "You do not own this course", HttpStatus.FORBIDDEN),
    USER_NOT_ENROLLED(3008, "User is not enrolled in this course", HttpStatus.BAD_REQUEST),
    ENROLLMENT_ALREADY_COMPLETED(3009, "Enrollment is already completed", HttpStatus.BAD_REQUEST),
    MENTOR_REQUEST_ALREADY_EXISTS(4003, "You already have a mentor request", HttpStatus.BAD_REQUEST),
    MENTOR_REQUEST_ALREADY_PROCESSED(4004, "Mentor request already processed", HttpStatus.BAD_REQUEST),
    CANNOT_REVIEW_NOT_COMPLETED(5003, "You must complete the course before reviewing", HttpStatus.BAD_REQUEST),
    INVALID_RATING(5004, "Rating must be between 1 and 5", HttpStatus.BAD_REQUEST),
    QUIZ_NOT_FOUND(3010, "Quiz not found", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND(3011, "Question not found", HttpStatus.NOT_FOUND),
    ATTENDANCE_NOT_QUALIFIED(4030, "Student does not meet attendance requirement", HttpStatus.BAD_REQUEST),
    CLAZZ_FULL(4031, "Class has reached maximum student capacity", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
