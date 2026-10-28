package com.smartconsume.service;

import com.smartconsume.common.constant.Constants;
import com.smartconsume.common.exception.BusinessException;
import com.smartconsume.common.result.ResultCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 凑单计算器核心算法服务
 */
@Slf4j
@Service
public class CalculatorService {

    /**
     * 凑单计算结果
     */
    @Data
    public static class CalcResult {
        private List<CalcPlanGoodsDTO> goodsList;
        private List<CalcRuleDTO> ruleList;
        private BigDecimal totalAmount;
        private BigDecimal totalDiscount;
        private BigDecimal finalAmount;
        private List<String> riskTags;
        private List<CalcScenario> scenarios;
    }

    @Data
    public static class CalcPlanGoodsDTO {
        private String goodsName;
        private String goodsLink;
        private String platform;
        private BigDecimal unitPrice;
        private Integer quantity;
    }

    @Data
    public static class CalcRuleDTO {
        private String ruleType;
        private BigDecimal ruleCondition;
        private BigDecimal ruleDiscount;
        private Integer isStackable;
        private String categoryLimit;
    }

    @Data
    public static class CalcScenario {
        private String scenarioName;
        private BigDecimal totalAmount;
        private BigDecimal totalDiscount;
        private BigDecimal finalAmount;
        private List<String> riskTags;
        private List<GoodsAllocation> goodsAllocations;
    }

    @Data
    public static class GoodsAllocation {
        private String goodsName;
        private BigDecimal unitPrice;
        private BigDecimal discountedPrice;
        private BigDecimal actualPaid;
    }

    /**
     * 执行凑单计算
     *
     * @param goodsList 商品列表
     * @param ruleList  优惠规则列表
     * @return 计算结果
     */
    public CalcResult calculate(List<CalcPlanGoodsDTO> goodsList, List<CalcRuleDTO> ruleList) {
        if (goodsList == null || goodsList.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED.getCode(), "商品列表不能为空");
        }

