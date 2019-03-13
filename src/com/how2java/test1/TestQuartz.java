package com.how2java.test1;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

import java.util.Date;


public class TestQuartz {

    //设置通过不同的时间段执行任务(示例)        todo 根据设定的时间获取对应的date对象
    private static void setFutureDate(Date date, String dateType) {
        //10秒后
        Date startTime = DateBuilder.futureDate(10, DateBuilder.IntervalUnit.SECOND);
        //20秒后
        Date startTime1 = DateBuilder.futureDate(20, DateBuilder.IntervalUnit.SECOND);
        //3分钟后
        Date startTime2 = DateBuilder.futureDate(3, DateBuilder.IntervalUnit.MINUTE);
        //5小时后
        Date startTime3 = DateBuilder.futureDate(5, DateBuilder.IntervalUnit.HOUR);
        //5天后
        Date startTime4 = DateBuilder.futureDate(5, DateBuilder.IntervalUnit.DAY);
        //3个月后
        Date startTime5 = DateBuilder.futureDate(3, DateBuilder.IntervalUnit.MONTH);
        //1年后
        Date startTime6 = DateBuilder.futureDate(1, DateBuilder.IntervalUnit.YEAR);
    }


    public static void main(String[] args) throws Exception {
//        databaseCurrentJob();
//        mailJob();
//        exceptionHandle1();
//        exceptionHandle2();
//        stop();
//        mailJob2();
        /* 20秒后执行任务 */
        Date startTime1 = DateBuilder.futureDate(10, DateBuilder.IntervalUnit.SECOND);
//        Date startTime2 = DateBuilder.futureDate(1,DateBuilder.IntervalUnit.MINUTE);
//        mailJobWithTime(startTime2);
//        repeatMailJob(startTime1);
//        repeatForever(startTime1);
//        cronJob(startTime1);
        testJobListener(startTime1);

    }


    /**
     * 终止任务  scheduler.interrupt(job.getKey());
     * @throws Exception
     */
    private static void stop() throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        Trigger trigger = newTrigger().withIdentity("trigger1", "group1")
                .startNow()
                .build();

        //定义一个JobDetail
        JobDetail job = newJob(StoppableJob.class)
                .withIdentity("exceptionJob1", "someJobGroup")
                .build();

        //调度加入这个job
        scheduler.scheduleJob(job, trigger);

        //启动
        scheduler.start();

        Thread.sleep(5000);
        System.out.println("过5秒，调度停止 job");

        //key 就相当于这个Job的主键
        scheduler.interrupt(job.getKey());

