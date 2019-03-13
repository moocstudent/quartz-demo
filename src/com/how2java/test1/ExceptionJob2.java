package com.how2java.test1;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 异常后修复参数,立即重新执行
 */
public class ExceptionJob2 implements Job {
    static int i = 0;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            System.out.println("运算结果:"+100/i);
        } catch (Exception e) {
            System.out.println("发生了异常,修改一下参数,立即重新执行.");
            i = 1;
            JobExecutionException je = new JobExecutionException(e);
            je.setRefireImmediately(true);
            throw je;
        }
    }
}
