package com.iecube.community.model.Exam.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@TableName("Exam_ques_content")
public class QuesContentEntity {
    private String id;
    private String pId;
    private String title;
    private JsonNode options;
    private String optionsString;
    private String answer;
    private String knowledgeStr;
    private List<String> knowledge;

    public void setOptions(JsonNode options) {
        this.options = options;
        if(options != null){
            try{
                this.optionsString = new ObjectMapper().writeValueAsString(options);
            }catch (JsonProcessingException e){
                this.optionsString = null;
            }
        }
    }

    public void setOptionsString(String optionsString) {
        this.optionsString = optionsString;
        if(optionsString != null && !optionsString.isEmpty()){
            try{
                this.options = new ObjectMapper().readTree(optionsString);
            }catch (JsonProcessingException e){
                this.options = null;
            }
        }
    }

    public void setKnowledge(List<String> knowledge) {
        this.knowledge = knowledge;
        if(knowledge != null && !knowledge.isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            knowledge.forEach(k->stringBuilder.append(k).append(","));
            this.knowledgeStr = stringBuilder.toString();
        }
    }

    public void setKnowledgeStr(String knowledgeStr) {
        if(knowledgeStr==null || knowledgeStr.isEmpty()){
            this.knowledgeStr = null;
            this.knowledge = null;
            return;
        }
        this.knowledgeStr = knowledgeStr;
        List<String> k = new ArrayList<>();
        String[] parts = knowledgeStr.split(",");
        for(String part:parts){
            String trimmedPart = part.trim();
            if(!trimmedPart.isEmpty()){
                k.add(trimmedPart);
            }
        }
        this.knowledge = k;
    }
}
