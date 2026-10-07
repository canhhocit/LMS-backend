const fs = require('fs');

let yml = fs.readFileSync('src/main/resources/application.yml', 'utf8');
yml = yml.replace('secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}', 'secret: ${JWT_SECRET}');
fs.writeFileSync('src/main/resources/application.yml', yml);

if (fs.existsSync('src/main/resources/application-prod.yml')) {
    let prodYml = fs.readFileSync('src/main/resources/application-prod.yml', 'utf8');
    if (prodYml.includes('secret: ${JWT_SECRET')) {
        prodYml = prodYml.replace(/secret:\s*\$\{JWT_SECRET:[^\}]+\}/g, 'secret: ${JWT_SECRET}');
        fs.writeFileSync('src/main/resources/application-prod.yml', prodYml);
    }
}
console.log("JWT Secret fixed!");
