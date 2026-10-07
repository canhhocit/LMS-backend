const fs = require('fs');

let config = fs.readFileSync('src/main/java/com/ex/learninghub/common/config/SecurityConfig.java', 'utf8');

config = config.replace(
    'configuration.addAllowedOriginPattern("*");',
    'configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));'
);

fs.writeFileSync('src/main/java/com/ex/learninghub/common/config/SecurityConfig.java', config);
console.log("CORS patched!");
