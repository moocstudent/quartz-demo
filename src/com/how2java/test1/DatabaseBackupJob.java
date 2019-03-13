package com.how2java.test1;

import org.quartz.*;

import java.util.Date;

/**
 * 注解@DisallowConcurrentExecution 标明一个任务执行完才执行后一个.不发生混乱
 */
@DisallowConcurrentExecution
public class DatabaseBackupJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail detail = jobExecutionContext.getJobDetail();
        String database = detail.getJobDataMap().getString("database");
        Date date = new Date();
        System.out.printf("给数据库 %s 备份,耗时10秒 %n  currentTime:"+date+"%n",database);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
