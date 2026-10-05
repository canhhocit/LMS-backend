const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', 'utf8');

const importClazzResponse = `import com.ex.learninghub.modules.course.dto.response.ClazzResponse;`;
if (!content.includes(importClazzResponse)) {
    content = content.replace(/import java\.util\.List;/, `import java.util.List;\n${importClazzResponse}`);
}

const methodsStr = `    // =================== PERIOD CLASSES ===================

    @Override
    @Transactional
    public void addClazzToPeriod(Long periodId, Long clazzId) {
        RegistrationPeriod period = periodRepository.findActiveWithClasses()
            .filter(p -> p.getId().equals(periodId))
            .orElseGet(() -> periodRepository.findById(periodId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND)));

        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        period.getAllowedClasses().add(clazz);
        periodRepository.save(period);
    }

    @Override
    @Transactional
    public void removeClazzFromPeriod(Long periodId, Long clazzId) {
        RegistrationPeriod period = periodRepository.findById(periodId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        period.getAllowedClasses().removeIf(c -> c.getId().equals(clazzId));
        periodRepository.save(period);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClazzResponse> getClazzesInPeriod(Long periodId) {
        RegistrationPeriod period = periodRepository.findById(periodId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        return period.getAllowedClasses().stream()
                .map(c -> {
                    long currentStudents = enrollmentRepository.findByClazzId(c.getId()).stream()
                            .filter(e -> "ACTIVE".equals(e.getStatus())).count();
                    return ClazzResponse.from(c, (int) currentStudents);
                })
                .collect(Collectors.toList());
    }

    // =================== STUDENT OPERATIONS ===================`;

if (!content.includes('addClazzToPeriod(')) {
    content = content.replace('    // =================== STUDENT OPERATIONS ===================', methodsStr);
}

fs.writeFileSync('src/main/java/com/ex/learninghub/modules/registration/service/impl/RegistrationServiceImpl.java', content);
