##源码编译说明
+ 在github将源码拉下来，使用mvn idea:idea将项目转化为idea项目
+ 使用mvn clean install 可以将项目安装到本地
+ 如果需要修改项目版本号，需要先在dubbo-parent项目将reversion 改成自己的版本,
   然后在dubbo-dependencies项目中的reversion改成自己的版本号，
   dubbo-dependencies从目录结构来看，像是dubbo-parent的子模块，
   其实不是
+ 如果在编译项目的同时还想将自己更改的地方打入source包，那么使用
  mvn clean source:jar install -Dmaven.test.skip 命令   