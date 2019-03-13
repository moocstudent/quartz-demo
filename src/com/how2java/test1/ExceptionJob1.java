package com.how2java.test1;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 取消job调度
 */
public class ExceptionJob1 implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int i = 0;
        try {
            System.out.println(100/i);
        } catch (Exception e) {
            System.out.println("发生了异常,取消这个Job对应的所有调度.");
            JobExecutionException je = new JobExecutionException(e);
            je.setUnscheduleAllTriggers(true);
            throw je;
        }
    }
}
