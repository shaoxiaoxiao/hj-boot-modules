package com.hj.modules.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hj.modules.log.dao.OperateLogMapper;
import com.hj.modules.log.model.bean.OperateLogBean;
import com.hj.modules.log.service.OperateLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hj
 * @since 2023-12-06
 */
@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLogBean> implements OperateLogService {

    @Override
    public void insertOperateLog(OperateLogBean object) {
        save(object);
    }
}
