package com.iecube.community.util.xlsx;

import com.iecube.community.email.EmailParams;
import com.iecube.community.model.student.dto.AddStudentDto;
import com.iecube.community.model.student.service.impl.StudentServiceImpl;
import com.iecube.community.util.SHA256;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class xlsxReadTest {

    public static void read(InputStream in) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(in);
        List<EmailParams> toSendEmail = new ArrayList<>();
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    if (row.getLastCellNum() < 1) {
                        continue;
                    }
                    EnumMap<UserExcelHeaderEnum, String> rowData = getRowData(row);
                    System.out.println(rowData);
                    String studentId = rowData.get(UserExcelHeaderEnum.NUM);
                    String studentName = rowData.get(UserExcelHeaderEnum.NAME);
                    String email = rowData.get(UserExcelHeaderEnum.EMAIL);
                    String school = rowData.get(UserExcelHeaderEnum.SCHOOL);
                    String collage = rowData.get(UserExcelHeaderEnum.COLLAGE);
                    String major = rowData.get(UserExcelHeaderEnum.MAJOR);
                    String gradeClass = rowData.get(UserExcelHeaderEnum.GRADE_CLASS);
                    String salt = UUID.randomUUID().toString().toUpperCase();
                    String password = "111111";
                    String sha256Password = SHA256.encryptStringWithSHA256(password);
                    String md5Password = getMD5Password(sha256Password, salt);
                    System.out.println(school+collage+major+gradeClass+studentId+studentName+email+md5Password+salt);
                }
            }
        }
    }

    private static String getMD5Password(String password, String salt){
        // md5加密算法的方法 进行3次
        for (int i=0; i<3; i++){
            password = DigestUtils.md5DigestAsHex((salt+password+salt).getBytes()).toUpperCase();
        }
        //返回加密之后的密码
        return password;
    }

    private static EnumMap<UserExcelHeaderEnum, String> getRowData(Row row) {
        EnumMap<UserExcelHeaderEnum, String> map = new EnumMap<>(UserExcelHeaderEnum.class);
        for (UserExcelHeaderEnum col :UserExcelHeaderEnum.values()) {
            Cell cell = row.getCell(col.ordinal());
            String value = getStringValue(cell);
            map.put(col, value);
        }
        return map;
    }

    private static String getStringValue(Cell cell) {
        if (cell == null) {
            return StringUtils.EMPTY;
        }
        return cell.getStringCellValue();
    }


    @Getter
    protected enum UserExcelHeaderEnum {
        /**
         * 编号
         */
        SCHOOL("学校"),
        COLLAGE("学院"),
        MAJOR("专业"),
        GRADE_CLASS("班级"),
        NUM("学号"),
        NAME("姓名"),
        EMAIL("邮箱");
        private final String remark;

        UserExcelHeaderEnum(String remark) {
            this.remark = remark;
        }
    }

    public static void main(String[] args) throws IOException {
        read(Files.newInputStream(Paths.get("D:\\work\\学生信息.xlsx")));
    }
}