        //等待20秒，让前面的任务都执行完了之后，再关闭调度器
        Thread.sleep(20000);
        scheduler.shutdown(true);
    }

    /**
     * 异常处理2 异常后修复参数,立即重新执行
     * @throws Exception
     */
    private static void exceptionHandle2() throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        Trigger trigger = newTrigger().withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(2)
                        .withRepeatCount(10))
                .build();

        //定义一个JobDetail
        JobDetail job = newJob(ExceptionJob2.class)
                .withIdentity("exceptionJob1", "someJobGroup")
                .build();

        //调度加入这个job
        scheduler.scheduleJob(job, trigger);

        //启动
        scheduler.start();

        //等待20秒，让前面的任务都执行完了之后，再关闭调度器
        Thread.sleep(20000);
        scheduler.shutdown(true);
    }


    /**
     * 异常处理1  取消job调度
     * @throws Exception
     */
    private static void exceptionHandle1() throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        Trigger trigger = newTrigger().withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(2)
                        .withRepeatCount(10))
                .build();

        //定义一个JobDetail
        JobDetail job = newJob(ExceptionJob1.class)
                .withIdentity("exceptionJob1", "someJobGroup")
                .build();

        //调度加入这个job
        scheduler.scheduleJob(job, trigger);

        //启动
        scheduler.start();

        //等待20秒，让前面的任务都执行完了之后，再关闭调度器
        Thread.sleep(20000);
        scheduler.shutdown(true);
    }


    /**
     * 调用数据库备份任务(假设)
     * @throws Exception
     */
    private static void databaseCurrentJob() throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        Trigger trigger = newTrigger().withIdentity("trigger1", "group1").startNow().withSchedule(simpleSchedule().withIntervalInSeconds(2).withRepeatCount(10)).build();

        //定义一个JobDetail
        JobDetail job = newJob(DatabaseBackupJob.class).withIdentity("backupjob", "databasegroup").usingJobData("database", "how2java").build();

        //调度加入这个job
        scheduler.scheduleJob(job, trigger);

        //启动
        scheduler.start();

        //等待200秒,让前面的任务都执行完了之后,再关闭调度器
        Thread.sleep(200000);
        scheduler.shutdown(true);


    }


    /*====下方的方法从下往上看起====*/



    /**
     * 增加Job监听
     * @param startTime
     * @throws Exception
     */
    private static void testJobListener(Date startTime)throws Exception{
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = newJob(MailJob.class).withIdentity("mailJob","mailGroup").usingJobData("email","chinaBaby@hostinbj.com").build();
        CronTrigger trigger = newTrigger().withIdentity("trigger1","group1")
                .withSchedule(cronSchedule("0/2 * * * * ?")).build();

        //增加Job监听
        MailJobListener mailJobListener = new MailJobListener();
        KeyMatcher<JobKey> keyMatcher = KeyMatcher.keyEquals(job.getKey());
        scheduler.getListenerManager().addJobListener(mailJobListener,keyMatcher);

        // schedule it to run! 调度加入job,返回的ft为第一次执行job的日期
        Date ft = scheduler.scheduleJob(job,trigger);

        System.out.println("使用的Cron表达式是: "+trigger.getCronExpression());

        scheduler.start();

        //等待20秒,让前面的任务都执行完成之后,再关闭调度器.
        Thread.sleep(20000);
        scheduler.shutdown(true);
    }


    /**
     * cronSchedule("0/2 * * * * ?")  每2秒执行一次,一直执行
     * @param startTime
     * @throws Exception
     */
    private static void cronJob(Date startTime)throws Exception{
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = newJob(MailJob.class).withIdentity("mailJob","mailGroup").usingJobData("email","chinaBaby@hostinbj.com").build();
        CronTrigger trigger = newTrigger().withIdentity("trigger1","group1")
                .withSchedule(cronSchedule("0/2 * * * * ?")).build();

        // schedule it to run!
        Date ft = scheduler.scheduleJob(job,trigger);

        System.out.println("使用的Cron表达式是: "+trigger.getCronExpression());

        scheduler.start();

        //等待200秒,让前面的任务都执行完成之后,再关闭调度器.
        Thread.sleep(200000);
        scheduler.shutdown(true);
    }



    /**
     * 有重复时间( .repeatForever()) 一直重复,开始时间
     * @param startTime 开始时间
     * @throws Exception
     */
    private static void repeatForever(Date startTime)throws Exception{
        //获取调度器
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDetail job = newJob(MailJob.class)
                .withIdentity("mailJob", "mailGroup")//任务分组 mailGroup.mailJob
                .usingJobData("email", "zq@elove1234.com")//使用任务数据
                .build();

        SimpleTrigger trigger = (SimpleTrigger) newTrigger()
                .withIdentity("trigger1", "group1") //触发分组
                .startAt(startTime) //开始触发器时间(开始任务时间)
                .withSchedule(simpleSchedule()
                        .repeatForever() //任务一直重复forever
                        .withIntervalInSeconds(1)) //间隔时间(秒) 1秒
                .build();

        //schedule it to run !  ft:第一次执行该job的时间
        Date ft = scheduler.scheduleJob(job, trigger);

        System.out.println("scheduler.scheduleJob(job, trigger):" + ft);

        System.out.println("当前时间是: " + new Date().toLocaleString());
        //无限重复 累计次数是显示的0，因为没法表示无限。。
        System.out.printf("%s 这个任务会在 %s 准时开始运行,累计运行%d次,间隔时间是%d毫秒%n", job.getKey(),
                ft.toLocaleString(), trigger.getRepeatCount() + 1, trigger.getRepeatInterval());

        scheduler.start();

        //等待200秒,让前面的任务都执行完成了之后,再关闭调度器.
        Thread.sleep(200000);
        scheduler.shutdown(true);

    }


    /**
     * 有重复时间,间隔时间
     * @param startTime 开始时间
     * @throws Exception
     */
    private static void repeatMailJob(Date startTime) throws Exception {
        //获取调度器
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDetail job = newJob(MailJob.class)
                .withIdentity("mailJob", "mailGroup")//任务分组 mailGroup.mailJob
                .usingJobData("email", "zq@elove1234.com")//使用任务数据
                .build();

        SimpleTrigger trigger = (SimpleTrigger) newTrigger()
                .withIdentity("trigger1", "group1") //触发分组
                .startAt(startTime) //开始触发器时间(开始任务时间)
                .withSchedule(simpleSchedule()
                        .withRepeatCount(3)  //再重复此次任务几次
                        .withIntervalInSeconds(1)) //间隔时间
                .build();

        //schedule it to run !  ft:第一次执行该job的时间
        Date ft = scheduler.scheduleJob(job, trigger);

        System.out.println("scheduler.scheduleJob(job, trigger):" + ft);

        System.out.println("当前时间是: " + new Date().toLocaleString());
        System.out.printf("%s 这个任务会在 %s 准时开始运行,累计运行%d次,间隔时间是%d毫秒%n", job.getKey(),
                ft.toLocaleString(), trigger.getRepeatCount() + 1, trigger.getRepeatInterval());

        scheduler.start();

        //等待200秒,让前面的任务都执行完成了之后,再关闭调度器.
        Thread.sleep(200000);
        scheduler.shutdown(true);

    }

    /**
     * 设定任务开始时间
     * @param startTime 开始时间
     * @throws Exception
     */
    private static void mailJobWithTime(Date startTime) throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//        Date startTime = DateBuilder.futureDate(10, DateBuilder.IntervalUnit.SECOND);
        JobDetail job = newJob(MailJob.class).withIdentity("mailJob", "mailGroup").usingJobData("email", "testTime@admin.com").build();
        SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity("trigger1", "group1").startAt(startTime).build();
        Date ft = scheduler.scheduleJob(job, trigger);
        System.out.println("当前时间是:" + new Date().toLocaleString());
        System.out.printf("%s 这个任务会在 %s 准时开始运行,累计运行%d次,间隔时间是%d毫秒%n", job.getKey(),
                ft.toLocaleString(), trigger.getRepeatCount() + 1, trigger.getRepeatInterval());
        scheduler.start();

        //等待200秒,让前面的任务都执行完了之后,再关闭调度器
        Thread.sleep(200000);
        scheduler.shutdown(true);
    }

    //普通方式
    private static void mailJob2() throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        //下一个8秒的倍数时间 如09:43:08秒 ,09:43:16秒......
        Date startTime = DateBuilder.nextGivenSecondDate(null, 8);
        JobDetail job = newJob(MailJob.class).withIdentity("mailJob", "mailGroup").usingJobData("email", "abc@qq.com").build();
        SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity("trigger1", "group1").startAt(startTime).build();

        Date ft = scheduler.scheduleJob(job, trigger);

        System.out.println("当前时间是:" + new Date().toLocaleString());
        System.out.printf("%s 这个任务会在 %s 准时开始运行,累计运行%d次,间隔时间是%d毫秒%n", job.getKey(), ft.toLocaleString(), trigger.getRepeatCount() + 1, trigger.getRepeatInterval());

        scheduler.start();

        Thread.sleep(200000);
        scheduler.shutdown(true);
    }

    //普通方式 更改job job.getJobDataMap().put("email", "admin@baobao.com");
    private static void mailJob() throws Exception {
        //创建调度器
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        //定义一个触发器
        Trigger trigger = newTrigger().withIdentity("trigger1", "group1") //定义名称和所属的租
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(2) //每隔2秒执行一次
                        .withRepeatCount(10)) //总共执行11次(第一次执行不基数)
                .build();

        //定义一个JobDetail
        JobDetail job = newJob(MailJob.class) //指定干活的类MailJob
                .withIdentity("mailjob1", "mailgroup") //定义任务名称和分组
                .usingJobData("email", "admin@10086.com") //定义属性
                .build();

        //类似替换了key value,原来的admin@10086.com被新的email的值取代了
        job.getJobDataMap().put("email", "admin@baobao.com");

        //调度加入这个job
        scheduler.scheduleJob(job, trigger);

        //启动
        scheduler.start();

        //等待20秒，让前面的任务都执行完了之后，再关闭调度器
        Thread.sleep(20000);
        scheduler.shutdown(true);
    }
}
