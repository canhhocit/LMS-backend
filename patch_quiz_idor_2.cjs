const fs = require('fs');

let impl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/quiz/service/impl/QuizServiceImpl.java', 'utf8');

const regex = /public List<QuestionResponse> getQuestionsByQuizId\(Long quizId, UserPrincipal userPrincipal\) {\s*\/\/ Verify quiz exists\s*findQuizOrThrow\(quizId\);/;
if (regex.test(impl)) {
    impl = impl.replace(regex, `public List<QuestionResponse> getQuestionsByQuizId(Long quizId, UserPrincipal userPrincipal) {
        // Verify quiz exists
        Quiz quiz = findQuizOrThrow(quizId);
        checkAuthorization(quiz, userPrincipal);`);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/quiz/service/impl/QuizServiceImpl.java', impl);
    console.log("Fixed getQuestionsByQuizId authorization!");
} else {
    console.log("Regex not matched");
}
