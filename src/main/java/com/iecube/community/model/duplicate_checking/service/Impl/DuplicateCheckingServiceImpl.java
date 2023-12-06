package com.iecube.community.model.duplicate_checking.service.Impl;

import com.iecube.community.model.duplicate_checking.dto.TaskStudentPDFFile;
import com.iecube.community.model.duplicate_checking.entity.RepetitiveRate;
import com.iecube.community.model.duplicate_checking.mapper.DuplicateCheckingMapper;
import com.iecube.community.model.duplicate_checking.service.DuplicateCheckingService;
import com.iecube.community.util.PdfFilesContentRepeatability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Service
public class DuplicateCheckingServiceImpl implements DuplicateCheckingService {

    @Autowired
    private DuplicateCheckingMapper duplicateCheckingMapper;

    @Override
    public Void DuplicateCheckingByPSTid(Integer pstId) {
        Integer taskId = duplicateCheckingMapper.getTaskIdByPSTId(pstId);
        List<Integer> pstIdList = duplicateCheckingMapper.getPSTIdsByTaskId(taskId);
        List<TaskStudentPDFFile> TaskAllStudentsFiles = new ArrayList<>();
        for(Integer id:pstIdList){
            List<TaskStudentPDFFile> studentFileList = duplicateCheckingMapper.getStudentFileListByPSTId(id);
            for(TaskStudentPDFFile studentFile: studentFileList){
                TaskAllStudentsFiles.add(studentFile);
            }
        }
        if(TaskAllStudentsFiles.size()<=1){
            return null;
        }
        for(TaskStudentPDFFile studentFile: TaskAllStudentsFiles){
            for (TaskStudentPDFFile contrastFile:TaskAllStudentsFiles){
                if(studentFile.getFileName().equals(contrastFile.getFileName())){
                    continue;
                }
                RepetitiveRate repetitiveRate = new RepetitiveRate();
                repetitiveRate.setProjectId(studentFile.getProjectId());
                repetitiveRate.setTaskId(studentFile.getTaskId());
                repetitiveRate.setStudentId(studentFile.getStudentId());
                repetitiveRate.setPstId(studentFile.getPstId());
                repetitiveRate.setResourceId(studentFile.getResourceId());
                repetitiveRate.setFileName(studentFile.getFileName());
                repetitiveRate.setContrastStudentId(contrastFile.getStudentId());
                repetitiveRate.setContrastResourceId(contrastFile.getResourceId());
                repetitiveRate.setContrastFileName(contrastFile.getFileName());
                File fileA = new File(""+studentFile.getFileName());
                File fileB = new File("" + contrastFile.getFileName());
                Double res = PdfFilesContentRepeatability.getSimilarity(fileA,fileB);
                repetitiveRate.setRepetitiveRate(res);
                //插入数据库
            }
        }

        System.out.println(TaskAllStudentsFiles);
        return null;
    }
}