        if (goodsList.size() > Constants.MAX_CALC_GOODS_COUNT) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED.getCode(), "商品数量不能超过15件");
        }

        // 计算商品总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CalcPlanGoodsDTO goods : goodsList) {
            BigDecimal goodsTotal = goods.getUnitPrice().multiply(new BigDecimal(goods.getQuantity()));
            totalAmount = totalAmount.add(goodsTotal);
        }

        // 过滤并排序规则
        List<CalcRuleDTO> validRules = filterAndSortRules(ruleList);

        // 枚举可行组合
        List<CalcScenario> scenarios = enumerateScenarios(goodsList, validRules, totalAmount);

        // 排序场景
        scenarios.sort((s1, s2) -> s2.getTotalDiscount().compareTo(s1.getTotalDiscount()));

        // 生成风险标签
        List<String> riskTags = generateRiskTags(goodsList, validRules);

        // 生成最优方案
        CalcResult result = new CalcResult();
        result.setGoodsList(goodsList);
        result.setRuleList(validRules);
        result.setTotalAmount(totalAmount);
        result.setTotalDiscount(scenarios.isEmpty() ? BigDecimal.ZERO : scenarios.get(0).getTotalDiscount());
        result.setFinalAmount(scenarios.isEmpty() ? totalAmount : scenarios.get(0).getFinalAmount());
        result.setRiskTags(riskTags);
        result.setScenarios(scenarios);

        return result;
    }

    /**
     * 过滤并排序规则
     */
    private List<CalcRuleDTO> filterAndSortRules(List<CalcRuleDTO> ruleList) {
        if (ruleList == null || ruleList.isEmpty()) {
            return new ArrayList<>();
        }

        // 按优先级排序：券 > 满减 > 津贴 > 折扣
        Map<String, Integer> priorityMap = new HashMap<>();
        priorityMap.put("category_coupon", 1);
        priorityMap.put("shop_coupon", 2);
        priorityMap.put("full_reduce", 3);
        priorityMap.put("cross_store_discount", 4);
        priorityMap.put("discount", 5);

        List<CalcRuleDTO> validRules = new ArrayList<>();
        for (CalcRuleDTO rule : ruleList) {
            if (rule.getRuleDiscount() != null && rule.getRuleDiscount().compareTo(BigDecimal.ZERO) > 0) {
                validRules.add(rule);
            }
        }

        validRules.sort((r1, r2) -> {
            int p1 = priorityMap.getOrDefault(r1.getRuleType(), 99);
            int p2 = priorityMap.getOrDefault(r2.getRuleType(), 99);
            return Integer.compare(p1, p2);
        });

        return validRules;
    }

    /**
     * 枚举可行组合
     */
    private List<CalcScenario> enumerateScenarios(List<CalcPlanGoodsDTO> goodsList, List<CalcRuleDTO> ruleList, BigDecimal totalAmount) {
        List<CalcScenario> scenarios = new ArrayList<>();

        // 如果没有规则，直接返回基础场景
        if (ruleList == null || ruleList.isEmpty()) {
            CalcScenario scenario = new CalcScenario();
            scenario.setScenarioName("基础方案");
            scenario.setTotalAmount(totalAmount);
            scenario.setTotalDiscount(BigDecimal.ZERO);
            scenario.setFinalAmount(totalAmount);
            scenario.setRiskTags(new ArrayList<>());

            List<GoodsAllocation> allocations = new ArrayList<>();
            for (CalcPlanGoodsDTO goods : goodsList) {
                GoodsAllocation allocation = new GoodsAllocation();
                allocation.setGoodsName(goods.getGoodsName());
                allocation.setUnitPrice(goods.getUnitPrice());
                allocation.setDiscountedPrice(goods.getUnitPrice());
                allocation.setActualPaid(goods.getUnitPrice().multiply(new BigDecimal(goods.getQuantity())));
                allocations.add(allocation);
            }
            scenario.setGoodsAllocations(allocations);
            scenarios.add(scenario);
            return scenarios;
        }

        // 计算应用规则后的方案
        CalcScenario bestScenario = calculateScenarioWithRules(goodsList, ruleList, totalAmount);
        scenarios.add(bestScenario);

        return scenarios;
    }

    /**
     * 计算应用规则后的场景
     */
    private CalcScenario calculateScenarioWithRules(List<CalcPlanGoodsDTO> goodsList, List<CalcRuleDTO> ruleList, BigDecimal totalAmount) {
        CalcScenario scenario = new CalcScenario();
        scenario.setScenarioName("最优凑单方案");
        scenario.setTotalAmount(totalAmount);

        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<String> riskTags = new ArrayList<>();

        // 应用规则
        for (CalcRuleDTO rule : ruleList) {
            if (canApplyRule(rule, totalAmount, totalDiscount)) {
                // 检查是否可叠加
                if (rule.getIsStackable() != null && rule.getIsStackable() == 0) {
                    // 不可叠加，只应用第一个规则
                    totalDiscount = totalDiscount.add(rule.getRuleDiscount());
                    break;
                } else {
                    totalDiscount = totalDiscount.add(rule.getRuleDiscount());
                }
            }
        }

        // 限制折扣不能超过总金额
        if (totalDiscount.compareTo(totalAmount) > 0) {
            totalDiscount = totalAmount;
        }

        BigDecimal finalAmount = totalAmount.subtract(totalDiscount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        scenario.setTotalDiscount(totalDiscount);
        scenario.setFinalAmount(finalAmount);
        scenario.setRiskTags(riskTags);

        // 商品分摊计算
        List<GoodsAllocation> allocations = new ArrayList<>();
        for (CalcPlanGoodsDTO goods : goodsList) {
            GoodsAllocation allocation = new GoodsAllocation();
            allocation.setGoodsName(goods.getGoodsName());
            allocation.setUnitPrice(goods.getUnitPrice());

            // 简单分摊：按商品金额比例分摊优惠
            BigDecimal goodsTotal = goods.getUnitPrice().multiply(new BigDecimal(goods.getQuantity()));
            BigDecimal ratio = goodsTotal.divide(totalAmount, 4, RoundingMode.HALF_UP);
            BigDecimal discountShare = totalDiscount.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
            BigDecimal actualPaid = goodsTotal.subtract(discountShare).setScale(2, RoundingMode.HALF_UP);

            allocation.setDiscountedPrice(goods.getUnitPrice().subtract(discountShare.divide(new BigDecimal(goods.getQuantity()), 2, RoundingMode.HALF_UP)));
            allocation.setActualPaid(actualPaid.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : actualPaid);
            allocations.add(allocation);
        }
        scenario.setGoodsAllocations(allocations);

        return scenario;
    }

    /**
     * 判断是否可以应用规则
     */
    private boolean canApplyRule(CalcRuleDTO rule, BigDecimal totalAmount, BigDecimal currentDiscount) {
        if (rule.getRuleCondition() == null || rule.getRuleCondition().compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        return totalAmount.compareTo(rule.getRuleCondition()) >= 0;
    }

    /**
     * 生成风险标签
     */
    private List<String> generateRiskTags(List<CalcPlanGoodsDTO> goodsList, List<CalcRuleDTO> ruleList) {
        List<String> riskTags = new ArrayList<>();

        // 检查限时限量风险
        for (CalcRuleDTO rule : ruleList) {
            if ("cross_store_discount".equals(rule.getRuleType())) {
                riskTags.add("限时限量风险");
                break;
            }
        }

        // 检查售后风险（多平台组合）
        Set<String> platforms = new HashSet<>();
        for (CalcPlanGoodsDTO goods : goodsList) {
            if (goods.getPlatform() != null) {
                platforms.add(goods.getPlatform());
            }
        }
        if (platforms.size() > 1) {
            riskTags.add("跨平台售后风险");
        }

        return riskTags;
    }
}
