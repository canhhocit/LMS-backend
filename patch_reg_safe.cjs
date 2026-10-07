const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', 'utf8');

const targetStr = `        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        // Check if class is in the allowed classes for this period`;

const replacement = `        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        // Check if already enrolled in another class of the SAME course in this semester
        if (clazz.getCourse() != null) {
            boolean duplicateCourse = enrollmentRepository.findByStudentId(student.getId()).stream()
                    .anyMatch(e -> e.getClazz().getCourse() != null 
                            && e.getClazz().getCourse().getId().equals(clazz.getCourse().getId())
                            && e.getSemester().equals(period.getEffectiveSemester())
                            && e.getAcademicYear().equals(period.getEffectiveAcademicYear()));
            if (duplicateCourse) {
                throw new AppException(ErrorCode.ENROLLMENT_EXISTS);
            }
        }

        // Check if class is in the allowed classes for this period`;

if (content.includes(targetStr)) {
    content = content.replace(targetStr, replacement);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', content);
}
