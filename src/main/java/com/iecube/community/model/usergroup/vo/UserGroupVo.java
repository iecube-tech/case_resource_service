package com.iecube.community.model.usergroup.vo;

import com.iecube.community.model.auth.entity.Authority;
import com.iecube.community.model.teacher.vo.TeacherVo;
import lombok.Data;

import java.util.List;

@Data
public class UserGroupVo {
    private Integer id;
    private String name;
    private List<Authority> authorities;
    private List<TeacherVo> teacherList;
}
