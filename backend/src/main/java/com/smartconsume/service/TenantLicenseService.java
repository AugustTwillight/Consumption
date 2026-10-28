package com.smartconsume.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartconsume.common.constant.Constants;
import com.smartconsume.common.exception.BusinessException;
import com.smartconsume.common.result.ResultCode;
import com.smartconsume.entity.SysLicenseLog;
import com.smartconsume.entity.SysTenant;
import com.smartconsume.entity.SysUser;
import com.smartconsume.mapper.SysLicenseLogMapper;
import com.smartconsume.mapper.SysTenantMapper;
import com.smartconsume.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 租户与License服务类
 */
@Slf4j
@Service
public class TenantLicenseService {

    private final SysTenantMapper sysTenantMapper;
    private final SysUserMapper sysUserMapper;
    private final SysLicenseLogMapper sysLicenseLogMapper;

    public TenantLicenseService(SysTenantMapper sysTenantMapper, SysUserMapper sysUserMapper, SysLicenseLogMapper sysLicenseLogMapper) {
        this.sysTenantMapper = sysTenantMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysLicenseLogMapper = sysLicenseLogMapper;
    }

    /**
     * 根据租户ID获取租户信息
     *
     * @param tenantId 租户ID
     * @return 租户信息
     */
    public SysTenant getTenantById(Long tenantId) {
        return sysTenantMapper.selectById(tenantId);
    }

    /**
     * 根据License Key获取租户信息
     *
     * @param licenseKey License Key
     * @return 租户信息
     */
    public SysTenant getTenantByLicenseKey(String licenseKey) {
        LambdaQueryWrapper<SysTenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysTenant::getLicenseKey, licenseKey)
                .eq(SysTenant::getDelFlag, Constants.DEL_FLAG_NOT_DELETED);
        return sysTenantMapper.selectOne(queryWrapper);
    }

    /**
     * 校验License是否有效
     *
     * @param tenant 租户信息
     * @return 是否有效
     */
    public boolean validateLicense(SysTenant tenant) {
        // 超管租户不校验license
        if (tenant.getId() != null && tenant.getId() == Constants.SUPER_ADMIN_TENANT_ID) {
            return true;
        }

        // 检查租户状态
        if (tenant.getStatus() == null || tenant.getStatus() == 0) {
            return false;
        }

        // 检查license是否过期
        if (tenant.getExpireTime() != null && tenant.getExpireTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        // 检查用户数量是否超限
        LambdaQueryWrapper<SysUser> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(SysUser::getTenantId, tenant.getId())
                .eq(SysUser::getDelFlag, Constants.DEL_FLAG_NOT_DELETED);
        long userCount = sysUserMapper.selectCount(userQueryWrapper);

        if (tenant.getMaxUser() != null && tenant.getMaxUser() > 0 && userCount >= tenant.getMaxUser()) {
            return false;
        }

        return true;
    }

    /**
     * 记录License校验日志
     *
     * @param tenantId 租户ID
     * @param result   校验结果：0失败，1成功
     * @param message  校验消息
     */
    public void recordLicenseLog(Long tenantId, Integer result, String message) {
        SysLicenseLog log = new SysLicenseLog();
        log.setTenantId(tenantId);
        log.setCheckTime(LocalDateTime.now());
        log.setResult(result);
        log.setMessage(message);
        sysLicenseLogMapper.insert(log);
    }

    /**
     * 定时校验License（每12小时执行一次）
     */
    @Scheduled(cron = "${license.check-cron:0 0 */12 * * ?}")
    public void scheduledValidateLicense() {
        log.info("开始执行License定时校验任务...");

        LambdaQueryWrapper<SysTenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(SysTenant::getId, Constants.SUPER_ADMIN_TENANT_ID)
                .eq(SysTenant::getDelFlag, Constants.DEL_FLAG_NOT_DELETED);
        java.util.List<SysTenant> tenants = sysTenantMapper.selectList(queryWrapper);

        for (SysTenant tenant : tenants) {
            boolean isValid = validateLicense(tenant);
            if (isValid) {
                recordLicenseLog(tenant.getId(), 1, "License校验成功");
            } else {
                recordLicenseLog(tenant.getId(), 0, "License校验失败：授权过期或用户数超限");
                // 冻结租户
                tenant.setStatus(0);
                sysTenantMapper.updateById(tenant);
                log.warn("租户[{}]License校验失败，已冻结", tenant.getTenantName());
            }
        }

        log.info("License定时校验任务执行完成。");
    }

    /**
     * 检查租户是否被冻结
     *
     * @param tenantId 租户ID
     */
    public void checkTenantFrozen(Long tenantId) {
        if (tenantId == null || tenantId == Constants.SUPER_ADMIN_TENANT_ID) {
            return;
        }

        SysTenant tenant = getTenantById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "租户不存在");
        }

        if (tenant.getStatus() == null || tenant.getStatus() == 0) {
            throw new BusinessException(ResultCode.LICENSE_FROZEN.getCode(), "License校验失败，租户已冻结");
        }

        // 再次校验License
        if (!validateLicense(tenant)) {
            tenant.setStatus(0);
            sysTenantMapper.updateById(tenant);
            recordLicenseLog(tenantId, 0, "License校验失败：授权过期或用户数超限");
            throw new BusinessException(ResultCode.LICENSE_EXPIRED.getCode(), "License已过期，租户功能受限");
        }
    }
}
