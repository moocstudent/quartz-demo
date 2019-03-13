package com.how2java.test1;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * 任务监听器:
 * 任务执行之前和执行之后都可以得到通知,这样的好处就是明确的知道,任务是否执行过了.
 * 对于业务要求严谨的系统,可以把这些信息保存到数据库里,将来回头查看哪些Job执行了,哪些Job没有执行.
 */
public class MailJobListener implements JobListener {

    /**
     * getName()方法返回一个字符串用以说明JobListener的名称.
     * 对于注册为全局的监听器,getName()主要用于记录日志,对于由
     * 特定Job引用的JobListener,注册在JobDetail上的监听器名称
     * 必须匹配从监听器上getName()方法的返回值.
     * @return
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "listener of mail job";
    }

    /**
     * Scheduler在JobDetail即将被执行,但又被TriggerListener否决了时调用这个方法.
     * @param context
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // TODO Auto-generated method stub
        System.out.println("取消执行：\t "+context.getJobDetail().getKey());
    }

    /**
     * Scheduler在JobDetail将要被执行时调用这个方法.
     * @param context
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        // TODO Auto-generated method stub
        System.out.println("准备执行：\t "+context.getJobDetail().getKey());
    }

    /**
     * Scheduler在JobDetail被执行之后调用这个方法.
     * @param context
     * @param arg1
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException arg1) {
        // TODO Auto-generated method stub
        System.out.println("执行结束：\t "+context.getJobDetail().getKey());
        System.out.println();
    }

}
