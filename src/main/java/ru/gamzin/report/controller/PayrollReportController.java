package ru.gamzin.report.controller;

import ru.gamzin.report.service.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class PayrollReportController {

    private final PayrollReportService reportService;

    public PayrollReportController(PayrollReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Пример: GET /api/report?nomScol=006&year=2024
     * Отчёт по точному номеру школы.
     */
    @GetMapping("/api/report")
    public ResponseEntity<byte[]> reportBySchool(@RequestParam String nomScol,
                                                 @RequestParam int year) throws IOException {
        List<PayrollReportService.ReportRow> rows =
                reportService.buildReportBySchoolNumber(nomScol, year);
        return toXlsxResponse(rows, year, "report_" + nomScol + "_" + year + ".xlsx");
    }

    /**
     * Пример: GET /api/report/by-locality?keyword=Никитин&year=2024
     * Отчёт по "месту" (Новоникитино), охватывающий все связанные коды школ
     * (школа, садик, ученики, советники и т.д. — см. PayrollReportService).
     */
    @GetMapping("/api/report/by-locality")
    public ResponseEntity<byte[]> reportByLocality(@RequestParam String keyword,
                                                   @RequestParam int year) throws IOException {
        List<PayrollReportService.ReportRow> rows =
                reportService.buildReportByLocality(keyword, year);
        return toXlsxResponse(rows, year, "report_" + keyword + "_" + year + ".xlsx");
    }

    private ResponseEntity<byte[]> toXlsxResponse(List<PayrollReportService.ReportRow> rows,
                                                  int year, String filename) throws IOException {
        byte[] xlsx = ExcelReportBuilder.build(rows, year);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }
}
