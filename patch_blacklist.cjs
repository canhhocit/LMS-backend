const fs = require('fs');

let service = fs.readFileSync('src/main/java/com/ex/learninghub/common/security/TokenBlacklistService.java', 'utf8');

service = service.replace(
    'return false;\n        }\n    }',
    'throw new com.ex.learninghub.common.exception.AppException(com.ex.learninghub.common.exception.ErrorCode.UNAUTHENTICATED);\n        }\n    }'
);

fs.writeFileSync('src/main/java/com/ex/learninghub/common/security/TokenBlacklistService.java', service);
console.log("TokenBlacklistService patched!");
