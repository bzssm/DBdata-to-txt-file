import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by 丰汀 on 2016/5/13.
 */
public class ExportExcel {
    public static void main(String[] args) {
        String[][] string = new String[][]{{"1", "2", "3"}, {"4", "5", "6"}, {"7", "8", "9"}};
        try {
            OutputStream out = new FileOutputStream("D://a.xlsx");

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("sheet 1");
            sheet.setDefaultColumnWidth((short) 15);
            for (int i = 0; i < 3; i++) {
                XSSFRow row = sheet.createRow(i);
                for (int j = 0; j < 3; j++) {
                    XSSFCell cell = row.createCell(j);
                    XSSFRichTextString text = new XSSFRichTextString(string[i][j]);
                    cell.setCellValue(text);
                }
            }
            try {
                workbook.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
