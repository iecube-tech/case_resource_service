package com.iecube.community.model.duplicate_checking.service.Impl;

import com.iecube.community.model.duplicate_checking.dto.Similarity;
import com.iecube.community.model.duplicate_checking.dto.TaskStudentPDFFile;
import com.iecube.community.model.duplicate_checking.entity.RepetitiveRate;
import com.iecube.community.model.duplicate_checking.mapper.DuplicateCheckingMapper;
import com.iecube.community.model.duplicate_checking.service.DuplicateCheckingService;
import com.iecube.community.model.duplicate_checking.service.ex.NoPDFFilesException;
import com.iecube.community.model.duplicate_checking.service.ex.NoRepetitiveRateVoException;
import com.iecube.community.model.duplicate_checking.vo.RepetitiveRateVo;
import com.iecube.community.util.PdfFilesContentRepeatability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Service
public class DuplicateCheckingServiceImpl implements DuplicateCheckingService {

    @Value("${resource-location}/file")
    private String files;

    @Autowired
    private DuplicateCheckingMapper duplicateCheckingMapper;

    @Override
    public Void DuplicateCheckingByTaskId(Integer taskId) {
        List<Integer> pstIdList = duplicateCheckingMapper.getPSTIdsByTaskId(taskId);
        List<TaskStudentPDFFile> TaskAllStudentsFiles = new ArrayList<>();
        for(Integer id:pstIdList){
            List<TaskStudentPDFFile> studentFileList = duplicateCheckingMapper.getStudentFileListByPSTId(id);
            for(TaskStudentPDFFile studentFile: studentFileList){
                TaskAllStudentsFiles.add(studentFile);
            }
        }
        if(TaskAllStudentsFiles.size()<=1){
            throw new NoPDFFilesException("本任务/实验下没有可以对比的文件");
        }

        for(TaskStudentPDFFile studentFile: TaskAllStudentsFiles){
            for (TaskStudentPDFFile contrastFile:TaskAllStudentsFiles){
                if(studentFile.getStudentId() == contrastFile.getStudentId()){
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
                File fileA = new File(this.files, studentFile.getFileName());
                File fileB = new File(this.files, contrastFile.getFileName());
                Similarity similarity = PdfFilesContentRepeatability.getSimilarity(fileA,fileB);
                repetitiveRate.setRepetitiveRate(similarity.getSimilarity());
                repetitiveRate.setRepetitiveContent(similarity.getContent());
                duplicateCheckingMapper.insertRepetitiveRate(repetitiveRate);
            }
        }
        return null;
    }

    @Override
    public Void DuplicateCheckingByPSTid(Integer pstId){
        Integer taskId = duplicateCheckingMapper.getTaskIdByPSTId(pstId);
        this.DuplicateCheckingByTaskId(taskId);
        return null;
    }

    @Override
    public List<RepetitiveRateVo> getRepetitiveRateByTask(Integer taskId){
        List<RepetitiveRateVo> repetitiveRateVoList = duplicateCheckingMapper.getRepetitiveRateVoByTaskId(taskId);
        if(repetitiveRateVoList.size()==0){
            throw new NoRepetitiveRateVoException("没有该案例的数据");
        }
        return repetitiveRateVoList;
    }

    @Override
    public Void regenerate(Integer taskId) {
        duplicateCheckingMapper.deleteRepetitiveRate(taskId);
        this.DuplicateCheckingByTaskId(taskId);
        return null;
    }

}
