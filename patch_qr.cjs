const fs = require('fs');
let impl = fs.readFileSync('src/main/java/com/ex/learninghub/modules/grading/service/impl/QrCodeAttendanceServiceImpl.java', 'utf8');

const generateBefore = `    public QrSessionResponse generateQrSession(Long classId, UserPrincipal lecturerPrincipal) {
        if (!clazzRepository.existsById(classId)) {
            throw new AppException(ErrorCode.CLAZZ_NOT_FOUND);
        }

        String sessionToken = UUID.randomUUID().toString();
        // Generate 6-digit dynamic OTP
        long timeWindow = System.currentTimeMillis() / 10000; // Changes every 10s
        String otpCode = String.format("%06d", Math.abs((sessionToken + timeWindow).hashCode() % 1000000));`;

const generateAfter = `    public QrSessionResponse generateQrSession(Long classId, UserPrincipal lecturerPrincipal) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        if (lecturerPrincipal.getUser().getRole() == com.ex.learninghub.common.enums.Role.LECTURER) {
            if (clazz.getLecturer() == null || !clazz.getLecturer().getId().equals(lecturerPrincipal.getUser().getId())) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
        }

        String sessionToken = UUID.randomUUID().toString();
        // Generate 6-digit secure OTP
        java.security.SecureRandom random = new java.security.SecureRandom();
        String otpCode = String.format("%06d", random.nextInt(1000000));`;

if (impl.includes(generateBefore)) {
    impl = impl.replace(generateBefore, generateAfter);
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/grading/service/impl/QrCodeAttendanceServiceImpl.java', impl);
    console.log("QR Attendance IDOR and OTP patched!");
} else {
    console.log("Not matched");
}
