package com.iecube.community.model.pst_resource.entity;

import com.iecube.community.model.resource.entity.Resource;
import lombok.Data;

@Data
public class PSTResourceVo {
    Integer id;
    Integer pstId;
    Resource resource;
    Resource readOver;
}
