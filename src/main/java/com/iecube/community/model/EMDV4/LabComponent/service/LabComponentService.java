package com.iecube.community.model.EMDV4.LabComponent.service;

import com.iecube.community.model.EMDV4.LabComponent.entity.LabComponent;

public interface LabComponentService {
    LabComponent create(LabComponent labComponent);

    void delete(Long id);

    LabComponent update(LabComponent labComponent);
}
