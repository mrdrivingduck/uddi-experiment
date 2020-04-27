# uddi-experiment

🖇️ Service registry through jUDDI API.

Created by : Mr Dk.

2020 / 04 / 27 @Ningbo, Zhejiang, China

---

服务计算 (SoC) 实验。

## About [UDDI](https://www.ibm.com/developerworks/cn/webservices/ws-uwsdl/part1/index.html)

> Web 服务描述语言 (WSDL) 是用于描述 Web 服务的一种 XML 语言，它将 Web 服务描述为一组对消息进行操作的网络端点。一个 WSDL 服务描述包含对一组操作和消息的一个抽象定义，绑定到这些操作和消息的一个具体协议，和这个绑定的一个网络端点规范。
>
> 统一描述发现和集成 (UDDI) 提供一种发布和查找服务描述的方法。UDDI 数据实体提供对定义业务和服务信息的支持。WSDL 中定义的服务描述信息是 UDDI 注册中心信息的补充。UDDI 提供对许多不同类型的服务描述的支持。因此，UDDI 没有对 WSDL 的直接支持， 也没有对任何其它服务描述机制的直接支持。
>
> UDDI 组织，即 UDDI.org，已经发布了一个优化方法文档，标题为 在 UDDI 注册中心 1.05 中使用 WSDL (请参阅 参考资料)。这个优化方法文档描述了关于如何在 UDDI 注册中心发布 WSDL 服务描述的一些元素。本文的目的就是增加这种信息。主要的焦点问题是如何将一个完整的 WSDL 服务描述映射到 UDDI 注册中心，现有的 WSDL 工具和运行时环境要求必须做到这一点。本文中的信息遵守那个优化方法文档中列出的大致过程， 且与 WSDL 1.1、UDDI 1.0 和 UDDI 2.0 规范一致。

## jUDDI

[jUDDI](http://juddi.apache.org/) (pronounced "Judy") 是 Apache 实现的 **私有** UDDI 注册中心，可配置在应用服务器或 Servlet 容器中。

## Run jUDDI

本实验使用 jUDDI 3.3.8 release (Mar 15, 2020)，环境为 Windows 10 Professional。下载的是 `juddi-distro-3.3.8.zip`，这里面包含了 jUDDI 的 **客户端** 和 **服务器** (`juddi-client` 是纯客户端)。压缩包中已经自带了一个可以运行的 Tomcat。运行 `juddi-tomcat-3.3.8/bin/startup.bat`。然后通过浏览器访问 `http://localhost:8080`，可以看到 jUDDI 的欢迎页面。

可以以管理员的身份进入管理页面 - `http://localhost:8080/juddiv3/admin/home.jsp`。进入该界面的用户名为 `uddiadmin`，默认密码位于 `juddi-tomcat-3.3.8/conf/tomcat-users.xml` 中。

坑：

* Tomcat 启动后控制台乱码问题 - `cmd` 的代码页为 GBK 编码，而 Tomcat 的配置中使用的编码为 UTF-8
    * 将 `juddi-tomcat-3.3.8/conf/logging.properties` 中的所有 UTF-8 更换为 GBK (虽然我很讨厌这样 🙄)
* 启动 Tomcat 时出现异常：外部 DTD: 无法读取外部 DTD
    * 在 `%JAVA_HOME%/jre/lib` 目录下新建一个 `jaxp.properties`，加入以下两行内容：
        ```
        javax.xml.accessExternalSchema=all
        javax.xml.accessExternalDTD=all
        ```
    * [Reference](https://blog.csdn.net/dingshuo168/article/details/103317453)
* 启动 Tomcat 时出现异常：
    ```
    Caused by: java.lang.IllegalArgumentException: The AJP Connector is configured with secretRequired="true" but the secret attribute is either null or "". This combination is not valid.
    ```
    * 当前 jUDDI release 中自带的 Tomcat 是 9.0.31 版本，网上有将 Tomcat 8 升级至 9 时出现了类似问题
    * 在 `juddi-tomcat-3.3.8/conf/server.xml` 中，添加 `secretRequeired`：
        ```xml
        <!-- Define an AJP 1.3 Connector on port 8009 -->
        <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
        ```
        ```xml
        <!-- Define an AJP 1.3 Connector on port 8009 -->
        <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" secretRequired="" />
        ```
    * [Reference](https://blog.csdn.net/zhangjianming2018/article/details/104447164)

以上过程基本根据 [jUDDI 官方文档](http://juddi.apache.org/docs/3.3/juddi-guide/html/ch02.html) 的步骤进行。

## API

在上述管理界面中，已经可以实现服务的注册、发现等。另外，还可以通过 jUDDI 提供的 API，通过程序来进行服务的注册和发现。文档链接：

* http://juddi.apache.org/docs/3.3/juddi-client-guide/html/ch01.html
* http://juddi.apache.org/docs/3.3/juddi-client-guide/html/ch05.html

通过程序操作完成后，可以在 Web 的管理页面中查看效果。

---

