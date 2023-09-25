<p align="center">
	<a target="_blank" href="https://search.maven.org/search?q=justauth">
		<img src="https://img.shields.io/github/v/release/xingyuv/justauth?style=flat-square" ></img>
	</a>
	<a target="_blank" href="https://oss.sonatype.org/content/repositories/snapshots/com/xingyuv/justauth/">
		<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/com.xingyuv.justauth/justauth.svg?style=flat-square" ></img>
	</a>
	<a target="_blank" href="https://gitee.com/xingyuv/justauth/blob/master/LICENSE">
		<img src="https://img.shields.io/apm/l/vim-mode.svg?color=yellow" ></img>
	</a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img src="https://img.shields.io/badge/JDK-1.8+-green.svg" ></img>
	</a>
	<a target="_blank" href="https://www.justauth.cn" title="参考文档">
		<img src="https://img.shields.io/badge/Docs-latest-blueviolet.svg" ></img>
	</a>
	<a href="https://codecov.io/gh/justauth/justauth">
		<img src="https://codecov.io/gh/justauth/justauth/branch/master/graph/badge.svg?token=zYiAqd9aFz" />
	</a>
	<a href='https://gitee.com/xingyuv/justauth/stargazers'>
	  <img src='https://gitee.com/xingyuv/justauth/badge/star.svg' alt='star'></img>
	</a>
	<a target="_blank" href='https://github.com/xingyuv/justauth'>
		<img src="https://img.shields.io/github/stars/xingyuv/justauth.svg?style=social" alt="github star"></img>
	</a>
</p>


-------------------------------------------------------------------------------

