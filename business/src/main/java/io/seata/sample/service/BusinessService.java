package io.seata.sample.service;

import io.seata.sample.feign.OrderFeignClient;
import io.seata.sample.feign.StorageFeignClient;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class BusinessService {
    private final StorageFeignClient storageFeignClient;
    private final OrderFeignClient orderFeignClient;
    private final JdbcTemplate jdbcTemplate;

    public BusinessService(StorageFeignClient storageFeignClient, OrderFeignClient orderFeignClient, JdbcTemplate jdbcTemplate) {
        this.storageFeignClient = storageFeignClient;
        this.orderFeignClient = orderFeignClient;
        this.jdbcTemplate = jdbcTemplate;
    }
    // (为了使用这一行注解代码，才有了这一整个演示项目 -_-# )
    @GlobalTransactional //使用Seata的全局事务注解，每个方法都会被开启一个事务 ，每个方法执行完成后都会提交事务，如果抛出异常，则回滚事务
    public void purchase(String userId, String commodityCode, int orderCount) {
        //1. 减库存
        //2. 下订单
        //3. 校验
        storageFeignClient.deduct(commodityCode, orderCount);
        orderFeignClient.create(userId, commodityCode, orderCount);
        if (!validData()) {
            throw new RuntimeException("账户或库存不足,执行回滚");
        }
    }

    public boolean validData() {
        Map<String, Object> accountMap = jdbcTemplate.queryForMap("select * from account_tbl where user_id='U100000'");
        if (Integer.parseInt(accountMap.get("money").toString()) < 0) {
            return false;
        }
        Map<String, Object> storageMap = jdbcTemplate.queryForMap("select * from storage_tbl where commodity_code='C100000'");
        return Integer.parseInt(storageMap.get("count").toString()) >= 0;
    }

    @PostConstruct
    public void initData() {
        jdbcTemplate.update("delete from account_tbl");
        jdbcTemplate.update("delete from order_tbl");
        jdbcTemplate.update("delete from storage_tbl");
        jdbcTemplate.update("insert into account_tbl(user_id,money) values('U100000','10000') ");
        jdbcTemplate.update("insert into storage_tbl(commodity_code,count) values('C100000','200') ");
    }
}
