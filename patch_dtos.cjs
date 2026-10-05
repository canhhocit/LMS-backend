const fs = require('fs');

// PATCH ClazzRequest
let req = fs.readFileSync('src/main/java/com/ex/learninghub/modules/course/dto/request/ClazzRequest.java', 'utf8');
if (!req.includes('import java.time.LocalDate;')) {
    req = req.replace('import lombok.Setter;', 'import lombok.Setter;\nimport java.time.LocalDate;\nimport java.time.LocalDateTime;');
}
if (!req.includes('private LocalDate startDate;')) {
    const fields = `
    private Long semesterId;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime examDate;

    private String examRoom;

    private String examFormat;

    private Integer examDuration;
`;
    req = req.replace('private String academicYear;', 'private String academicYear;\n' + fields);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/course/dto/request/ClazzRequest.java', req);
}

// PATCH ClazzResponse
let res = fs.readFileSync('src/main/java/com/ex/learninghub/modules/course/dto/response/ClazzResponse.java', 'utf8');
if (!res.includes('import java.time.LocalDate;')) {
    res = res.replace('import java.time.LocalDateTime;', 'import java.time.LocalDate;\nimport java.time.LocalDateTime;');
}
if (!res.includes('private LocalDate startDate;')) {
    const fields = `
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime examDate;
    private String examRoom;
    private String examFormat;
    private Integer examDuration;
`;
    res = res.replace('private Integer lessonCount;', fields + '    private Integer lessonCount;');
    
    const builderMapping = `
                .startDate(clazz.getStartDate())
                .endDate(clazz.getEndDate())
                .examDate(clazz.getExamDate())
                .examRoom(clazz.getExamRoom())
                .examFormat(clazz.getExamFormat())
                .examDuration(clazz.getExamDuration())`;
                
    res = res.replace('.lessonCount(lessonCount != null ? lessonCount.intValue() : 0)', '.lessonCount(lessonCount != null ? lessonCount.intValue() : 0)' + builderMapping);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/course/dto/response/ClazzResponse.java', res);
}
console.log('done DTOs');
