const fs = require('fs');
let content = fs.readFileSync('src/main/java/com/ex/learninghub/modules/tuition/service/impl/TuitionServiceImpl.java', 'utf8');

const regex = /var existing = invoiceRepository\.findByStudentIdAndSemesterAndAcademicYear[\s\S]*?return TuitionInvoiceResponse\.from\(invoiceRepository\.save\(inv\)\);\s*\}/m;

const replacement = `var existing = invoiceRepository.findByStudentIdAndSemesterAndAcademicYear(studentId, semester, academicYear);

        LocalDate rateDate = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        TuitionRate rate = rateRepository
                .findFirstByAcademicYearAndSemesterAndEffectiveFromLessThanEqualAndIsActiveTrueOrderByEffectiveFromDesc(
                        academicYear, normalizeSemester(semester), rateDate)
                .or(() -> rateRepository
                        .findFirstByAcademicYearAndSemesterIsNullAndEffectiveFromLessThanEqualAndIsActiveTrueOrderByEffectiveFromDesc(
                                academicYear, rateDate))
                .orElseThrow(() -> new AppException(ErrorCode.TUITION_RATE_NOT_FOUND));

        int totalCredits = enrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> semester.equals(e.getSemester()) && academicYear.equals(e.getAcademicYear()))
                .filter(e -> e.getClazz() != null && e.getClazz().getCourse() != null
                        && e.getClazz().getCourse().getCredit() != null)
                .mapToInt(e -> e.getClazz().getCourse().getCredit())
                .sum();

        BigDecimal amount = rate.getPricePerCredit()
                .multiply(BigDecimal.valueOf(totalCredits))
                .setScale(2, RoundingMode.HALF_UP);

        if (existing.isPresent()) {
            TuitionInvoice inv = existing.get();
            if (inv.getTotalCredits() == totalCredits) {
                return TuitionInvoiceResponse.from(inv);
            }
            inv.setTotalCredits(totalCredits);
            inv.setAmount(amount);
            if ("PAID".equals(inv.getStatus())) {
                inv.setStatus("UNPAID");
                inv.setPaidAt(null);
            }
            return TuitionInvoiceResponse.from(invoiceRepository.save(inv));
        }

        TuitionInvoice inv = TuitionInvoice.builder()
                .student(student)
                .semester(semester)
                .academicYear(academicYear)
                .totalCredits(totalCredits)
                .pricePerCredit(rate.getPricePerCredit())
                .amount(amount)
                .status("UNPAID")
                .dueDate(LocalDateTime.now().plusDays(30))
                .build();
        return TuitionInvoiceResponse.from(invoiceRepository.save(inv));
    }`;

content = content.replace(regex, replacement);
fs.writeFileSync('src/main/java/com/ex/learninghub/modules/tuition/service/impl/TuitionServiceImpl.java', content);
