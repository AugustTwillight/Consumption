package com.smartconsume.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartconsume.common.constant.Constants;
import com.smartconsume.common.result.Result;
import com.smartconsume.dto.CalcPlanCalculateDTO;
import com.smartconsume.dto.CalcPlanGoodsItemDTO;
import com.smartconsume.dto.CalcPlanSaveDTO;
import com.smartconsume.dto.CalcRuleItemDTO;
import com.smartconsume.entity.CalcPlan;
import com.smartconsume.entity.CalcPlanGoods;
import com.smartconsume.entity.CalcRule;
import com.smartconsume.mapper.CalcPlanGoodsMapper;
import com.smartconsume.mapper.CalcPlanMapper;
import com.smartconsume.mapper.CalcRuleMapper;
import com.smartconsume.service.CalculatorService;
import com.smartconsume.service.CalculatorService.CalcPlanGoodsDTO;
import com.smartconsume.service.CalculatorService.CalcResult;
import com.smartconsume.service.CalculatorService.CalcRuleDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 凑单方案控制器
 */
@RestController
@RequestMapping("/api/v1/calc-plan")
@Validated
public class CalcPlanController {

    private final CalcPlanMapper calcPlanMapper;
    private final CalcPlanGoodsMapper calcPlanGoodsMapper;
    private final CalcRuleMapper calcRuleMapper;
    private final CalculatorService calculatorService;

    public CalcPlanController(CalcPlanMapper calcPlanMapper, CalcPlanGoodsMapper calcPlanGoodsMapper,
                              CalcRuleMapper calcRuleMapper, CalculatorService calculatorService) {
        this.calcPlanMapper = calcPlanMapper;
        this.calcPlanGoodsMapper = calcPlanGoodsMapper;
        this.calcRuleMapper = calcRuleMapper;
        this.calculatorService = calculatorService;
    }

    /**
     * 执行凑单计算
     *
     * @param dto 请求参数
     * @return 计算结果
     */
    @PostMapping("/calculate")
    public Result<CalcResult> calculate(@Valid @RequestBody CalcPlanCalculateDTO dto) {
        List<CalcPlanGoodsDTO> goodsList = new ArrayList<>();
        if (dto.getGoodsList() != null) {
            for (CalcPlanGoodsItemDTO item : dto.getGoodsList()) {
                CalcPlanGoodsDTO dtoItem = new CalcPlanGoodsDTO();
                dtoItem.setGoodsName(item.getGoodsName());
                dtoItem.setGoodsLink(item.getGoodsLink());
                dtoItem.setPlatform(item.getPlatform());
                dtoItem.setUnitPrice(item.getUnitPrice());
                dtoItem.setQuantity(item.getQuantity());
                goodsList.add(dtoItem);
            }
        }

        List<CalcRuleDTO> ruleList = new ArrayList<>();
        if (dto.getRuleList() != null) {
            for (CalcRuleItemDTO item : dto.getRuleList()) {
                CalcRuleDTO dtoItem = new CalcRuleDTO();
                dtoItem.setRuleType(item.getRuleType());
                dtoItem.setRuleCondition(item.getRuleCondition());
                dtoItem.setRuleDiscount(item.getRuleDiscount());
                dtoItem.setIsStackable(item.getIsStackable());
                dtoItem.setCategoryLimit(item.getCategoryLimit());
                ruleList.add(dtoItem);
            }
        }

        CalcResult result = calculatorService.calculate(goodsList, ruleList);
        return Result.success(result);
    }

