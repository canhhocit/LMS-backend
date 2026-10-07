const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', 'utf8');

const targetStr = `        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        period.getAllowedClasses().add(clazz);`;

const replacement = `        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        // Kiểm tra xem lớp đã có lịch học chưa. Phải có lịch học mới được mở đăng ký!
        if (scheduleRepository.findByClazzId(clazzId).isEmpty()) {
            throw new AppException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        period.getAllowedClasses().add(clazz);`;

if (content.includes(targetStr)) {
    content = content.replace(targetStr, replacement);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', content);
}