帮助文档：[www.justauth.cn](https://www.justauth.cn)

## 什么是 JustAuth？

JustAuth，如你所见，它仅仅是一个**第三方授权登录**的**工具类库**，它可以让我们脱离繁琐的第三方登录 SDK，让登录变得**So easy!
**

JustAuth
集成了诸如：Github、Gitee、支付宝、新浪微博、微信、Google、Facebook、Twitter、StackOverflow等国内外数十家第三方平台。更多请参考<a href="https://www.justauth.cn" target="_blank">
已集成的平台</a>

## 有哪些特点？

1. **全**
   ：已集成十多家第三方平台（国内外常用的基本都已包含），仍然还在持续扩展中（[开发计划](https://gitee.com/xingyuv/JustAuth/issues/IUGRK)）！
2. **简**：API就是奔着最简单去设计的（见后面`快速开始`），尽量让您用起来没有障碍感！

## 有哪些功能？

- 集成国内外数十家第三方平台，实现快速接入。<a href="https://www.justauth.cn/quickstart/how-to-use.html" target="_blank">
  参考文档</a>
- 自定义 State
  缓存，支持各种分布式缓存组件。<a href="https://www.justauth.cn/features/customize-the-state-cache.html" target="_blank">
  参考文档</a>
- 自定义 OAuth 平台，更容易适配自有的 OAuth
  服务。<a href="https://www.justauth.cn/features/customize-the-oauth.html" target="_blank">参考文档</a>
- 自定义 Http
  实现，选择权完全交给开发者，不会单独依赖某一具体实现。<a href="https://www.justauth.cn/quickstart/how-to-use.html#%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F" target="_blank">
  参考文档</a>
- 自定义 Scope，支持更完善的授权体系。<a href="https://www.justauth.cn/features/customize-scopes.html" target="_blank">
  参考文档</a>
- 更多...<a href="https://www.justauth.cn" target="_blank">参考文档</a>

## 快速开始

### 引入依赖

```xml

<dependency>
    <groupId>com.xingyuv</groupId>
    <artifactId>justauth</artifactId>
    <version>{latest-version}</version>
</dependency>
```

> **latest-version** 可选：
> - 稳定版：![](https://img.shields.io/github/v/release/xingyuv/justauth?style=flat-square)
> - 快照版：![](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.xingyuv/justauth.svg?style=flat-square)
> > 注意：快照版本是功能的尝鲜，并不保证稳定性。请勿在生产环境中使用。
>
> <details>
>   <summary>如何引入快照版本</summary>
>
> JustAuth 的快照版本托管在 ossrh 上，所以要指定下载地址。
>
> ```xml
> <repositories>
>     <repository>
>         <id>ossrh-snapshot</id>
>         <url>https://oss.sonatype.org/content/repositories/snapshots</url>
>         <snapshots>
>             <enabled>true</enabled>
>         </snapshots>
>     </repository>
> </repositories>
> ```
>
> 如果你想第一时间获取 JustAuth 的最新快照，可以添加下列代码，每次构建时都检查是否有最新的快照（默认每天检查）。
>
> ```diff
>        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
>         <snapshots>
> +           <updatePolicy>always</updatePolicy>
>             <enabled>true</enabled>
>         </snapshots>
> ```
>
> </details>

如下**任选一种** HTTP 工具 依赖，
_项目内如果已有，请忽略。另外需要特别注意，如果项目中已经引入了低版本的依赖，请先排除低版本依赖后，再引入高版本或者最新版本的依赖_

- hutool-http

  ```xml
  <dependency>
      <groupId>cn.hutool</groupId>
      <artifactId>hutool-http</artifactId>
      <version>5.8.22</version>
  </dependency>
  ```

- httpclient

  ```xml
  <dependency>
  	<groupId>org.apache.httpcomponents</groupId>
    	<artifactId>httpclient</artifactId>
    	<version>4.5.14</version>
  </dependency>
  ```

- okhttp

  ```xml
  <dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.10.0</version>
  </dependency>
  ```

### 调用api

#### 普通方式

```java
// 创建授权request
AuthRequest authRequest=new AuthGiteeRequest(AuthConfig.builder()
        .clientId("clientId")
        .clientSecret("clientSecret")
        .redirectUri("redirectUri")
        .build());
// 生成授权页面
        authRequest.authorize("state");
// 授权登录后会返回code（auth_code（仅限支付宝））、state，1.8.0版本后，可以用AuthCallback类作为回调接口的参数
// 注：JustAuth默认保存state的时效为3分钟，3分钟内未使用则会自动清除过期的state
        authRequest.login(callback);
```

#### Builder 方式一

静态配置 `AuthConfig`

```java
AuthRequest authRequest=AuthRequestBuilder.builder()
        .source("github")
        .authConfig(AuthConfig.builder()
        .clientId("clientId")
        .clientSecret("clientSecret")
        .redirectUri("redirectUri")
        .build())
        .build();
// 生成授权页面
        authRequest.authorize("state");
// 授权登录后会返回code（auth_code（仅限支付宝））、state，1.8.0版本后，可以用AuthCallback类作为回调接口的参数
// 注：JustAuth默认保存state的时效为3分钟，3分钟内未使用则会自动清除过期的state
        authRequest.login(callback);
```

#### Builder 方式二

动态获取并配置 `AuthConfig`

```java
AuthRequest authRequest=AuthRequestBuilder.builder()
        .source("gitee")
        .authConfig((source)->{
        // 通过 source 动态获取 AuthConfig
        // 此处可以灵活的从 sql 中取配置也可以从配置文件中取配置
        return AuthConfig.builder()
        .clientId("clientId")
        .clientSecret("clientSecret")
        .redirectUri("redirectUri")
        .build();
        })
        .build();
        Assert.assertTrue(authRequest instanceof AuthGiteeRequest);
        System.out.println(authRequest.authorize(AuthStateUtils.createState()));
```

#### Builder 方式支持自定义的平台

```java
AuthRequest authRequest=AuthRequestBuilder.builder()
        // 关键点：将自定义实现的 AuthSource 配置上
        .extendSource(AuthExtendSource.values())
        // source 对应 AuthExtendSource 中的枚举 name
        .source("other")
        // ... 其他内容不变，参考上面的示例
        .build();
```
