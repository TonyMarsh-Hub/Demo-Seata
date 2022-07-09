package io.seata.sample.controller;

import io.seata.sample.service.BusinessService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusinessController {
    private final BusinessService service;

    public BusinessController(BusinessService service) {
        this.service = service;
    }

    /**
     * 购买下单，模拟全局事务提交
     *
     * @return 结果
     */
    @RequestMapping(value = "/purchase/commit", produces = "application/json")
    public String purchaseCommit() {
        try {
            service.purchase("U100000", "C100000", 30);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "全局事务提交";
    }


    /**
     * 购买下单，模拟全局事务回滚
     * 账户或库存不足
     *
     * @return 结果
     */
    @RequestMapping("/purchase/rollback")
    public String purchaseRollback() {
        try {
            service.purchase("U100000", "C100000", 99999);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "全局事务提交";
    }
}
