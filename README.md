# Kotlin EventSourcing and CQRS [![CI](https://github.com/daggerok/event-sourcing-and-cqrs-k/actions/workflows/ci.yaml/badge.svg)](https://github.com/daggerok/event-sourcing-and-cqrs-k/actions/workflows/ci.yaml)

See this README on [GitHub pages](https://daggerok.github.io/event-sourcing-and-cqrs-k/)

## Build

```bash
setjdk 17 ; ./mvnw
```

## Run and test web app

```bash
java -jar app/web-app/target/*jar

http post :8080/register-bank-account aggregateId=00000000-0000-0000-0000-000000000001 username=maksimko password=pwd
http get :8080/find-bank-account-registration-date/00000000-0000-0000-0000-000000000001
http get :8080/find-bank-account-activated-state/00000000-0000-0000-0000-000000000001

http post :8080/activate-bank-account aggregateId=00000000-0000-0000-0000-000000000001
http get :8080/find-bank-account-registration-date/00000000-0000-0000-0000-000000000001
http get :8080/find-bank-account-activated-state/00000000-0000-0000-0000-000000000001
```

## TODO

* merge `eventstore/in-memory-event-store` with `domain/bank-account-domain` and update `autoconfigure/bank-account-domain-in-memory-support`
* implement `domain/bank-account-domain-mapdb` and merge it with `eventstore/mapdb-event-store` and update `autoconfigure/bank-account-domain-mapdb-support`
* implement `web/web-mapdb-app`
* add application exception in `api` module and replace all RuntimeExceptions with custom application exceptions

## RTFM

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.3/maven-plugin/reference/html/#build-image)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#boot-features-developing-web-applications)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
