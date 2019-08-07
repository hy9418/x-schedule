# 分布式任务框架

* * *

## 快速开始

1.  使用注解 [@EnableRapScheduling](src/main/java/com/wolken/schedule/RapScheduleMode.java) 注册相关配置；默认使用 RapQuartz；
    
    [Example: Starter](src/test/java/com/ifugle/rap/schedule/Starter.java)
    
    ```java
    @SpringBootApplication  
    @EnableRapScheduling(mode = RapScheduleMode.RAP_QUARTZ)  
    public class Starter {  
    
    public static void main(String[] args) { SpringApplication.run(Starter.class); }  
    }
    ```
    
2.  使用注解 [@RapSchedule](src/main/java/com/ifugle/rap/schedule/RapSchedule.java) 声明一个方法可被调度；
    
    [Example: TaskTest](src/main/java/com/ifugle/rap/schedule/TaskContext.java)
    
    ```java
    @Service  
    public class CronTest{  
      @RapSchedule(cron = "0/1 * * * * ?")  
    public void cron() { System.out.println("Hello");} }
    ```
    

* * *

## 配置

<code>rap.schedule.enable</code> = 总闸，默认为true，开启后将注册相关Bean

#### Zookeeper作为分布式同步服务；

<ul>  
<li>  
<code>rap.zk.server</code> = zk地址，默认127.0.0.1:2181  
<li>  
<code>rap.zk.namespace</code> = zk命名空间  
<li>  
<code>rap.zk.sleep_time.base</code> = zk重试baseSleepTime，默认1秒  
<li>  
<code>rap.zk.sleep_time.max</code> = zk重试maxSleepTime，默认5秒  
<li>  
<code>rap.zk.max.retries</code> = zk最大重试次数，默认10次  
<li>  
<code>rap.zk.timeout.session</code> = zk session超时时间，默认5秒  
<li>  
<code>rap.zk.timeout.connection</code> = zk 建立连接超时时间，默认1秒  
<li>  
<code>rap.zk.password</code> = zk ACL 访问控制认证密码，默认无  
</ul>

#### Quartz

提供自定义Quartz上下文接口，于初始化上下文时执行；
 [See: ScheduleContextCustomizedOption](/src/main/java/com/ifugle/rap/schedule/ScheduleContextCustomizedOption.java)