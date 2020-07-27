##公共逻辑模块：包括 Util 类和通用模型

##serialize 层放在 common 模块中，以便更大程度复用

##dubbo的SPI整体关系加载过程
+ 1、首先使用ExtensionLoader.getExtensionLoader时会构建一个叫ExtensionFactory的loader，这个loader和
加载我们自定的拓展类的loader一样，也是一个ExtensionLoader，但是这个loader是用来加载dubbo自己的一些东西
例如AdaptiveExtension，加载过程和加载我们自定义的一些拓展是一样的，但是唯一不同的是它没有自己的
ExtensionFactory

+ 2、第二步就是将加载得到的ExtensionFactory再创建一个新的ExtensionLoader，这样对于每一个type(可以认为是一个接口)
就会有自己的一个ExtensionLoader和ExtensionFactory，加载的过程就是读取META-INF/dubbo/下的文件，然后一行一行的解析
如果有Adptive或者Active或者Wrapper那么就会单独存储，不会跟所有的扩展类存储在一起，减少了内存的占用（因为每个都只存一份）

##dubbo的自适应扩展机制的真正用途
###问题:
    为什么不直接了当的使用如下代码:
    ExtensionLoader.getExtensionLoader(xxx.class).getExtension("xxx")来加载拓展类，这样也能实现
    在启动初期不加载没有必要的扩展类，直到调用方法才加载对应的扩展类？
###原因:
    1、如果有多个方法那么需要写多次，而且可能是一些用户定义的扩展类，这样用户也需要去写判断,
    2、使用getAdaptiveExtension()动态获取一个自适应拓展类帮助我们减少重复代码，实现约定优于配置