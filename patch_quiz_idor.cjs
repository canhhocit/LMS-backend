const fs = require('fs');

// 1. Update QuizService.java
let svc = fs.readFileSync('src/main/java/com/ex/learninghub/modules/quiz/service/QuizService.java', 'utf8');
svc = svc.replace('QuizResponse getQuizById(Long quizId);', 'QuizResponse getQuizById(Long quizId, UserPrincipal userPrincipal);');
svc = svc.replace('List<QuestionResponse> getQuestionsByQuizId(Long quizId);', 'List<QuestionResponse> getQuestionsByQuizId(Long quizId, UserPrincipal userPrincipal);');
fs.writeFileSync('src/main/java/com/ex/learninghub/modules/quiz/service/QuizService.java', svc);

// 2. Update QuizController.java
let ctrl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/quiz/controller/QuizController.java', 'utf8');
ctrl = ctrl.replace(
    'public ApiResponse<QuizResponse> getQuiz(@PathVariable Long quizId) {',
    'public ApiResponse<QuizResponse> getQuiz(@PathVariable Long quizId, @AuthenticationPrincipal UserPrincipal userPrincipal) {'
);
ctrl = ctrl.replace(
    'return ApiResponse.success(quizService.getQuizById(quizId));',
    'return ApiResponse.success(quizService.getQuizById(quizId, userPrincipal));'
);
ctrl = ctrl.replace(
    'public ApiResponse<List<QuestionResponse>> getQuestionsByQuiz(@PathVariable Long quizId) {',
    'public ApiResponse<List<QuestionResponse>> getQuestionsByQuiz(@PathVariable Long quizId, @AuthenticationPrincipal UserPrincipal userPrincipal) {'
);
ctrl = ctrl.replace(
    'return ApiResponse.success(quizService.getQuestionsByQuizId(quizId));',
    'return ApiResponse.success(quizService.getQuestionsByQuizId(quizId, userPrincipal));'
);
fs.writeFileSync('src/main/java/com/ex/learninghub/modules/quiz/controller/QuizController.java', ctrl);

// 3. Update QuizServiceImpl.java
let impl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/quiz/service/impl/QuizServiceImpl.java', 'utf8');

// Add authorization helper
const authHelper = `
    private void checkAuthorization(Quiz quiz, UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new AppException(com.ex.learninghub.common.exception.ErrorCode.UNAUTHORIZED);
        }
        com.ex.learninghub.common.enums.Role role = userPrincipal.getUser().getRole();
        if (role == com.ex.learninghub.common.enums.Role.ADMIN) {
            return;
        }
        if (role == com.ex.learninghub.common.enums.Role.LECTURER) {
            if (!quiz.getClazz().getLecturer().getId().equals(userPrincipal.getUser().getId())) {
                throw new AppException(com.ex.learninghub.common.exception.ErrorCode.FORBIDDEN);
            }
            return;
        }
        if (role == com.ex.learninghub.common.enums.Role.STUDENT) {
            if (!enrollmentRepository.existsByStudentIdAndClazzId(userPrincipal.getUser().getId(), quiz.getClazz().getId())) {
                throw new AppException(com.ex.learninghub.common.exception.ErrorCode.FORBIDDEN);
            }
            return;
        }
        throw new AppException(com.ex.learninghub.common.exception.ErrorCode.FORBIDDEN);
    }
`;

if (!impl.includes('checkAuthorization')) {
    impl = impl.replace(
        'public class QuizServiceImpl implements QuizService {',
        'public class QuizServiceImpl implements QuizService {' + authHelper
    );
}

// Update getQuizById
impl = impl.replace(
    'public QuizResponse getQuizById(Long quizId) {',
    'public QuizResponse getQuizById(Long quizId, UserPrincipal userPrincipal) {'
);
if (impl.includes('Quiz quiz = findQuizOrThrow(quizId);')) {
    impl = impl.replace(
        'Quiz quiz = findQuizOrThrow(quizId);',
        'Quiz quiz = findQuizOrThrow(quizId);\n        checkAuthorization(quiz, userPrincipal);'
    );
}

// Update getQuizzesByClassId
// Need to check class authorization too!
if (impl.includes('public List<QuizResponse> getQuizzesByClassId(Long classId, UserPrincipal userPrincipal) {')) {
    const classAuthHelper = `
        // Check authorization
        if (userPrincipal.getUser().getRole() == com.ex.learninghub.common.enums.Role.STUDENT) {
            if (!enrollmentRepository.existsByStudentIdAndClazzId(userPrincipal.getUser().getId(), classId)) {
                throw new AppException(com.ex.learninghub.common.exception.ErrorCode.FORBIDDEN);
            }
        } else if (userPrincipal.getUser().getRole() == com.ex.learninghub.common.enums.Role.LECTURER) {
            com.ex.learninghub.modules.course.entity.Clazz clazz = clazzRepository.findById(classId).orElseThrow(() -> new AppException(com.ex.learninghub.common.exception.ErrorCode.CLAZZ_NOT_FOUND));
            if (!clazz.getLecturer().getId().equals(userPrincipal.getUser().getId())) {
                throw new AppException(com.ex.learninghub.common.exception.ErrorCode.FORBIDDEN);
            }
        }
`;
    impl = impl.replace(
        'public List<QuizResponse> getQuizzesByClassId(Long classId, UserPrincipal userPrincipal) {',
        'public List<QuizResponse> getQuizzesByClassId(Long classId, UserPrincipal userPrincipal) {' + classAuthHelper
    );
}

// Update getQuestionsByQuizId
impl = impl.replace(
    'public List<QuestionResponse> getQuestionsByQuizId(Long quizId) {',
    'public List<QuestionResponse> getQuestionsByQuizId(Long quizId, UserPrincipal userPrincipal) {'
);
if (impl.includes('Quiz quiz = findQuizOrThrow(quizId);') && impl.indexOf('Quiz quiz = findQuizOrThrow(quizId);') !== impl.lastIndexOf('Quiz quiz = findQuizOrThrow(quizId);')) {
    // We need to replace the second instance (which is in getQuestionsByQuizId).
    // Let's use a regex to target getQuestionsByQuizId block
    const questionsBlockRegex = /public List<QuestionResponse> getQuestionsByQuizId\(Long quizId, UserPrincipal userPrincipal\) {[\s\S]*?Quiz quiz = findQuizOrThrow\(quizId\);/;
    impl = impl.replace(questionsBlockRegex, (match) => {
        return match + '\n        checkAuthorization(quiz, userPrincipal);';
    });
}

fs.writeFileSync('src/main/java/com/ex/learninghub/modules/quiz/service/impl/QuizServiceImpl.java', impl);
console.log("Quiz IDOR fixed!");
