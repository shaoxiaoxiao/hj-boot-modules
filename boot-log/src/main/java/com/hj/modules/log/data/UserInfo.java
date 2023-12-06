package com.hj.modules.log.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfo implements Serializable {
    private Long userId;
    private String account;
    private Long companyId;
    private Integer userMode;
    private Long areaId;
    private Long departmentId;
    private Long finalDepartmentId;
    private String realName;
    /**
     * 是否是流程超级管理员
     */
    private Boolean flowManager;
    /**
     * 外部系统用户ID 如浙里办用户id
     */
    private String thirdPartyUserId;
}
