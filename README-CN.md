<p align="center">
  <img
    src="img/logo.png"
    alt="Logo"
    width="200"
  />
</p>

<h3 align="center">轻量、高扩展性的责任链模式框架</h3>

<p align="center">
  <a href="README-CN.md">中文</a>
  &nbsp;|&nbsp;
  <a href="README.md">English</a>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/com.rpamis/chainlet/1.0.2">
    <img alt="maven" src="https://img.shields.io/maven-central/v/com.rpamis/chainlet?style=flat-square">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="license" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>

  <a href="">
    <img alt="code style" src="https://img.shields.io/badge/JDK-8%2B-orange.svg?style=flat-square">
  </a>

  <a href="https://codecov.io/gh/rpamis/chainlet" > 
    <img alt="codecov" src="https://img.shields.io/codecov/c/gh/rpamis/chainlet?color=%23&style=flat-square"/> 
  </a>
</p>

---

🌱Chainlet项目是一款轻量、高扩展性的责任链模式框架，内置多种责任链形态及执行策略

## 🚀快速开始

- 引入依赖

```xml
<dependency>
    <groupId>com.rpamis</groupId>
    <artifactId>chainlet</artifactId>
    <version>1.0.2</version>
</dependency>
```

- 实现`ChainHandler`创建Handler处理

以下以创建2个处理类为例

```java
public class ValidateHandler implements ChainHandler<List<User>> {

    @Override
    public boolean process(List<User> handlerData) {
        return handlerData.stream().anyMatch(user -> {
            if (user.getName() == null || user.getPwd() == null) {
                System.out.println("用户名或密码为空");
                return false;
            }
            System.out.println("用户名或密码校验通过");
            return true;
        });
    }
}
```

```java
public class AuthHandler implements ChainHandler<List<User>> {

    @Override
    public boolean process(List<User> handlerData) {
        return handlerData.stream().anyMatch(user -> {
            if (!"admin".equals(user.getRole())) {
                System.out.println("超管校验失败");
                throw new RuntimeException("失败");
            }
            System.out.println("超管校验成功");
            return true;
        });
    }
}
```

- 构建责任链，并启动，获取处理结果

```java
DemoUser demoUser = new DemoUser();
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .chain()
        .addHandler(new ValidateHandler())
        .addHandler(new AuthHandler())
        .strategy(Strategy.FAST_FAILED)
        .build();
CompleteChainResult chainResult = demoChain.apply(demoUser);
boolean allow = chainResult.isAllow();
```

## 🎯核心特性

### ⭐多种类型责任链

#### 串行

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .chain()
        .addHandler(new ValidateHandler())
        .build();
```

#### 并行

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .parallelChain()
        .addHandler(new AuthHandler())
        .build();
```

### ❄️多种执行策略

#### 内置策略

- `FullExecutionStrategy`： 全执行策略，责任链默认策略，无论某个`Handler`是否成功，都会执行完所有链上的处理
- `FastReturnStrategy`：快速返回策略，当`Handler`中有一个成功就立即返回结果，后续`Handler`不再执行
- `FastFailedStrategy`：快速失败策略，当`Handler`中有一个失败就立即返回结果，后续`Handler`不再执行

策略key，`com.rpamis.chainlet.core.strategy.Strategy`

构建链时声明

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .parallelChain()
        .addHandler(new AuthHandler())
        .strategy(Strategy.FULL)
        .build();
```

### 📍降级方法

#### 接口降级

- `LocalChainFallBack`：提供接口实现局部降级方法，适用于单个Handler降级处理

```java
public class TestLocalFallBackHandler implements ChainHandler<DemoUser>, LocalChainFallBack<DemoUser> {

    @Override
    public boolean process(DemoUser handlerData, ChainHandlerContext<DemoUser> context) {
        return false;
    }

    @Override
    public void fallBack(LocalFallBackContext<DemoUser> fallBackContext) {
        System.out.println("local fall back success");
    }
}
```

- `GlobalChainFallBack`：提供接口实现全局降级方法，适用于整个责任链降级处理

```java
public class DemoChainGlobalFallBack implements GlobalChainFallBack<DemoUser> {

