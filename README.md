# spring-boot scheduler

spring-boot 任务调度项目。

email: 519955464@qq.com

---

### 配置示例

#### 1. yml配置文件
application-scheduler.yml
````
scheduler:
    database: false
    configs:
            # 任务id
        -   id: test_one_task_5
            # 执行任务的实现类，可以自定义任务执行内容
            task: testTwoTask
            # corn表达式
            corn: "0/5 * * * * ?"
            # 任务描述
            desc: 测试定时任务配置2

        -   id: test_one_task_3
            task: testOneTask
            corn: "0/3 * * * * ?"
            desc: 测试定时任务配置1

        -   id: test_one_task_4
            # 使用http实现任务调度，需要配合url属性一起使用
            task: httpTask
            corn: "0/4 * * * * ?"
            desc: 测试http定时任务
            # http发起请求的url，只支持get方式调用
            url: http://www.baidu.com
````

#### 2. 开发自定义扩展任务
自定义任务需要实现`com.wpj.servicescheduler.task.service.BaseTaskService`接口。
组件`@Scope`为`BeanDefinition.SCOPE_PROTOTYPE`类型，因为组件中会包含当前任务的调度属性，
使用单例模式会造成组件状态混乱。
````
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TestTwoTask extends BaseTaskService {
    /**
     * 定时任务实现方法
     */
    @Override
    public void doTask() {
        log.info("当前任务id：[{}]", this.getScheduler().getId());
    }
}
````

#### 3. 数据库持久化任务
默认项目在内存中管理任务，启动时从配置文件读取执行的任务，设置为数据库读取后，会从数据库中读取配置，
并对操作进行记录

#### 4. 通过api接口调整定时任务
##### 4.1  添加
不传递`corn`参数则只调用一次，可以实现手动触发调度任务。
配置为不使用数据库则当前任务只存在内存中，重启服务后任务丢失
````
post http://localhost:9070/task

body {
     	"id": "wpj_2",
     	"task":"httpTask",
     	"corn":"0/10 * * * * ?",
     	"desc":"接口添加任务",
     	"url": "http://www.baidu.com"
     }
````

##### 4.2  删除
立即移除指定任务
````
delete http://localhost:9070/task/{taskId}
````

##### 4.3  获取任务列表
立即移除指定任务
````
get http://localhost:9070/task
````

##### 4.4  更新任务配置
立即移除原任务，并重新添加任务
````
put http://localhost:9070/task

body {
        "id": "wpj_2",
        "task":"testOneTask",
        "corn":"0/10 * * * * ?",
        "desc":"接口更新任务"
     }
````