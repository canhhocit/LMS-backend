const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', 'utf8');

const oldRegister = `        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        if (period.getAcademicSemester() != null && clazz.getAcademicSemester() != null) {`;

const newRegister = `        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        // Check if class is in the allowed classes for this period
        RegistrationPeriod activePeriodWithClasses = periodRepository.findActiveWithClasses().orElse(period);
        boolean isAllowed = activePeriodWithClasses.getAllowedClasses().stream()
                .anyMatch(c -> c.getId().equals(clazzId));
        if (!isAllowed && !activePeriodWithClasses.getAllowedClasses().isEmpty()) {
            throw new AppException(ErrorCode.FORBIDDEN); // Class is not in the allowed list for this period
        }

        if (period.getAcademicSemester() != null && clazz.getAcademicSemester() != null) {`;

if (content.includes(oldRegister)) {
    content = content.replace(oldRegister, newRegister);
}

fs.writeFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', content);
