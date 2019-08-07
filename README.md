# 分布式任务框架

* * *

## 快速开始

1.  使用注解 [@EnableXScheduling](src/main/java/com/wolken/schedule/EnableXScheduling.java) 注册相关配置；默认使用 ZKQuartz；
    
    [Example: Starter](src/test/java/com/wolken/schedule/Starter.java)
    
    ```java
    @SpringBootApplication  
    @EnableXScheduling  
    public class Starter {  
    
    public static void main(String[] args) { SpringApplication.run(Starter.class); }  
    }
    ```
    
2.  使用注解 [@XSchedule](src/main/java/com/wolken/schedule/XSchedule.java) 声明一个方法可被调度；
    
    [Example: TaskTest](src/main/java/com/wolken/schedule/TaskContext.java)
    
    ```java
    @Service  
    public class CronTest{  
      @XSchedule(cron = "0/1 * * * * ?")  
    public void cron() { System.out.println("Hello");} }
    ```
    

* * *

## 配置

<code>xschedule.enable</code> = 总闸，默认为true，开启后将注册相关Bean

#### Zookeeper作为分布式同步服务；

<ul>  
<li>  
<code>xschedule.zk.server</code> = zk地址，默认127.0.0.1:2181  
<li>  
<code>xschedule.zk.namespace</code> = zk命名空间  
<li>  
<code>xschedule.zk.sleep_time.base</code> = zk重试baseSleepTime，默认1秒  
<li>  
<code>xschedule.zk.sleep_time.max</code> = zk重试maxSleepTime，默认5秒  
<li>  
<code>xschedule.zk.max.retries</code> = zk最大重试次数，默认10次  
<li>  
<code>xschedule.zk.timeout.session</code> = zk session超时时间，默认5秒  
<li>  
<code>xschedule.zk.timeout.connection</code> = zk 建立连接超时时间，默认1秒  
<li>  
<code>xschedule.zk.password</code> = zk ACL 访问控制认证密码，默认无  
</ul>

#### Quartz

提供自定义Quartz上下文接口，于初始化上下文时执行；
 [See: ScheduleContextCustomizedOption](/src/main/java/com/wolken/schedule/ScheduleContextCustomizedOption.java)