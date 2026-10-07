const fs = require('fs');
const filePath = 'src/main/java/com/ex/learninghub/modules/schedule/service/impl/ScheduleServiceImpl.java';
let content = fs.readFileSync(filePath, 'utf8');

// The file has duplicated annotations:
//    @Override
//    @Transactional(readOnly = true)
//    
//    @Override
//    @Transactional(readOnly = true)
//    public List<ScheduleResponse> getAllAdminSchedules(UserPrincipal principal) {

const badBlock = `    @Override
    @Transactional(readOnly = true)
    
    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllAdminSchedules`;

const goodBlock = `    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllAdminSchedules`;

if (content.includes(badBlock)) {
    content = content.replace(badBlock, goodBlock);
    fs.writeFileSync(filePath, content);
    console.log("Fixed duplicated annotations!");
} else {
    // If exact spacing varies, try regex
    const regex = /@Override\s+@Transactional\(readOnly\s*=\s*true\)\s+@Override\s+@Transactional\(readOnly\s*=\s*true\)\s+public List<ScheduleResponse> getAllAdminSchedules/g;
    if (regex.test(content)) {
        content = content.replace(regex, `@Override\n    @Transactional(readOnly = true)\n    public List<ScheduleResponse> getAllAdminSchedules`);
        fs.writeFileSync(filePath, content);
        console.log("Fixed duplicated annotations via regex!");
    } else {
        console.log("Could not find the duplicated block.");
    }
}
