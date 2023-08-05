package com.rurbisservices.churchdonation.utils;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.rurbisservices.churchdonation.utils.Constants.EMPTY_STRING;

public class ServiceUtils {
    public static boolean isObjectNull(Object object) {
        return object == null;
    }

    public static boolean isStringEmpty(String string) {
        return string.trim().equals(Constants.EMPTY_STRING);
    }

    public static boolean isStringNullOrEmpty(String string) {
        return isObjectNull(string) || isStringEmpty(string);
    }

    public static String returnTrimOrNull(String string) {
        if (isObjectNull(string)) return Constants.EMPTY_STRING;
        return string.trim();
    }

    public static boolean isListNullOrEmpty(List<?> list) {
        return isObjectNull(list) || list.size() == 0;
    }

    public static String wildCardParam(String string) {
        if (isStringNullOrEmpty(string)) {
            return Constants.PERCENT;
        }
        return Constants.PERCENT + string.replaceAll("\\"+ Constants.STAR, Constants.PERCENT).trim() + Constants.PERCENT;
    }

    public static boolean isStringDouble(String string) {
        try {
            Double.parseDouble(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isStringLong(String string) {
        try {
            Long.parseLong(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void exportTableView(TableView<?> table, String filenameXLS, String filenameCSV) throws IOException {
        //export to XLS
        List<TableColumn<?, ?>> columns = table.getColumns().stream().filter(x -> !x.getId().startsWith("col")).collect(Collectors.toList());
        ObservableList<?> items = table.getItems();
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet");
        Row rowHeader = sheet.createRow(0);
        //init header
        for (int i = 0; i < columns.size(); i++) {
            rowHeader.createCell(i).setCellValue(columns.get(i).getText());
        }
        for (int i = 0; i < items.size(); i++) {
            Row rowData = sheet.createRow(i + 1);
            for (int j = 0; j < columns.size(); j++) {
                if (!isObjectNull(columns.get(j).getCellData(i))) {
                    rowData.createCell(j).setCellValue(columns.get(j).getCellData(i).toString());
                } else {
                    rowData.createCell(j).setCellValue(EMPTY_STRING);
                }
            }
        }
        FileOutputStream fos = new FileOutputStream(filenameXLS);
        workbook.write(fos);
        fos.close();

        //export to CSV
        FileWriter writer = new FileWriter(filenameCSV);
        //init header
        for (int i = 0; i < columns.size(); i++) {
            if ( i == columns.size() - 1) {
                writer.write(columns.get(i).getText() + "\n");
            } else {
                writer.write(columns.get(i).getText() + ",");
            }
        }
        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < columns.size(); j++) {
                String cell = columns.get(j).getCellData(i).toString().replaceAll("\\n", " ");
                if (j == columns.size() - 1) {
                    writer.write(cell + "\n");
                } else {
                    writer.write(cell + ",");
                }
            }
        }
        writer.close();
    }
}
