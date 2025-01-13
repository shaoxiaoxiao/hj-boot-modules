package com.hj.modules.log.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
@JsonIgnoreProperties
public class UserInfo implements Serializable {
    private Long userId;
    private String account;
    private Integer userMode;

    /**
     * 是否是飞手
     */
    private Boolean isFlyer;
    /**
     * 用户当前部门
     */
    private Long departmentId;

    /**
     * 当前用户有权限操作的部门。是否包括子部门，根据 {@link com.hj.mc.model.constant.UserRolePermissionTypeEnum} 决定
     * 可能为 null
     */
    private List<Long> departmentIds;

    /**
     * 是否为管理员(关联的部门 aid=1 && pid=0 的根部门)
     */
    private Boolean isAdmin;

    /**
     * 权限校验是否是当前部门获取子部门
     *
     * @param organizeId
     * @return
     */
    public Boolean validatePermissionDepartmentIds(Long organizeId) {
        // 不是管理员 && 不属于本部门 && 不属于自己

        return getDepartmentIds().contains(organizeId);
    }

    /**
     * 权限校验是否是当前部门
     *
     * @param organizeId
     * @return
     */
    public Boolean validatePermissionDepartmentId(Long organizeId) {
        // 不是管理员 && 不属于本部门 && 不属于自己
        return Objects.equals(getDepartmentId(), organizeId);
    }

    /**
     * 权限校验是否是当前用户
     *
     * @param userId
     * @return
     */
    public Boolean validatePermissionUserId(Long userId) {
        // 不是管理员 && 不属于本部门 && 不属于自己
        return Objects.equals(getUserId(), userId);
    }
}
