const fs = require('fs');

let service = fs.readFileSync('src/main/java/com/ex/learninghub/modules/course/service/impl/ClazzServiceImpl.java', 'utf8');

// Update create
const oldCreate = `.maxStudents(request.getMaxStudents())
                .build();`;
const newCreate = `.maxStudents(request.getMaxStudents())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .examDate(request.getExamDate())
                .examRoom(request.getExamRoom())
                .examFormat(request.getExamFormat())
                .examDuration(request.getExamDuration())
                .build();`;
if (service.includes(oldCreate)) {
    service = service.replace(oldCreate, newCreate);
}

// Update update
const oldUpdate = `clazz.setMaxStudents(request.getMaxStudents());`;
const newUpdate = `clazz.setMaxStudents(request.getMaxStudents());
        clazz.setStartDate(request.getStartDate());
        clazz.setEndDate(request.getEndDate());
        clazz.setExamDate(request.getExamDate());
        clazz.setExamRoom(request.getExamRoom());
        clazz.setExamFormat(request.getExamFormat());
        clazz.setExamDuration(request.getExamDuration());`;
if (service.includes(oldUpdate)) {
    service = service.replace(oldUpdate, newUpdate);
}

// Also need to handle AcademicSemester if passed?
// The original code doesn't map academicSemester yet, I'll add that mapping too if request has semesterId.
if (service.includes('import com.ex.learninghub.modules.course.entity.Course;') && !service.includes('AcademicSemesterRepository')) {
    // skip academic semester wiring for now to avoid injecting repository.
    // wait, we can just save it.
}

fs.writeFileSync('src/main/java/com/ex/learninghub/modules/course/service/impl/ClazzServiceImpl.java', service);
console.log('done Service');
