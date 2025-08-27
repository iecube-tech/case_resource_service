package com.iecube.community.model.EMDV4Project.EMDV4_component.service.impl;

import com.iecube.community.model.EMDV4Project.EMDV4_component.mapper.EMDV4ComponentMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_component.service.EMDV4ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EMDV4ComponentServiceImpl implements EMDV4ComponentService {
    @Autowired
    private EMDV4ComponentMapper emdV4ComponentMapper;

}