    @Override
    public void fallBack(GlobalFallBackContext<DemoUser> fallBackContext) {
        Boolean exceptionOccurred = fallBackContext.getExceptionOccurred();
        System.out.println("global fall back success");
    }
}
```

绑定责任链

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .chain()
        .addHandler(new AuthHandler())
        .globalFallback(new DemoChainGlobalFallBack())
        .strategy(Strategy.FULL)
        .build();
```

#### 注解降级

- `@Fallback`：标记局部降级方法
  - `fallbackMethod`：指定降级方法
  - `fallbackClass`：指定降级Class
  - `enable`：是否开起降级

```java
public class TestFallBackHandlerFour implements ChainHandler<DemoUser> {
    
    @Override
    @Fallback(fallbackMethod = "test")
    public boolean process(DemoUser handlerData, ChainHandlerContext<DemoUser> context) {
        return false;
    }

    public void test(LocalFallBackContext<DemoUser> localFallBackContext) {
        System.out.print("success");
    }
}
```

### ⛓️方法链

`chainlet`采用了`fluent interface`模式构建责任链模版，api只能够按照固定路径申明，避免未加入`handler`之前就进行了责任链构建

申明路径为

`createChain -> chain/parallelChain -> addHandler -> strategy/globalFallback -> build`

### ❤️核心实体

**执行结果`CompleteChainResult`**

- `isAllow`：获取整个链的执行结果，有一个失败则判断为失败，返回`boolean`
- `get(Class cls)`：根据`Handler`类`class`获取某个`Handler`处理结果，返回`boolean`
- `getFinalResult()`：获取责任链最终输出processedData实体，以最后一个处理结果为准
- `verifyIfFail(Class<T> handlerClass)`：根据handlerClass类获取处理结果，如果结果非空且处理失败，返回true
- `verifyIfSuccess(Class<T> handlerClass)`：根据handlerClass类获取处理结果，如果结果非空且处理成功，返回true
- `verifyAndThrow(Class<T> exceptionClass, Class<?> handlerClass)`：校验责任链结果，如果为处理不成功则抛出异常
- `verifyAllAndThrow(Class<T> exceptionClass)`：校验全部责任链结果，如果为处理不成功则抛出异常

**`Handler`上下文`ChainHandlerContext`**

- `handlerData`：责任链处理的数据
- `processedData`：责任链可变数据，处理后返回的数据
- `extendData`：扩展数据
- `localMessage`：当前Handler处理需要返回的消息，适用于一个handler内多个判断，返回不同的消息，避免handler逻辑简单引起类膨胀

**局部降级上下文`LocalFallBackContext`**

- `handlerData`：责任链处理的数据
- `exceptionOccurred`：责任链是否发生异常，如`Handler`中手动抛出异常

**全局降级上下文`GlobalFallBackContext`**

- `completeChainResult`：责任链最终结果实体

**执行策略上下文`ChainStrategyContext`**

- `handlerData`：责任链处理的数据
- `chain`：责任链
- `chainResult`：单个责任链Handler执行结果
- `checkResults`：责任链存储结果list

### ✨高级特性

#### 🔥自定义策略

实现`ChainStrategy`接口

```java
public class CustomStrategy<T> implements ChainStrategy<T>{

    @Override
    public void doStrategy(ChainStrategyContext<T> chainStrategyContext) {
        // do your custom chainstrategy
    }
}
```

#### 🔌策略SPI扩展

`ChainStrategy`接口被标记为`@RpamisSpi`扩展，内置实现的3种策略均为SPI实现，你可以在`Spring/Java`环境下在自定义扩展中使用内置策略进行`setter`注入，如

