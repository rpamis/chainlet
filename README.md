## Rpamis-pattern

🌱Rpamis-pattern project is a collection of design pattern frameworks that bring design patterns out of the box to developers

<p align="center">
  <a href="https://central.sonatype.com/artifact/com.rpamis/rpamis-pattern-chain/1.0.0">
    <img alt="maven" src="https://img.shields.io/maven-central/v/com.rpamis/rpamis-pattern-chain?style=flat-square">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="license" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>

  <a href="https://codecov.io/gh/benym/rpamis-pattern" > 
    <img alt="codecov" src="https://img.shields.io/codecov/c/gh/benym/rpamis-pattern?color=%23&style=flat-square"/> 
  </a>
</p>

-------------------------------------------------------------------------------
[**中文**](README-CN.md)|[**English**](README.md)
-------------------------------------------------------------------------------
### Rpamis-pattern-chain

Provides a framework for the Chain of Responsibility pattern with zero dependencies

### Usage

- Import dependence

```xml
<dependency>
    <groupId>com.rpamis</groupId>
    <artifactId>rpamis-pattern-chain</artifactId>
    <version>1.0.4</version>
</dependency>
```

- Creates a ChainPipeline by inheriting `AbstractChainPipeline`

```java
// Process List<User> type data
public class UserChainPipline extends AbstractChainPipeline<List<User>> {
    // Inject some necessary information, or other action
}
```

- Creates ChainHandler by inheriting `AbstractChainHandler`

The following uses creating three processing classes as an example

```java
public class ValidateHandler extends AbstractChainHandler<List<User>> {

    @Override
    protected boolean process(List<User> handlerData) {
        return handlerData.stream().anyMatch(user -> {
            if (user.getName() == null || user.getPwd() == null) {
                System.out.println("The user name or password is empty");
                return false;
            }
            System.out.println("The user name or password is verified successfully");
            return true;
        });
    }
}
```

```java
public class LoginHandler extends AbstractChainHandler<List<User>> {

    @Override
    protected boolean process(List<User> handlerData) {
        return handlerData.stream().anyMatch(user -> {
            if ("test".equals(user.getName()) && "123".equals(user.getPwd())) {
                user.setRole("admin");
            } else {
                System.out.println("User verification failure");
                return false;
            }
            System.out.println("User verification pass");
            return true;
        });
    }
}
```

```java
public class AuthHandler extends AbstractChainHandler<List<User>> {

    @Override
    protected boolean process(List<User> handlerData) {
        return handlerData.stream().anyMatch(user -> {
            if (!"admin".equals(user.getRole())) {
                System.out.println("The supertube check failed");
                throw new RuntimeException("failure");
            }
            System.out.println("The supertube check is successful");
            return true;
        });
    }
}
```

- Build chain and start it to get the processing results

```java
List<User> list = new ArrayList<>();
User user = new User("test", "1232321");
list.add(user);
ChainPipeline<List<User>> chain = new UserChainPipline()
    .addHandler(new ValidateHandler())
    .addHandler(new LoginHandler())
    .addHandler(new AuthHandler())
    .strategy(new FullExecutionStrategy<>());
try {
    CompleteChainResult result = chain.start(list);
    // Determines the execution result of the entire chain. If there is one failure by default, it is judged as a failure
    if (result.isAllow()) {
        System.out.println("The final execution result of the responsibility chain is" + result.isAllow());
    }
    // Gets a single processing class execution result based on the processing class
    boolean validateHandlerResult = result.get(ValidateHandler.class);
} catch (ChainException e) {
    e.printStackTrace();
}
```

`CompleteChainResult`

- `isAllow`：Gets the execution result of the entire chain. If there is a failure, it is judged as a failure and returns a `boolean`
- `get(Class cls)`：Gets the result of some `Handler` processing according to the `Handler` class and returns a `boolean`

#### Inner Strategy

- `FullExecutionStrategy`： The full execution strategy, the chain of responsibility default policy, will execute all processing on the chain regardless of whether a `Handler` succeeds or not
- `FastReturnStrategy`：The fast return strategy, when a `Handler` has a success returns the result immediately, the subsequent `Handler` does not execute
- `FastFailedStrategy`：Fast failure strategy, when a `Handler` failure is returned immediately, the subsequent `Handler` does not execute

#### Strategy Extension

You can expand your chain of responsibility handling strategy by following two steps

- Implement the `ChainStrategy` interface

```java
public class CustomStrategy<T> implements ChainStrategy<T>{

    @Override
    public ChainResult init(Class<?> handlerClass, boolean processResult) {
        return ChainStrategy.super.init(handlerClass, processResult);
    }

    @Override
    public void doStrategy(T handlerData, ChainPipeline<T> chain, ChainResult chainResult) throws IOException, ChainException {
        // do your custom chainstrategy
    }
}
```

`init` method: Provides a default wrapper for processing results

`doStrategy` method: Used to write your own chain processing strategy, `handlerData` is the data processed by the responsibility chain, `chain` is the responsibility chain context, `chainResult` is the result of a single `Handler` processing

- Use this implementation when building chains

```java
ChainPipeline<List<User>> chain = new UserChainPipline()
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .addHandler(new AuthHandler())
                .strategy(new CustomStrategy<>());
```

### Reference

- `AOP`:`org.springframework.aop.framework.ReflectiveMethodInvocation` 
- `Tomcat`: `org.apache.catalina.core.ApplicationFilterChain`
- `SpringMVC Interceptor`: `org.springframework.web.servlet.HandlerExecutionChain`,`org.springframework.web.servlet.HandlerInterceptor`
- `Servlet Filter`:`javax.servlet.FilterChain`,`javax.servlet.Filter`
