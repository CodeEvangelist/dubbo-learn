---------------
2020-08-12
#####dubbo的启动过程包含几个步骤：
+ 初始化数据
+ 检查数据、包括协议等等
+ 暴露服务（其实是将接口和协议绑定在指定的端口，使其能和外界通信）
+ 注册服务 （获取所有的注册中心，将服务的地址端口协议等等服务信息保存在注册中心）
+ 引用服务，生成invoker代理对象，用来调用远程服务
---------------------
2020-08-20
#####dubbo-demo模块的了解
+ spring整合dubbo流程梳理
+ 更好的方式研究dubbo启动过程
---------------------
2020-08-25
#####dubbo服务暴露过程
+ 获取对应的暴露协议，可能会有多个协议
+ 判断是否是调度或者延迟导出，否则立即导出
+ 首先会被FilterWapper拦截，转发到RegisterWapper
+ RegiterWapper会先调用doLocalExport，这个最终会转发到对应的协议导出类，例如是dubbo协议
  那么就会转发到DubboProtocol，DubboProtocol就是使用netty将服务开放出去
+ 接着RegisterWapper就会开始判断是否要注册到注册中心，如果要到注册中心，那么就会注册到注册中心
-------------------- 
2020-08-26
#####dubbo的consumer服务引用过程
+ 见dubbo-demo项目的dubbo-demo-annotation-consumer下的ConsumerApplication
--------------------
2020-08-27
#####dubbo的wapper感受
+ 其实dubbo中对于wapper的配置文件，解析完之后并不会马上创建Extension，而是放在一个wapper的缓存中
  后面创建真正的扩展类，就会包装真正的扩展类，所以并不是所有的dubbo SPI配置文件中的配置都会通过Extension
  来创建，可能是直接反射创建的等等~~
--------------------
2020-09-05
#####dubbo的Protocol,Registry自适应过程
+ 设计的比较精妙，巧妙的利用了java的子类父类的调用，配合dubbo的spi机制
  灵活的实现了多协议暴露、多注册中心类型
--------------------