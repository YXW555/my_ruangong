package com.second.hand.trading.server.task;

import com.second.hand.trading.server.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 资金释放定时任务
 * 
 * 技术亮点：
 * 1. 使用Spring的@Scheduled注解实现定时任务调度
 * 2. 采用Cron表达式进行精确的时间控制（每天凌晨2点执行，避开业务高峰期）
 * 3. 实现7天售后资金担保机制，保障买卖双方权益
 * 4. 自动化的资金流转，减少人工干预，提高系统可靠性
 * 5. 结合数据库事务保证资金释放的原子性操作
 * 
 * 业务价值：
 * - 保护买家权益：7天售后期内资金由平台保管，支持退款
 * - 保护卖家权益：售后期结束后自动释放资金，无需等待
 * - 提升用户体验：自动化处理，无需人工审核
 * - 降低运营成本：减少客服工作量
 */
@Component
public class FundReleaseTask {

    @Autowired
    private OrderService orderService;

    /**
     * 定时任务：每天凌晨2点执行
     * 
     * Cron表达式说明：
     * - 0 0 2 * * ? 表示：每天凌晨2点0分0秒执行
     * - 格式：秒 分 时 日 月 周
     * - 选择凌晨2点执行的原因：
     *   1. 避开用户活跃高峰期（白天和晚上）
     *   2. 系统负载较低，执行效率高
     *   3. 减少对正常业务的影响
     * 
     * 执行流程：
     * 1. 查询完成时间超过7天的订单
     * 2. 检查是否有活跃的售后申请
     * 3. 批量更新资金状态为"已释放"
     * 4. 记录执行日志
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void releaseFunds() {
        System.out.println("========== 开始执行资金释放定时任务 ==========");
        System.out.println("执行时间：" + new java.util.Date());
        try {
            int count = orderService.releaseFundsToSellers();
            if (count > 0) {
                System.out.println("✓ 资金释放定时任务执行成功，共释放 " + count + " 个订单的资金给卖家");
            } else {
                System.out.println("✓ 本次无需要释放资金的订单");
            }
        } catch (Exception e) {
            System.err.println("✗ 资金释放定时任务执行失败：" + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("========== 资金释放定时任务执行完成 ==========");
    }

    /**
     * 测试用：每5分钟执行一次（仅用于测试，生产环境应注释掉）
     */
    // @Scheduled(fixedRate = 300000) // 300000毫秒 = 5分钟
    // public void releaseFundsForTest() {
    //     System.out.println("[测试] 开始执行资金释放任务...");
    //     try {
    //         int count = orderService.releaseFundsToSellers();
    //         System.out.println("[测试] 资金释放任务执行完成，共释放 " + count + " 个订单的资金");
    //     } catch (Exception e) {
    //         System.err.println("[测试] 资金释放任务执行失败：" + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }
}

