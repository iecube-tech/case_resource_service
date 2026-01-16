package com.iecube.community.model.Exam.Service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.Exam.Dto.QuesType;
import com.iecube.community.model.Exam.Dto.QuestionDto;
import com.iecube.community.model.Exam.Service.ExamService;
import com.iecube.community.model.Exam.entity.ExamInfoEntity;
import com.iecube.community.model.Exam.exception.ExcelCellParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class ExamServiceImpl implements ExamService {

    // 日期格式化器
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    // 存储单元格解析异常信息（key: sheet名称, value: 异常信息列表）
    private static final Map<String, List<String>> cellParseErrors = new HashMap<>();

    @Value("${resource-location}/file")
    private String fileLocation;

    @Autowired
    private ObjectMapper objectMapper;

    //解析EXCEL // 返回vo对象
    @Override
    public void parseExcel(Integer projectId, String filename){
        resetErrors();
        File excel = new File(fileLocation  + File.separator + filename);
        if(!excel.exists()){
            throw new ServiceException("文件不存在");
        }
        try (
                InputStream is = new FileInputStream(excel);
                Workbook workbook = WorkbookFactory.create(is)
        ) {
            ExamInfoEntity exam = null;
            List<QuestionDto> questionDtoList = new ArrayList<>();
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++){
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                String sheetName = sheet.getSheetName();
                if(sheetName.equals("考试信息")){
                    exam =parseExamInfoSheet(sheet);
                    exam.setProjectId(projectId);
                }else {
                    List<QuestionDto> questions = parseQuesSheet(sheet);
                    if (questions != null && !questions.isEmpty()) {
                        questionDtoList.addAll(questions);
                    }
                }
            }
            System.out.println(exam);
            questionDtoList.forEach(q->{
                System.out.println(q.toString());
            });
            checkCellParseErrors();
            // todo 整理为vo 处理总分 以及 随机规则

        } catch (IOException e) {
            log.error("解析EXCEL文件异常：{}",e.getMessage(),e);
            throw new ServiceException(e);
        }
    }

    private List<QuestionDto>  parseQuesSheet(Sheet sheet){
        QuesType quesType = QuesType.getByDesc(sheet.getSheetName());
        if(quesType!=null){
            if(quesType==QuesType.QA){
                return parseQASheet(sheet);
            }else {
                return parseChoiceSheet(sheet);
            }
        }
        return null;
    }

    private List<QuestionDto> parseChoiceSheet(Sheet sheet){
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for(int rowIndex=1; rowIndex<sheet.getLastRowNum(); rowIndex++){
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            QuestionDto questionDto = new QuestionDto();
            questionDto.setOrder(rowIndex);
            if(sheet.getSheetName().equals("单选题")){
                questionDto.setType(QuesType.CHOICE);
            }else if(sheet.getSheetName().equals("多选题")){
                questionDto.setType(QuesType.MultipleCHOICE);
            }
            questionDto.setQuestion(getCellValue(row.getCell(1),sheet.getSheetName(),rowIndex, 1,String.class));
            questionDto.setAnswer(getCellValue(row.getCell(6),sheet.getSheetName(),rowIndex, 6,String.class));
            questionDto.setScore(getCellValue(row.getCell(7),sheet.getSheetName(),rowIndex, 7,Double.class));
            questionDto.setDifficulty(getCellValue(row.getCell(8),sheet.getSheetName(),rowIndex, 8,Integer.class));
            String knowledge = getCellValue(row.getCell(9),sheet.getSheetName(),rowIndex, 9,String.class);
            questionDto.setKnowledge(splitString(knowledge, "，", true));
            if(row.getCell(10)==null){
                questionDto.setIsRandom(false);
            }else {
                questionDto.setRandomType(getCellValue(row.getCell(10), sheet.getSheetName(), rowIndex, 10, Integer.class));
                questionDto.setRandomNumber(getCellValue(row.getCell(11), sheet.getSheetName(), rowIndex, 11, Integer.class));
                questionDto.setIsRandom(questionDto.getRandomType()!=null);
            }
            List<String> labels = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for(int i=2; i<6; i++){
                if(row.getCell(i)!=null){
                    ObjectNode objectNode = objectMapper.createObjectNode();
                    objectNode.put("label", labels.get(i-2));
                    objectNode.put("value", getCellValue(row.getCell(i),sheet.getSheetName(),rowIndex, i, String.class));
                    arrayNode.add(objectNode);
                }
            }
            questionDto.setOptions(arrayNode);
            questionDtoList.add(questionDto);
        }
        return questionDtoList;
    }

    private List<QuestionDto> parseQASheet(Sheet sheet){
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for(int rowIndex=1; rowIndex<sheet.getLastRowNum(); rowIndex++){
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            QuestionDto questionDto = new QuestionDto();
            questionDto.setType(QuesType.QA);
            questionDto.setOrder(rowIndex);
            questionDto.setQuestion(getCellValue(row.getCell(1),sheet.getSheetName(),rowIndex, 1,String.class));
            questionDto.setAnswer(getCellValue(row.getCell(2),sheet.getSheetName(),rowIndex, 2,String.class));
            questionDto.setScore(getCellValue(row.getCell(3),sheet.getSheetName(),rowIndex, 3,Double.class));
            questionDto.setDifficulty(getCellValue(row.getCell(4),sheet.getSheetName(),rowIndex, 4,Integer.class));
            String knowledge = getCellValue(row.getCell(5),sheet.getSheetName(),rowIndex, 5,String.class);
            questionDto.setKnowledge(splitString(knowledge, "，", true));
            if(row.getCell(6)==null){
                questionDto.setIsRandom(false);
            }else {
                questionDto.setRandomType(getCellValue(row.getCell(6), sheet.getSheetName(), rowIndex, 6, Integer.class));
                questionDto.setRandomNumber(getCellValue(row.getCell(7), sheet.getSheetName(), rowIndex, 7, Integer.class));
                questionDto.setIsRandom(questionDto.getRandomType()!=null);
            }
            questionDtoList.add(questionDto);
        }
        return questionDtoList;
    }

    /**
     * 通用字符串拆分方法
     * @param originalStr 原始字符串（可为null）
     * @param separator 拆分符（如逗号、竖线等）
     * @param ignoreEmpty 是否忽略拆分后的空元素（true: 忽略，false: 保留）
     * @return 拆分后的字符串列表，若原始字符串为null/空则返回空列表
     */
    private static List<String> splitString(String originalStr, String separator, boolean ignoreEmpty) {
        List<String> result = new ArrayList<>();

        // 处理空值情况
        if (originalStr == null || originalStr.trim().isEmpty()) {
            return result;
        }

        // 拆分字符串
        String[] parts = originalStr.split(separator);

        // 处理拆分结果
        for (String part : parts) {
            String trimmedPart = part.trim();
            // 根据ignoreEmpty参数决定是否忽略空元素
            if (ignoreEmpty) {
                if (!trimmedPart.isEmpty()) {
                    result.add(trimmedPart);
                }
            } else {
                result.add(trimmedPart);
            }
        }

        return result;
    }

    private ExamInfoEntity parseExamInfoSheet(Sheet sheet){
        Row row = sheet.getRow(1);
        ExamInfoEntity examInfoEntity = new ExamInfoEntity();
        examInfoEntity.setName(getCellValue(row.getCell(0), sheet.getSheetName(), 1, 0,String.class));
        examInfoEntity.setDuration(getCellValue(row.getCell(1), sheet.getSheetName(), 1, 1, Integer.class));
        examInfoEntity.setTotalScore(getCellValue(row.getCell(2), sheet.getSheetName(), 1, 2, Double.class));
        examInfoEntity.setPassScore(getCellValue(row.getCell(3), sheet.getSheetName(), 1, 3, Double.class));
        examInfoEntity.setStartTime(getCellValue(row.getCell(4), sheet.getSheetName(), 1, 4, Date.class));
        examInfoEntity.setEndTime(getCellValue(row.getCell(5), sheet.getSheetName(), 1, 5, Date.class));
        return examInfoEntity;
    }


    /**
     * 优化后的单元格值获取方法
     * @param cell 单元格对象
     * @param sheetName sheet名称
     * @param rowNum 实际行号（用户视角，从1开始）
     * @param colNum 实际列号（用户视角，从1开始）
     * @param targetType 目标返回类型
     * @return 转换后的目标类型数据，转换失败返回null
     */
    private <T> T getCellValue(Cell cell, String sheetName, int rowNum, int colNum, Class<T> targetType) {
        // 空单元格直接记录异常并返回null
        if (cell == null) {
            String errorMsg = String.format("Sheet[%s] 第%d行第%d列：单元格为空，无法转换为%s类型",
                    sheetName, rowNum+1, colNum+1, targetType.getSimpleName());
            cellParseErrors.computeIfAbsent(sheetName, k -> new ArrayList<>());
            cellParseErrors.get(sheetName).add(errorMsg);
            return null;
        }

        try {
            Object cellValue = getRawCellValue(cell);
            // 转换为目标类型
            return convertToTargetType(cellValue, targetType);
        } catch (Exception e) {
            // 捕获转换异常，记录信息并返回null
            String errorMsg = String.format("Sheet[%s] 第%d行第%d列：%s，目标类型：%s",
                    sheetName, rowNum+1, colNum+1, e.getMessage(), targetType.getSimpleName());
            cellParseErrors.computeIfAbsent(sheetName, k -> new ArrayList<>());
            cellParseErrors.get(sheetName).add(errorMsg);
            return null;
        }
    }

    /**
     * 获取单元格原始值（不做类型转换）
     */
    private Object getRawCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue(); // 日期类型直接返回Date
                } else {
                    yield cell.getNumericCellValue(); // 数字返回Double
                }
            }
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> {
                // 公式单元格计算后获取值
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                yield getRawCellValue(evaluator.evaluateInCell(cell));
            }
            default -> null;
        };
    }

    /**
     * 将原始值转换为目标类型
     */
    private <T> T convertToTargetType(Object rawValue, Class<T> targetType) {
        if (rawValue == null) {
            throw new IllegalArgumentException("单元格值为空");
        }

        // 相同类型直接返回
        if (targetType.isInstance(rawValue)) {
            return targetType.cast(rawValue);
        }

        // 不同类型转换逻辑
        try {
            if (targetType == String.class) {
                return targetType.cast(rawValue.toString());
            } else if (targetType == Integer.class) {
                if (rawValue instanceof Number num) {
                    return targetType.cast(num.intValue());
                } else if (rawValue instanceof String str) {
                    return targetType.cast(Integer.parseInt(str));
                }
            } else if (targetType == Long.class) {
                if (rawValue instanceof Number num) {
                    return targetType.cast(num.longValue());
                } else if (rawValue instanceof String str) {
                    return targetType.cast(Long.parseLong(str));
                }
            } else if (targetType == Double.class) {
                if (rawValue instanceof Number num) {
                    return targetType.cast(num.doubleValue());
                } else if (rawValue instanceof String str) {
                    return targetType.cast(Double.parseDouble(str));
                }
            } else if (targetType == Date.class) {
                if (rawValue instanceof String str) {
                    // 字符串转日期
                    return targetType.cast(DATE_FORMAT.parse(str));
                }
            } else if (targetType == Boolean.class) {
                if (rawValue instanceof String str) {
                    return targetType.cast(Boolean.parseBoolean(str));
                }
            }
        } catch (NumberFormatException | ParseException e) {
            throw new IllegalArgumentException("类型转换失败：" + e.getMessage());
        }

        // 不支持的转换类型
        throw new IllegalArgumentException("不支持将" + rawValue.getClass().getSimpleName() + "转换为" + targetType.getSimpleName());
    }

    /**
     * 检查单元格解析异常，有异常则抛出自定义异常
     */
    private static void checkCellParseErrors() {
        // 收集所有sheet的异常信息
        List<String> allErrors = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : cellParseErrors.entrySet()) {
            allErrors.addAll(entry.getValue());
        }

        // 有异常则抛出
        if (!allErrors.isEmpty()) {
            throw new ExcelCellParseException("Excel单元格解析失败，共" + allErrors.size() + "处错误:"+allErrors);
        }
    }

    // 重置异常列表（可选，防止多次解析时异常累积）
    private static void resetErrors() {
        cellParseErrors.clear();
    }
}
