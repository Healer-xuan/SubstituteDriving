package com.atguigu.daijia.rules.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.form.rules.RewardRuleRequestForm;
import com.atguigu.daijia.model.vo.rules.RewardRuleResponseVo;
import com.atguigu.daijia.rules.service.RewardRuleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/rules/reward")
@SuppressWarnings({"unchecked", "rawtypes"})
public class RewardRuleController {

    @Autowired
    private RewardRuleService rewardRuleService;


    /**
     * 计算订单奖励费用
     * @param rewardRuleRequestForm
     * @return
     */
    @Operation(summary = "计算订单奖励费用")
    @PostMapping("/calculateOrderRewardFee")
    public Result<RewardRuleResponseVo> calculateOrderRewardFee(@RequestBody RewardRuleRequestForm rewardRuleRequestForm){
        return Result.ok(rewardRuleService.calculateOrderRewardFee(rewardRuleRequestForm));
    }

}

