package com.iecube.community.model.exportProgress.util;

import com.iecube.community.model.exportProgress.dto.PstReportCommentDto;
import com.iecube.community.model.exportProgress.dto.PstReportDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GradeExcelGenEmdV4 {
    public void genSheetOverview(Workbook workbook,
                                 Map<Long, List<PstReportDTO>> pstReportDTOListGroupByPT,
                                 Map<String, List<PstReportDTO>> pstReportDTOGroupByStudentId) throws Exception{
        try{
            List<PstReportDTO> studentCourseScore = pstReportDTOListGroupByPT.entrySet().iterator().next().getValue(); // 通过某一个实验的信息获取课程总成绩
            Sheet sheet = workbook.createSheet("成绩");
            Row headerRow1 = sheet.createRow(0);
            Row headerRow2 = sheet.createRow(1);
            // 合并单元格 填充表头内容
            CellRangeAddress headerMerge0 = new CellRangeAddress(0, 1, 0, 0);
            CellRangeAddress headerMerge1 = new CellRangeAddress(0, 1, 1, 1);
            CellRangeAddress headerMerge2 = new CellRangeAddress(0, 1, 2, 2);
            CellRangeAddress headerMerge3 = new CellRangeAddress(0, 1, 3, 3);
            sheet.addMergedRegion(headerMerge0);
            sheet.addMergedRegion(headerMerge1);
            sheet.addMergedRegion(headerMerge2);
            sheet.addMergedRegion(headerMerge3);
            headerRow1.createCell(0).setCellValue("学号");
            headerRow1.createCell(1).setCellValue("姓名");
            headerRow1.createCell(2).setCellValue("得分");
            headerRow1.createCell(3).setCellValue("课程总分");
            Map<Long, Integer> excelPTColIndexMap = new HashMap<>();
            AtomicInteger taskStartIndex= new AtomicInteger(4);
            pstReportDTOListGroupByPT.keySet().forEach(PTId->{
                headerRow2.createCell(taskStartIndex.get()).setCellValue("得分");
                headerRow2.createCell(taskStartIndex.get()+1).setCellValue("总分");
                headerRow2.createCell(taskStartIndex.get()+2).setCellValue("权重");
                excelPTColIndexMap.put(PTId, taskStartIndex.get()); // 保存这个实验在excel中的列index，合并单元格前左上角的列index
                sheet.addMergedRegion(new CellRangeAddress(0,0, taskStartIndex.get(), taskStartIndex.get() +2)); // 合并
                headerRow1.createCell(taskStartIndex.get()).setCellValue(pstReportDTOListGroupByPT.get(PTId).get(0).getTaskName());
                taskStartIndex.addAndGet(3);
            });
            // 写入数据
            for(int i=0; i<studentCourseScore.size();i++){
                Row row = sheet.createRow(i+2);
                row.createCell(0).setCellValue(studentCourseScore.get(i).getStudentId());
                row.createCell(1).setCellValue(studentCourseScore.get(i).getStudentName());
                row.createCell(2).setCellValue(studentCourseScore.get(i).getPsScore());
                row.createCell(3).setCellValue(100);
                for(PstReportDTO pst : pstReportDTOGroupByStudentId.get(studentCourseScore.get(i).getStudentId())){
                    // 哪个实验
                    int colIndexStart = excelPTColIndexMap.get(pst.getPtId());
                    row.createCell(colIndexStart).setCellValue(pst.getTaskScore());
                    row.createCell(colIndexStart+1).setCellValue(pst.getTaskTotalScore());
                    row.createCell(colIndexStart+2).setCellValue(pst.getTaskWeighting());
                }
            }
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    public void genTaskView(Workbook workbook, String sheetName, List<PstReportDTO> pstReportDTOList, Map<Long,List<PstReportCommentDto>> pstComponentsMap ) throws Exception{
        try {
            Sheet sheet = workbook.createSheet(sheetName);
            Row headerRow1 = sheet.createRow(0);
            Row headerRow2 = sheet.createRow(1);
            Row headerRow3 = sheet.createRow(2);
            // 合并单元格 填充表头内容
            CellRangeAddress headerMerge0 = new CellRangeAddress(0, 2, 0, 0);
            CellRangeAddress headerMerge1 = new CellRangeAddress(0, 2, 1, 1);
            CellRangeAddress headerMerge2 = new CellRangeAddress(0, 2, 2, 2);
            CellRangeAddress headerMerge3 = new CellRangeAddress(0, 2, 3, 3);
            sheet.addMergedRegion(headerMerge0);
            sheet.addMergedRegion(headerMerge1);
            sheet.addMergedRegion(headerMerge2);
            sheet.addMergedRegion(headerMerge3);
            headerRow1.createCell(0).setCellValue("学号");
            headerRow1.createCell(1).setCellValue("姓名");
            headerRow1.createCell(2).setCellValue("得分");
            headerRow1.createCell(3).setCellValue("实验总分");
            List<PstReportCommentDto> components = pstComponentsMap.entrySet().iterator().next().getValue();
            Map<String, Integer> excelComponetColIndexMap = new HashMap<>();
            AtomicInteger taskStartIndex= new AtomicInteger(4);
            Map<Integer, Integer> stageSizeMap = new HashMap<>();
            stageSizeMap.put(0,0);
            stageSizeMap.put(1,0);
            stageSizeMap.put(2,0);
            components.forEach(component->{
                stageSizeMap.compute(component.getBlockStage(),(k, size) -> size==null?0:size+2 );

                headerRow3.createCell(taskStartIndex.get()).setCellValue("得分");
                headerRow3.createCell(taskStartIndex.get()+1).setCellValue("总分");
                String key = component.getBlockStage().toString()+
                        component.getBlockLevel().toString()+
                        component.getBlockOrder().toString()+
                        component.getComOrder().toString();
                excelComponetColIndexMap.put(key, taskStartIndex.get()); // 保存这个component在excel中的列index，合并单元格前左上角的列index
                sheet.addMergedRegion(new CellRangeAddress(1,1, taskStartIndex.get(), taskStartIndex.get() +1)); // 合并
                headerRow2.createCell(taskStartIndex.get()).setCellValue(component.getName());
                taskStartIndex.addAndGet(2);
            });
            // 处理第一行实验前中后
            if(stageSizeMap.get(0)>0){
                sheet.addMergedRegion(new CellRangeAddress(0,0, 4,  4+stageSizeMap.get(0)-1 )); // 合并
                headerRow1.createCell(4).setCellValue("实验前");
            }
            if(stageSizeMap.get(1)>0){
                sheet.addMergedRegion(new CellRangeAddress(0,0, 4+stageSizeMap.get(0), 4+stageSizeMap.get(0)+stageSizeMap.get(1)-1)); // 合并
                headerRow1.createCell(4+stageSizeMap.get(0)).setCellValue("实验中");
            }
            if(stageSizeMap.get(2)>0){
                sheet.addMergedRegion(new CellRangeAddress(0,0, 4+stageSizeMap.get(0)+stageSizeMap.get(1),  4+stageSizeMap.get(0)+stageSizeMap.get(1)+stageSizeMap.get(2)-1 )); // 合并
                headerRow1.createCell(4+stageSizeMap.get(0)+stageSizeMap.get(1)).setCellValue("实验后");
            }

            // 写入数据
            for(int i=0; i<pstReportDTOList.size();i++){
                Row row = sheet.createRow(i+3);
                row.createCell(0).setCellValue(pstReportDTOList.get(i).getStudentId());
                row.createCell(1).setCellValue(pstReportDTOList.get(i).getStudentName());
                row.createCell(2).setCellValue(pstReportDTOList.get(i).getTaskScore());
                row.createCell(3).setCellValue(pstReportDTOList.get(i).getTaskTotalScore());
                // component 的数据
                for(PstReportCommentDto component : pstComponentsMap.get(pstReportDTOList.get(i).getPstId())){
                    String key = component.getBlockStage().toString()+
                            component.getBlockLevel().toString()+
                            component.getBlockOrder().toString()+
                            component.getComOrder().toString();
                    int colIndex = excelComponetColIndexMap.get(key);
                    row.createCell(colIndex).setCellValue(component.getScore());
                    row.createCell(colIndex+1).setCellValue(component.getTotalScore());
                }
            }
        }catch (Exception e){
            throw new Exception(e);
        }
    }
}
