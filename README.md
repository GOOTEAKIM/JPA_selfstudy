# 7/19

## lec 1

### 기본 설정

- build gradle

    ```gradle
    plugins {
        id 'java'
        id 'org.springframework.boot' version '3.3.2'
        id 'io.spring.dependency-management' version '1.1.6'
    }

    group = 'selfstudy'
    version = '0.0.1-SNAPSHOT'

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    configurations {
        compileOnly {
            extendsFrom annotationProcessor
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
        implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
        implementation 'org.springframework.boot:spring-boot-starter-web'
        compileOnly 'org.projectlombok:lombok'
        runtimeOnly 'com.mysql:mysql-connector-j'
        annotationProcessor 'org.projectlombok:lombok'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    }

    tasks.named('test') {
        useJUnitPlatform()
    }

    ```

- application properties

    ```properties
    spring.application.name=jpa-01

    server.port=8085 // == localcost:8085

    spring.datasource.url=jdbc:mysql://localhost:3306/jpabegin // 사용할 db 주소
    spring.datasource.username=root // 사용할 db 유저 이름
    spring.datasource.password=1157139 // 유저 비번
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

    jpa.open-in-view=false
    jpa.show-sql=true
    spring.jpa.hibernate.ddl-auto=update //
    ```

- resources/META-INF/persistence.xml 생성

    ```xml
    <persistence xmlns="http://java.sun.com/xml/ns/persistence"
                version="1.0">
        <persistence-unit name="jpabegin">
            <class>jpabasic.reserve.domain.User</class>
            <properties>
                <property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/jpabegin"/>
                <property name="jakarta.persistence.jdbc.user" value="root"/>
                <property name="jakarta.persistence.jdbc.password" value="1157139"/>
                <property name="hibernate.hbm2ddl.auto" value="update"/>
                <property name="hibernate.show_sql" value="true"/>
            </properties>
        </persistence-unit>
    </persistence>
    ```
---
## lec 2

```java
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabegin");
        // 영속 단위 기준으로 초기화
        // 필요한 자원 생성

        EntityManager entityManager = emf.createEntityManager(); // EntityManager 생성
        
        EntityTransaction transaction = entityManager.getTransaction(); // EntityTransaction 구함

        //트랜잭션 시작
        try {
            transaction.begin();

            // ... entityManager로 DB 작업

            transaction.commit(); // 트랜잭션 커밋

        } catch (Exception ex) {
            transaction.rollback(); // 트랜잭션 롤백
        } finally {
            entityManager.close(); // EntityManager 닫음
        }
```

### 정리

- 기본 구조
  - EntityManagerFactory 초기화
  - DB 작업시 필요할 때마다
    - EntityManager 생성
    - EntityManager로 DB 조작
    - EntityTransaction으로 트랜잭션 관리
  - 하지만 스프링과 연동할 때는
    - 대부분 스프링이 대신 처리하므로 매핑 설정 중심으로 작업

- 영속 컨텍스트
  - 엔티티를 메모리에 보관
  - 변경을 추적해서 트랜잭션 커밋 시점에 DB에 반영
