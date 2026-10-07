const fs = require('fs');

let config = fs.readFileSync('src/main/java/com/ex/learninghub/common/config/WebSocketConfig.java', 'utf8');

if (!config.includes('String allowedOrigins')) {
    config = config.replace(
        'public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {',
        'public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {\n\n    @org.springframework.beans.factory.annotation.Value("${app.cors.allowed-origins:http://localhost:5173}")\n    private String allowedOrigins;'
    );
}

config = config.replace(
    '.setAllowedOriginPatterns("*")',
    '.setAllowedOrigins(allowedOrigins.split(","))'
);

fs.writeFileSync('src/main/java/com/ex/learninghub/common/config/WebSocketConfig.java', config);
console.log("WebSocketConfig patched!");
