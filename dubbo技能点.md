------------------------
* spring(通过spring容器起来的aware接口，来启动服务导出，服务引入)
* dubbo-spi(通过传递url方式来决定使用的扩展类)
* javassist(在生成Invoker对象的时候，用来生成代理类)
* wapper(暴露服务时，Protocol使用wapper模式，形成一层灵活扩展接口)
* netty(在使用RPC协议将服务暴露到本地端口的时候)
* zookeeper-curator(使用zookeeper注册中心的时候)
---------------------------------