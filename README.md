基于netty实现的web框架,命名为Glue（胶水）。

创作Glue主要目的是为了巩固jdk8,netty知识点，掌握整个MVC框架、RPC框架设计流程。 将Glue定位为一个简约高效的Web框架。

本项目计划分几个步骤实施：
- 基于netty框架支持http、https、websocket请求；
- 实现IOC，http请求路由；
- 支持数据持久化、文件上传、静态资源;
- 支持protobuf,支持RPC的服务注册、服务发现;
- 扩展负载均衡算法、熔断策略;


已实现功能：
-支持http
-IOC、http路由

目前框架仍然需要不断完善，只能用于学习交流，如果希望在线上使用可以选用业界两个优秀框架，我也是参考他们实现的：
[SpringBoot](http://projects.spring.io/spring-boot/)

[Blade MVC](https://lets-blade.com/)
