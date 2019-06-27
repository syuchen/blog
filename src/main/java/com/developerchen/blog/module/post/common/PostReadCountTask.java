package com.developerchen.blog.module.post.common;

import com.alibaba.druid.pool.DruidDataSource;
import com.developerchen.blog.module.post.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 定期将缓存中的post阅读次数持久化到数据库中
 *
 * @author syc
 */
@Component
public class PostReadCountTask {

    @Autowired
    private IPostService postService;

    @Autowired(required = false)
    private DataSource dataSource;

    @Async
    @Scheduled(initialDelay = 1000, fixedRate = 60000)
    public void updatePostReadCount() {
        // 未更新到数据中的post的新增加的阅读次数
        Map<Long, AtomicInteger> postReadCountMap = PostEventListener.getPostReadCountCacheMap();
        if (postReadCountMap.isEmpty()) {
            return;
        }

        if (dataSource != null && dataSource instanceof DruidDataSource) {
            // 获取当前连接池信息
            Map<String, Object> statData = ((DruidDataSource) dataSource).getStatData();
            int poolingCount = (int) statData.getOrDefault("PoolingCount", 0);
            int activeCount = (int) statData.getOrDefault("ActiveCount", 0);

            if ((poolingCount - activeCount) <= 1) {
                // 如果数据库连接池空闲连接没有超过一个放弃本次更新，尽量闲时做更新动作
                return;
            }
        }

        Map<Long, AtomicInteger> tempCountMap = new HashMap<>(postReadCountMap);
        postReadCountMap.clear();

        for (Map.Entry<Long, AtomicInteger> entry : tempCountMap.entrySet()) {
            postService.increasePostReadCount(entry.getKey(), entry.getValue().get());
        }
    }
}
