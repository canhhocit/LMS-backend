const fs = require('fs');

let repo = fs.readFileSync('src/main/java/com/ex/learninghub/modules/auth/repository/RefreshTokenRepository.java', 'utf8');

if (!repo.includes('revokeToken')) {
    repo = repo.replace(
        'void deleteByUserId(Long userId);',
        'void deleteByUserId(Long userId);\n\n    @org.springframework.data.jpa.repository.Modifying\n    @org.springframework.data.jpa.repository.Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.id = :id AND r.revoked = false")\n    int revokeToken(@org.springframework.data.repository.query.Param("id") Long id);'
    );
    fs.writeFileSync('src/main/java/com/ex/learninghub/modules/auth/repository/RefreshTokenRepository.java', repo);
}

let svc = fs.readFileSync('src/main/java/com/ex/learninghub/modules/auth/service/impl/AuthServiceImpl.java', 'utf8');

// Password change/reset revocation
svc = svc.replace(
    'user.setIsFirstLogin(false);\n        userRepository.save(user);',
    'user.setIsFirstLogin(false);\n        userRepository.save(user);\n        refreshTokenRepository.deleteByUserId(user.getId());'
);
svc = svc.replace(
    'user.setPassword(passwordEncoder.encode(request.getNewPassword()));\n        userRepository.save(user);',
    'user.setPassword(passwordEncoder.encode(request.getNewPassword()));\n        userRepository.save(user);\n        refreshTokenRepository.deleteByUserId(user.getId());'
);

// Refresh race condition
svc = svc.replace(
    '// Rotate: revoke old token, issue new one\n        stored.setRevoked(true);\n        refreshTokenRepository.save(stored);',
    '// Rotate: revoke old token, issue new one\n        int updatedRows = refreshTokenRepository.revokeToken(stored.getId());\n        if (updatedRows == 0) {\n            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);\n        }'
);

fs.writeFileSync('src/main/java/com/ex/learninghub/modules/auth/service/impl/AuthServiceImpl.java', svc);
console.log("AuthServiceImpl patched!");
