package com.smartconsume.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartconsume.common.constant.Constants;
import com.smartconsume.common.result.Result;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 凑单方案控制器
 */
@RestController
@RequestMapping("/api/v1/calc-plan")
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
     * @param request 请求参数 {planName, goodsList, ruleList}
     * @return 计算结果
     */
    @PostMapping("/calculate")
    public Result<CalcResult> calculate(@RequestBody Map<String, Object> request) {
        String planName = (String) request.get("planName");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> goodsListData = (List<Map<String, Object>>) request.get("goodsList");
        List<CalcPlanGoodsDTO> goodsList = new java.util.ArrayList<>();
        for (Map<String, Object> data : goodsListData) {
            CalcPlanGoodsDTO dto = new CalcPlanGoodsDTO();
            dto.setGoodsName((String) data.get("goodsName"));
            dto.setGoodsLink((String) data.get("goodsLink"));
            dto.setPlatform((String) data.get("platform"));
            dto.setUnitPrice(new BigDecimal(data.get("unitPrice").toString()));
            dto.setQuantity(((Number) data.get("quantity")).intValue());
            goodsList.add(dto);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ruleListData = (List<Map<String, Object>>) request.get("ruleList");
        List<CalcRuleDTO> ruleList = new java.util.ArrayList<>();
        for (Map<String, Object> data : ruleListData) {
            CalcRuleDTO dto = new CalcRuleDTO();
            dto.setRuleType((String) data.get("ruleType"));
            dto.setRuleCondition(new BigDecimal(data.get("ruleCondition").toString()));
            dto.setRuleDiscount(new BigDecimal(data.get("ruleDiscount").toString()));
            dto.setIsStackable(((Number) data.get("isStackable")).intValue());
            dto.setCategoryLimit((String) data.get("categoryLimit"));
            ruleList.add(dto);
        }

        CalcResult result = calculatorService.calculate(goodsList, ruleList);
        return Result.success(result);
    }

    /**
     * 保存凑单方案
     *
     * @param request 请求参数 {planName, goodsList, ruleList, totalAmount, totalDiscount, finalAmount, riskTags}
     * @param httpRequest HTTP请求
     * @return 保存结果
     */
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> savePlan(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Long tenantId = (Long) httpRequest.getAttribute("tenantId");

        String planName = (String) request.get("planName");
        BigDecimal totalAmount = new BigDecimal(request.get("totalAmount").toString());
        BigDecimal totalDiscount = new BigDecimal(request.get("totalDiscount").toString());
        BigDecimal finalAmount = new BigDecimal(request.get("finalAmount").toString());
        String riskTags = (String) request.get("riskTags");

        // 保存方案主表
        CalcPlan plan = new CalcPlan();
        plan.setTenantId(tenantId);
        plan.setUserId(userId);
        plan.setPlanName(planName);
        plan.setTotalAmount(totalAmount);
        plan.setTotalDiscount(totalDiscount);
        plan.setFinalAmount(finalAmount);
        plan.setRiskTags(riskTags);
        plan.setStatus(1);
        calcPlanMapper.insert(plan);

        // 保存商品子表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> goodsListData = (List<Map<String, Object>>) request.get("goodsList");
        for (Map<String, Object> data : goodsListData) {
            CalcPlanGoods goods = new CalcPlanGoods();
            goods.setPlanId(plan.getId());
            goods.setGoodsName((String) data.get("goodsName"));
            goods.setGoodsLink((String) data.get("goodsLink"));
            goods.setPlatform((String) data.get("platform"));
            goods.setUnitPrice(new BigDecimal(data.get("unitPrice").toString()));
            goods.setQuantity(((Number) data.get("quantity")).intValue());
            calcPlanGoodsMapper.insert(goods);
        }

        // 保存规则子表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ruleListData = (List<Map<String, Object>>) request.get("ruleList");
        for (Map<String, Object> data : ruleListData) {
            CalcRule rule = new CalcRule();
            rule.setPlanId(plan.getId());
            rule.setRuleType((String) data.get("ruleType"));
            rule.setRuleCondition(new BigDecimal(data.get("ruleCondition").toString()));
            rule.setRuleDiscount(new BigDecimal(data.get("ruleDiscount").toString()));
            rule.setIsStackable(((Number) data.get("isStackable")).intValue());
            rule.setCategoryLimit((String) data.get("categoryLimit"));
            calcRuleMapper.insert(rule);
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
