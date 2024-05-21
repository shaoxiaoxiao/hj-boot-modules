package com.hj.modules.log.model.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author hj
 * @since 2023-12-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_user_operate_log")
@ApiModel(value="UserOperateLogBean对象", description="")
@Builder
public class OperateLogBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.NONE)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "操作模块")
    private String module;

    @ApiModelProperty(value = "操作内容")
    private String content;

    @ApiModelProperty(value = "请求路径")
    private String url;

    @ApiModelProperty(value = "请求方法")
    private String method;

    @ApiModelProperty(value = "请求参数")
    private String param;

    @ApiModelProperty(value = "请求结果 0失败 1成功")
    private Integer result;

    @ApiModelProperty(value = "失败原因")
    private String failReason;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
