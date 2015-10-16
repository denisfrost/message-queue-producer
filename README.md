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

* a `com.sun.jersey.api.client.Client`
* to set up a configuration in `QueueProxyConfiguration`
* a list of `com.ft.messaging.standards.message.v1.Message`s to send

example:

```java
Client client = null;
QueueProxyConfiguration config = new QueueProxyConfiguration("test", "http://localhost:8080", Collections.emptyMap());
QueueProxyProducer producer = QueueProxyProducer.builder()
        .withJerseyClient(client)
        .withQueueProxyConfiguration(config)
        .build();
producer.send(messages);
```

or you could use with any HTTP client with which you implement the `HttpClient` interface.

## Build

```
mvn clean install
```