```java
public class FastReturnStrategy<T> implements ChainStrategy<T> {

    private FullExecutionStrategy<T> fullExecutionStrategy;

    public void setFullExecutionStrategy(FullExecutionStrategy<T> fullExecutionStrategy) {
        this.fullExecutionStrategy = fullExecutionStrategy;
    }

    @Override
    public void doStrategy(ChainStrategyContext<T> chainStrategyContext) {
        ChainResult chainResult = chainStrategyContext.getChainResult();
        List<ChainResult> checkResults = chainStrategyContext.getCheckResults();
        if (chainResult.isProcessResult()) {
            checkResults.add(chainResult);
        } else {
            chainResult.setProcessResult(false);
            fullExecutionStrategy.doStrategy(chainStrategyContext);
        }
    }
}
```

当使用你的自定义策略时需要在`resource/META-INFO/rpamis`目录下新建`com.rpamis.chainlet.core.definition.ChainStrategy`文件

并在内部编写你对应的策略key(命名)和value(全限定名)，如

```java
customStrategy=com.xxx.CustomStrategy
```

实现`com.rpamis.chainlet.core.strategy.StrategyKey`接口定义自定义策略名称，其中code需要和上面的key相等，以便构建责任链时能够找到你的扩展类，如

```java
public enum CustomStrategy implements StrategyKey {
    /**
     * 自定义模式
     */
    Custom("customStrategy");

    private final String code;

    Strategy(String code) {
        this.code = code;
    }

    @Override
    public String getImplCode() {
        return code;
    }
}
```

在构建链时使用自定义策略

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .chain()
        .addHandler(new AuthHandler())
        .strategy(CustomStrategy.Custom)
        .build();
```

### 🧪编译时生成(实验性)-扩展更多种类责任链

引入依赖

```yaml
<dependency>
    <groupId>com.rpamis</groupId>
    <artifactId>chainlet-processor</artifactId>
    <version>1.0.2</version>
</dependency>
```

`chainlet`提供了一系列编译时生成代码的注解，用于用户自主扩展更多种类型的责任链，生成符合`fluent interface`的代码模板

对于需要拓展种类的用户而言，主要关心`@ChainBuilder`和`@ChainBuilderService`注解

比如新定义一个异步责任链`Builder`，传递`ExecutorService`

```java
@ChainBuilder
public interface AsyncChainPipelineBuilder<T> extends ChainPipelineBuilder<T>{

    AsyncChainPipelineBuilder<T> async(ExecutorService executorService);
}
```

实现该接口

```java
@ChainBuilderService
public class AsyncChainPipelineImpl<T> extends AbstractChainPipeline<T> implements AsyncChainPipelineBuilder<T> {

    private ExecutorService executorService = Executors.newCachedThreadPool();

    protected AsyncChainPipelineImpl(ChainTypeReference<T> chainTypeReference) {
        super(chainTypeReference);
    }

    @Override
    public AsyncChainPipelineBuilder<T> async(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }
}
```

执行`mvn clean compile`编译代码，能够生成内部需要的代码，并暴露新的责任链方法到`ChainPipelineFactory`中，编译后产生

```java
public class ChainPipelineFactory {

    public static <T> AsyncChainPipelineBuilder<T> getAsyncChain(String chainId, ChainTypeReference<T> chainTypeReference) {
        return ChainPipelineCache.getAsyncChain(chainId, chainTypeReference);
    }
}
```

```java
public class ChainPipelineDirectorImpl<T> implements ChainPipelineDirector<T> {

    public AsyncChainPipelineBuilder<T> asyncChain() {
        return new AsyncChainPipelineImpl(this.chainTypeReference);
    }

    public AsyncChainPipelineBuilder<T> asyncChain(String chainId) {
        AsyncChainPipelineImpl asyncChain = new AsyncChainPipelineImpl(this.chainTypeReference);
        ChainPipelineCache.registerAsyncChain(asyncChain, chainId);
        return asyncChain;
    }
}
```

之后便可以支持用户在链式定义时采用自己的责任链种类

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .asyncChain()
        .addHandler(new AuthHandler())
        .strategy(CustomStrategy.Custom)
        .build();
```

### 🐕‍🦺配套Idea插件chainlet-idea-plugin

支持在idea中提示动态编译的api，加入插件后无需手动编译就能够识别方法，如`lombok`

