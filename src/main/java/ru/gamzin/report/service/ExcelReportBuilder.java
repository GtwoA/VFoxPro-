package ru.gamzin.report.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelReportBuilder {

    private static final String[] MONTH_NAMES = {
            "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
    };

    public static byte[] build(List<PayrollReportService.ReportRow> rows, int year) throws IOException {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Начисления " + year);

            CellStyle headerStyle = wb.createCellStyle();
            Font boldFont = wb.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);

            int totalCols = 3 + 12 + 1;
            String[] titles = new String[totalCols];
            titles[0] = "ФИО";
            titles[1] = "Должность";
            titles[2] = "Таб. номер";
            for (int m = 0; m < 12; m++) {
                titles[3 + m] = MONTH_NAMES[m];
            }
            titles[totalCols - 1] = "Итого за " + year;

            Row header = sheet.createRow(0);
            for (int i = 0; i < titles.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(titles[i]);
                cell.setCellStyle(headerStyle);
            }

            CellStyle moneyStyle = wb.createCellStyle();
            moneyStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));

            CellStyle totalMoneyStyle = wb.createCellStyle();
            totalMoneyStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
            CellStyle totalMoneyFont = wb.createCellStyle();
            Font bold2 = wb.createFont();
            bold2.setBold(true);
            totalMoneyFont.cloneStyleFrom(totalMoneyStyle);
            totalMoneyFont.setFont(bold2);

            int rowIdx = 1;
            for (PayrollReportService.ReportRow r : rows) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.fio);
                row.createCell(1).setCellValue(r.naimDol);
                row.createCell(2).setCellValue(r.tabNom);

                for (int m = 1; m <= 12; m++) {
                    Cell c = row.createCell(2 + m);
                    java.math.BigDecimal v = r.monthly.getOrDefault(m, java.math.BigDecimal.ZERO);
                    c.setCellValue(v.doubleValue());
                    c.setCellStyle(moneyStyle);
                }

                Cell totalCell = row.createCell(totalCols - 1);
                totalCell.setCellValue(r.totalAccrued.doubleValue());
                totalCell.setCellStyle(totalMoneyFont);
            }

            for (int i = 0; i < titles.length; i++) {
                sheet.autoSizeColumn(i);
            }
            sheet.createFreezePane(3, 1);

            wb.write(out);
            return out.toByteArray();
        }
    }
}
