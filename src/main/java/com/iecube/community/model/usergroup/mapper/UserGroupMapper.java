package com.iecube.community.model.usergroup.mapper;

import com.iecube.community.model.usergroup.entity.UserGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserGroupMapper {
    /**
     * 新增用户组
     * @param userGroup userGroup 对象
     * @return 受影响的行数
     */
    Integer insert(UserGroup userGroup);

    Integer update(UserGroup userGroup);

    UserGroup findById(Integer id);

    UserGroup findCreatorById(Integer id);

    /**
     * 根据用户组的创建人查询用户组
     * @param creator creator
     * @return UserGroup对象
     */
    List<UserGroup> findByCreator(Integer creator);

    Integer delete(Integer id);

    /**
     *  * delete 后续操作 根据用户组的id更新用户的用户组为null
     * @param id 用户组id
     * @param lastModifiedId 最后操作人
     * @param lastModifiedTime 最后操作时间
     * @return
     */
    Integer userUpdateUserGroup(Integer id, Integer lastModifiedId, Date lastModifiedTime);

    /**
     * 统计用户组内的用户数量
     * @param id 用户组id
     * @return 用户组的数量
     */
    Integer countUsersByUserGroup(Integer id);

}
