<p align="center">
  <img
    src="img/logo.png"
    alt="Logo"
    width="200"
  />
</p>

<h3 align="center">Lightweight, Highly Scalable Responsibility Chain Pattern Framework</h3>

<p align="center">
  <a href="README-CN.md">中文</a>
  &nbsp;|&nbsp;
  <a href="README.md">English</a>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/com.rpamis/chainlet/1.0.0">
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

🌱The Chainlet project is a lightweight, highly scalable responsibility chain pattern framework with built-in various responsibility chain forms and execution strategies

## 🚀Quick Start

- Import dependency

```xml
<dependency>
    <groupId>com.rpamis</groupId>
    <artifactId>chainlet</artifactId>
    <version>1.0.1</version>
</dependency>
```

- Implement `ChainHandler` to create Handler processing

The following takes creating 2 handler classes as an example

```java
public class ValidateHandler implements ChainHandler<List<User>> {

    @Override
    public boolean process(List<User> handlerData) {
        return handlerData.stream().anyMatch(user -> {
            if (user.getName() == null || user.getPwd() == null) {
                System.out.println("Username or password is empty");
                return false;
            }
            System.out.println("Username or password validation passed");
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
                System.out.println("Super admin validation failed");
                throw new RuntimeException("Failure");
            }
            System.out.println("Super admin validation succeeded");
            return true;
        });
    }
}
```

- Build the responsibility chain, start it, and get the processing results

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

## 🎯Core Features

### ⭐Multiple Types of Responsibility Chains

#### Serial

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .chain()
        .addHandler(new ValidateHandler())
        .build();
```

#### Parallel

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .parallelChain()
        .addHandler(new AuthHandler())
        .build();
```

### ❄️Multiple Execution Strategies

#### Built-in Strategies

- `FullExecutionStrategy`： Full execution strategy, the default strategy of the responsibility chain. Regardless of whether a `Handler` succeeds or fails, all handlers on the chain will be executed.
- `FastReturnStrategy`： Fast return strategy. When there is a success in the `Handler`, the result is returned immediately and subsequent `Handler`s will not be executed.
- `FastFailedStrategy`： Fast failure strategy. When there is a failure in the `Handler`, the result is returned immediately and subsequent `Handler`s will not be executed.

Strategy keys, `com.rpamis.chainlet.core.strategy.Strategy`

Declare when building the chain

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .parallelChain()
        .addHandler(new AuthHandler())
        .strategy(Strategy.FULL)
        .build();
```

### 📍Fallback Methods

#### Interface Fallback

- `LocalChainFallBack`： Provides interface implementation for local fallback methods, suitable for individual Handler fallback processing

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

- `GlobalChainFallBack`： Provides interface implementation for global fallback methods, suitable for the entire responsibility chain fallback processing

```java
public class DemoChainGlobalFallBack implements GlobalChainFallBack<DemoUser> {

    @Override
    public void fallBack(GlobalFallBackContext<DemoUser> fallBackContext) {
        Boolean exceptionOccurred = fallBackContext.getExceptionOccurred();
        System.out.println("global fall back success");
    }
}
```

Bind to the responsibility chain

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .chain()
        .addHandler(new AuthHandler())
        .globalFallback(new DemoChainGlobalFallBack())
        .strategy(Strategy.FULL)
        .build();
```

#### Annotation Fallback

- `@Fallback`： Marks local fallback methods
  - `fallbackMethod`： Specifies the fallback method
  - `fallbackClass`： Specifies the fallback Class
  - `enable`： Whether to enable fallback

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

### ⛓️Method Chain

`chainlet` uses the `fluent interface` pattern to build the responsibility chain template. The API can only be declared along a fixed path, preventing the responsibility chain from being built before adding `handler`s.

The declaration path is

`createChain->chain/parallelChain->addHandler->strategy/globalFallback->build`

### ❤️Core Entities

**Execution Result `CompleteChainResult`**

