package com.smartconsume.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartconsume.entity.SysUser;
import com.smartconsume.entity.UserPointLog;
import com.smartconsume.mapper.SysUserMapper;
import com.smartconsume.mapper.UserPointLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PointService {

    private final UserPointLogMapper userPointLogMapper;
    private final SysUserMapper sysUserMapper;

    public PointService(UserPointLogMapper userPointLogMapper, SysUserMapper sysUserMapper) {
        this.userPointLogMapper = userPointLogMapper;
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 增加积分并记录流水
     *
     * @param tenantId    租户ID
     * @param userId      用户ID
     * @param pointChange 积分变动值（正为获得）
     * @param pointType   类型：earn/consume
     * @param sourceType  来源类型：calc/monitor/post/etc
     * @param sourceId    来源ID
     * @param remark      备注
     */
    public void addPoint(Long tenantId, Long userId, Integer pointChange, String pointType, String sourceType, Long sourceId, String remark) {
        // 1. 插入积分流水
        UserPointLog log = new UserPointLog();
        log.setTenantId(tenantId);
        log.setUserId(userId);
        log.setPointChange(pointChange);
        log.setPointType(pointType);
        log.setSourceType(sourceType);
        log.setSourceId(sourceId);
        log.setRemark(remark);
        userPointLogMapper.insert(log);

        // 2. 更新用户积分余额与等级
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null) {
            user.setPointBalance(user.getPointBalance() + pointChange);
            // 简单等级逻辑：积分 >= 1000 为2级，>= 5000 为3级
            if (user.getPointBalance() >= 5000) {
                user.setLevel(3);
            } else if (user.getPointBalance() >= 1000) {
                user.setLevel(2);
            } else {
                user.setLevel(1);
            }
            sysUserMapper.updateById(user);
            log.info("用户[{}]积分变动：{}，当前余额：{}，等级：{}", userId, pointChange, user.getPointBalance(), user.getLevel());
        }
    }

    /**
     * 查询用户积分流水列表
     */
    public List<UserPointLog> getPointLogList(Long tenantId, Long userId) {
        LambdaQueryWrapper<UserPointLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPointLog::getTenantId, tenantId)
                .eq(UserPointLog::getUserId, userId)
                .orderByDesc(UserPointLog::getCreateTime);
        return userPointLogMapper.selectList(queryWrapper);
    }
}
