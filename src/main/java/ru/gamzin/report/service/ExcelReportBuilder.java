package ru.gamzin.report.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelReportBuilder {

    public static byte[] build(List<PayrollReportService.ReportRow> rows, int year) throws IOException {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Начисления " + year);

            CellStyle headerStyle = wb.createCellStyle();
            Font boldFont = wb.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);

            Row header = sheet.createRow(0);
            String[] titles = {"ФИО", "Должность", "Таб. номер", "Сумма начислений за " + year};
            for (int i = 0; i < titles.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(titles[i]);
                cell.setCellStyle(headerStyle);
            }

            CellStyle moneyStyle = wb.createCellStyle();
            moneyStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));

            int rowIdx = 1;
            for (PayrollReportService.ReportRow r : rows) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.fio);
                row.createCell(1).setCellValue(r.naimDol);
                row.createCell(2).setCellValue(r.tabNom);
                Cell sumCell = row.createCell(3);
                sumCell.setCellValue(r.totalAccrued.doubleValue());
                sumCell.setCellStyle(moneyStyle);
            }

            for (int i = 0; i < titles.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        }
    }
}
