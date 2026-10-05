const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/registration/dto/response/RegistrationPeriodResponse.java', 'utf8');

if (!content.includes('private Integer classCount;')) {
    content = content.replace('private Boolean isActive;', `private Boolean isActive;\n    private Integer classCount;`);
    content = content.replace('.isActive(p.getIsActive())', `.isActive(p.getIsActive())\n                .classCount(p.getAllowedClasses() != null ? p.getAllowedClasses().size() : 0)`);
}

fs.writeFileSync('src/main/java/com/ex/learninghub/modules/registration/dto/response/RegistrationPeriodResponse.java', content);
