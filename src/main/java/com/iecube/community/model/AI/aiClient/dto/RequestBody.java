package com.iecube.community.model.AI.aiClient.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBody {
    private String chat_id;
    private Payload payload;
    private String module_name;
    private Object module_source;
    private String agent_name;
    private String llm_model_override;

    // 构造函数、getter 和 setter 方法
    public RequestBody(String chat_id, Payload payload, String module_name, Object module_source, String agent_name, String llm_model_override) {
        this.chat_id = chat_id;
        this.payload = payload;
        this.module_name = module_name;
        this.module_source = module_source;
        this.agent_name = agent_name;
        this.llm_model_override = llm_model_override;
    }
}
