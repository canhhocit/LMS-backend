const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/course/controller/MyClassController.java', 'utf8');

const regex = /\/\/ 3\. Get enrolled class IDs[\s\S]*?Set<Long> enrolledIds = enrollmentService\.getClazzesOfStudent\(studentId\)\.stream\(\)[\s\S]*?\.collect\(Collectors\.toSet\(\)\);[\s\S]*?\/\/ 4\. Filter and map[\s\S]*?List<ClazzResponse> available = allowedClasses\.stream\(\)[\s\S]*?\.filter\(c -> !enrolledIds\.contains\(c\.getId\(\)\)\)[\s\S]*?\.filter\(c -> finalCurrIds\.isEmpty\(\) \|\|[\s\S]*?\(c\.getCourse\(\) != null && finalCurrIds\.contains\(c\.getCourse\(\)\.getId\(\)\)\)\)[\s\S]*?\.map\(c -> ClazzResponse\.from\(c, 0L\)\)[\s\S]*?\.toList\(\);/m;

const replacement = `// 3. Get enrolled classes and courses
        List<ClazzResponse> enrolledClasses = enrollmentService.getClazzesOfStudent(studentId);
        Set<Long> enrolledIds = enrolledClasses.stream()
                .map(ClazzResponse::getId)
                .collect(Collectors.toSet());
        Set<Long> enrolledCourseIds = enrolledClasses.stream()
                .filter(c -> c.getCourseId() != null)
                .map(ClazzResponse::getCourseId)
                .collect(Collectors.toSet());

        // 4. Filter and map
        List<ClazzResponse> available = allowedClasses.stream()
                .filter(c -> !enrolledIds.contains(c.getId())) // Remove exact class
                .filter(c -> c.getCourse() == null || !enrolledCourseIds.contains(c.getCourse().getId())) // Remove other classes of SAME course
                .filter(c -> finalCurrIds.isEmpty() || 
                    (c.getCourse() != null && finalCurrIds.contains(c.getCourse().getId())))
                .map(c -> ClazzResponse.from(c, 0L))
                .toList();`;

if (content.match(regex)) {
    content = content.replace(regex, replacement);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/course/controller/MyClassController.java', content);
}
