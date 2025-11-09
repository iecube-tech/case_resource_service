package com.iecube.community.model.exportProgress.vo;

import com.iecube.community.model.exportProgress.entity.ExportProgress;
import com.iecube.community.model.resource.entity.Resource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportProgressVo extends ExportProgress {
    private Resource resource;
}
