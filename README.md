# message-queue-producer

A library that uses confluentinc's [kafka-rest](https://github.com/confluentinc/kafka-rest) to write to a message queue.

This can be called just by `send(messages)` and the HTTP calls are handled in the background.

## Use

```
<dependency>
    <groupId>com.ft</groupId>
    <artifactId>message-queue-producer</artifactId>
    <version>1.0.0.-SNAPSHOT</version>
</dependency>
```

You'll need

* an `com.ft.jerseyhttpwrapper.config.EndpointConfiguration`
* a `com.ft.jerseyhttpwrapper.ResilientClient`
* a list of `com.ft.messaging.standards.message.v1.Message`s

```java
QueueProxyProducer producer = new QueueProxyProducer(endpointConfig, resilientClient, "my-topic")
producer.send(messages)
```

## Build

`mvn clean install`
