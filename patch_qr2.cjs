const fs = require('fs');
let impl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/grading/service/impl/QrCodeAttendanceServiceImpl.java', 'utf8');

impl = impl.replace(
    'if (!clazzRepository.existsById(classId)) {',
    'Clazz clazz = clazzRepository.findById(classId).orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));\n        if (lecturerPrincipal.getUser().getRole() == com.ex.learninghub.common.enums.Role.LECTURER) {\n            if (clazz.getLecturer() == null || !clazz.getLecturer().getId().equals(lecturerPrincipal.getUser().getId())) {\n                throw new AppException(ErrorCode.FORBIDDEN);\n            }\n        }\n        if (false) {'
);

impl = impl.replace(
    'String otpCode = String.format("%06d", Math.abs((sessionToken + timeWindow).hashCode() % 1000000));',
    'java.security.SecureRandom random = new java.security.SecureRandom();\n        String otpCode = String.format("%06d", random.nextInt(1000000));'
);

fs.writeFileSync('src/main/java/com/ex/learninghub/modules/grading/service/impl/QrCodeAttendanceServiceImpl.java', impl);
console.log("Patched");
