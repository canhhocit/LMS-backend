const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/registration/service/RegistrationService.java', 'utf8');

if (!content.includes('batchRegister')) {
    content = content.replace('RegistrationResponse register(Long clazzId, UserPrincipal principal);', 'RegistrationResponse register(Long clazzId, UserPrincipal principal);\n    List<RegistrationResponse> batchRegister(List<Long> clazzIds, UserPrincipal principal);');
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/registration/service/RegistrationService.java', content);
}
