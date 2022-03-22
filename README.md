# Kotlin EventSourcing and CQRS [![CI](https://github.com/daggerok/event-sourcing-and-cqrs-k/actions/workflows/ci.yaml/badge.svg)](https://github.com/daggerok/event-sourcing-and-cqrs-k/actions/workflows/ci.yaml)

## Build

```bash
setjdk17 ; ./mvnw 
```

## Run and test

```bash
java -jar app/web-app/target/*jar

http post :8080/register-bank-account aggregateId=00000000-0000-0000-0000-000000000001 username=maksimko password=pwd
http get :8080/find-bank-account-registration-date/00000000-0000-0000-0000-000000000001
http get :8080/find-bank-account-activated-state/00000000-0000-0000-0000-000000000001

http post :8080/activate-bank-account aggregateId=00000000-0000-0000-0000-000000000001
http get :8080/find-bank-account-activated-state/00000000-0000-0000-0000-000000000001
```

## RTFM

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.3/maven-plugin/reference/html/#build-image)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.3/reference/htmlsingle/#boot-features-developing-web-applications)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