    /**
     * 保存凑单方案
     *
     * @param dto 请求参数
     * @param httpRequest HTTP请求
     * @return 保存结果
     */
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> savePlan(@Valid @RequestBody CalcPlanSaveDTO dto, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");

        // 保存方案主表
        CalcPlan plan = new CalcPlan();
        plan.setTenantId(tenantId);
        plan.setUserId(userId);
        plan.setPlanName(dto.getPlanName());
        plan.setTotalAmount(dto.getTotalAmount());
        plan.setTotalDiscount(dto.getTotalDiscount());
        plan.setFinalAmount(dto.getFinalAmount());
        plan.setRiskTags(dto.getRiskTags());
        plan.setStatus(1);
        calcPlanMapper.insert(plan);

        // 保存商品子表
        for (CalcPlanGoodsItemDTO item : dto.getGoodsList()) {
            CalcPlanGoods goods = new CalcPlanGoods();
            goods.setPlanId(plan.getId());
            goods.setGoodsName(item.getGoodsName());
            goods.setGoodsLink(item.getGoodsLink());
            goods.setPlatform(item.getPlatform());
            goods.setUnitPrice(item.getUnitPrice());
            goods.setQuantity(item.getQuantity());
            calcPlanGoodsMapper.insert(goods);
        }

        // 保存规则子表
        if (dto.getRuleList() != null) {
            for (CalcRuleItemDTO item : dto.getRuleList()) {
                CalcRule rule = new CalcRule();
                rule.setPlanId(plan.getId());
                rule.setRuleType(item.getRuleType());
                rule.setRuleCondition(item.getRuleCondition());
                rule.setRuleDiscount(item.getRuleDiscount());
                rule.setIsStackable(item.getIsStackable());
                rule.setCategoryLimit(item.getCategoryLimit());
                calcRuleMapper.insert(rule);
            }
        }

        return Result.success(plan.getId());
    }

    /**
     * 查询方案列表
     *
     * @param httpRequest HTTP请求
     * @return 方案列表
     */
    @GetMapping("/list")
    public Result<List<CalcPlan>> listPlans(HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");
        Long userId = (Long) httpRequest.getAttribute("userId");

        LambdaQueryWrapper<CalcPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CalcPlan::getTenantId, tenantId)
                .eq(CalcPlan::getUserId, userId)
                .eq(CalcPlan::getDelFlag, Constants.DEL_FLAG_NOT_DELETED)
                .orderByDesc(CalcPlan::getCreateTime);

        List<CalcPlan> plans = calcPlanMapper.selectList(queryWrapper);
        return Result.success(plans);
    }

    /**
     * 查询方案详情
     *
     * @param id 方案ID
     * @param httpRequest HTTP请求
     * @return 方案详情
     */
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getPlanDetail(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");

        CalcPlan plan = calcPlanMapper.selectById(id);
        if (plan == null || !plan.getTenantId().equals(tenantId) || plan.getDelFlag() != Constants.DEL_FLAG_NOT_DELETED) {
            return Result.fail(404, "方案不存在");
        }

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("plan", plan);

        LambdaQueryWrapper<CalcPlanGoods> goodsQuery = new LambdaQueryWrapper<>();
        goodsQuery.eq(CalcPlanGoods::getPlanId, id);
        List<CalcPlanGoods> goodsList = calcPlanGoodsMapper.selectList(goodsQuery);
        result.put("goodsList", goodsList);

        LambdaQueryWrapper<CalcRule> ruleQuery = new LambdaQueryWrapper<>();
        ruleQuery.eq(CalcRule::getPlanId, id);
        List<CalcRule> ruleList = calcRuleMapper.selectList(ruleQuery);
        result.put("ruleList", ruleList);

        return Result.success(result);
    }

    /**
     * 删除方案
     *
     * @param id 方案ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deletePlan(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");

        CalcPlan plan = calcPlanMapper.selectById(id);
        if (plan == null || !plan.getTenantId().equals(tenantId)) {
            return Result.fail(404, "方案不存在");
        }

        plan.setDelFlag(1);
        calcPlanMapper.updateById(plan);

        // 软删除商品和规则
        LambdaQueryWrapper<CalcPlanGoods> goodsQuery = new LambdaQueryWrapper<>();
        goodsQuery.eq(CalcPlanGoods::getPlanId, id);
        List<CalcPlanGoods> goodsList = calcPlanGoodsMapper.selectList(goodsQuery);
        for (CalcPlanGoods goods : goodsList) {
            goods.setDelFlag(1);
            calcPlanGoodsMapper.updateById(goods);
        }

        LambdaQueryWrapper<CalcRule> ruleQuery = new LambdaQueryWrapper<>();
        ruleQuery.eq(CalcRule::getPlanId, id);
        List<CalcRule> ruleList = calcRuleMapper.selectList(ruleQuery);
        for (CalcRule rule : ruleList) {
            rule.setDelFlag(1);
            calcRuleMapper.updateById(rule);
        }

        return Result.success();
    }
}