- `isAllow`： Get the execution result of the entire chain. If any handler fails, it is considered a failure, returning `boolean`
- `get(Class cls)`： Get the processing result of a specific `Handler` according to the `Handler` class, returning `boolean`
- `getFinalResult()`： Get the final output processedData entity of the responsibility chain, based on the last processing result
- `verifyIfFail(Class<T> handlerClass)`： Get the processing result according to handlerClass. If the result is not null and processing failed, return true
- `verifyIfSuccess(Class<T> handlerClass)`： Get the processing result according to handlerClass. If the result is not null and processing succeeded, return true
- `verifyAndThrow(Class<T> exceptionClass, Class<?> handlerClass)`： Verify the responsibility chain result, throw an exception if processing is unsuccessful
- `verifyAllAndThrow(Class<T> exceptionClass)`： Verify all responsibility chain results, throw an exception if processing is unsuccessful

**`Handler` Context `ChainHandlerContext`**

- `handlerData`： Data processed by the responsibility chain
- `processedData`： Mutable data of the responsibility chain, data returned after processing
- `extendData`： Extended data
- `localMessage`： Messages that need to be returned by the current Handler, suitable for multiple judgments within one handler, returning different messages to avoid class inflation due to simple handler logic

**Local Fallback Context `LocalFallBackContext`**

- `handlerData`： Data processed by the responsibility chain
- `exceptionOccurred`： Whether an exception occurred in the responsibility chain, such as manually throwing an exception in the `Handler`

**Global Fallback Context `GlobalFallBackContext`**

- `completeChainResult`： Final result entity of the responsibility chain

**Execution Strategy Context `ChainStrategyContext`**

- `handlerData`： Data processed by the responsibility chain
- `chain`： Responsibility chain
- `chainResult`： Execution result of a single responsibility chain Handler
- `checkResults`： List storing responsibility chain results

### ✨Advanced Features

#### 🔥Custom Strategy

Implement the `ChainStrategy` interface

```java
public class CustomStrategy<T> implements ChainStrategy<T>{

    @Override
    public void doStrategy(ChainStrategyContext<T> chainStrategyContext) {
        // do your custom chainstrategy
    }
}
```

#### 🔌Strategy SPI Extension

The `ChainStrategy` interface is marked as `@RpamisSpi` extension. The three built-in strategies are all SPI implementations. You can use the built-in strategies for `setter` injection in custom extensions in Spring/Java environments, such as:

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

When using your custom strategy, you need to create a new file `com.rpamis.chainlet.core.definition.ChainStrategy` in the `resource/META-INFO/rpamis` directory

and write your corresponding strategy key (name) and value (fully qualified name) inside, such as:

```java
customStrategy=com.xxx.CustomStrategy
```

Implement the `com.rpamis.chainlet.core.strategy.StrategyKey` interface to define your custom strategy name, where the code needs to equal the key above so that the extension class can be found when building the responsibility chain, such as:

```java
public enum CustomStrategy implements StrategyKey {
    /**
     * Custom mode
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

Use custom strategy when building the chain

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .chain()
        .addHandler(new AuthHandler())
        .strategy(CustomStrategy.Custom)
        .build();
```

### 🧪Compile-time Generation (Experimental) - Extend More Types of Responsibility Chains

Import dependency

```yaml
<dependency>
    <groupId>com.rpamis</groupId>
    <artifactId>chainlet-processor</artifactId>
    <version>1.0.0</version>
</dependency>
```

`chainlet` provides a series of compile-time code generation annotations for users to independently extend more types of responsibility chains, generating code templates that conform to `fluent interface`

For users who need to extend types, mainly focus on the `@ChainBuilder` and `@ChainBuilderService` annotations

For example, defining a new asynchronous responsibility chain `Builder`, passing `ExecutorService`

```java
@ChainBuilder
public interface AsyncChainPipelineBuilder<T> extends ChainPipelineBuilder<T>{

    AsyncChainPipelineBuilder<T> async(ExecutorService executorService);
}
```

Implement the interface

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

Execute `mvn clean compile` to compile the code, which can generate the internally needed code and expose new responsibility chain methods to `ChainPipelineFactory`. After compilation, it produces:

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

Afterwards, users can support their own responsibility chain types during chain definition

```java
ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
        .asyncChain()
        .addHandler(new AuthHandler())
        .strategy(CustomStrategy.Custom)
        .build();
```

### 🐕‍🦺Companion Idea Plugin chainlet-idea-plugin

Supports dynamic compilation API prompts in IDEA. After adding the plugin, methods can be identified without manual compilation, similar to `lombok`
