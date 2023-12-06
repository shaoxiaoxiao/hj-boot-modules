package com.hj.modules.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hj.modules.log.model.bean.OperateLogBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hj
 * @since 2023-12-06
 */
public interface OperateLogService extends IService<OperateLogBean> {

    void insertOperateLog(OperateLogBean object);
}
