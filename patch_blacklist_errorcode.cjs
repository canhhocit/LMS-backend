const fs = require('fs');
let service = fs.readFileSync('src/main/java/com/ex/learninghub/common/security/TokenBlacklistService.java', 'utf8');
service = service.replace('ErrorCode.UNAUTHENTICATED', 'ErrorCode.UNAUTHORIZED');
fs.writeFileSync('src/main/java/com/ex/learninghub/common/security/TokenBlacklistService.java', service);
console.log("Fixed UNAUTHENTICATED to UNAUTHORIZED");
