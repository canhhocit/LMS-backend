const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/course/entity/Clazz.java', 'utf8');

if (!content.includes('import java.time.LocalDate;')) {
    content = content.replace('import lombok.Setter;', 'import lombok.Setter;\nimport java.time.LocalDate;\nimport java.time.LocalDateTime;');
}

const newFields = `
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "exam_date")
    private LocalDateTime examDate;

    @Column(name = "exam_room", length = 50)
    private String examRoom;

    @Column(name = "exam_format", length = 50)
    private String examFormat;

    @Column(name = "exam_duration")
    private Integer examDuration;

    /** Tiện ích: lấy semester string từ FK hoặc fallback sang string cũ */`;

if (!content.includes('private LocalDate startDate;')) {
    content = content.replace('/** Tiện ích: lấy semester string từ FK hoặc fallback sang string cũ */', newFields);
}

fs.writeFileSync('src/main/java/com/ex/learninghub/modules/course/entity/Clazz.java', content);
console.log('done Clazz.java');
