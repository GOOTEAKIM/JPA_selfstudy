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
---
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
---
## lec 3

### IntelliJ IDEA에서 MySQL 데이터베이스와 연결하기

1. Database Tool Window 열기:

- IntelliJ IDEA의 우측 하단에 있는 Database 탭을 클릭합니다. 만약 이 탭이 보이지 않는다면, View -> Tool Windows -> Database를 선택하여 열 수 있습니다.

2. 데이터베이스 드라이버 설정:

- 데이터베이스 도구 창에서 + 아이콘을 클릭하고 Data Source -> MySQL을 선택합니다.
- Download missing driver files 버튼이 나타나면 클릭하여 필요한 MySQL 드라이버를 다운로드합니다.

3. 데이터베이스 연결 설정:

- Host: localhost (또는 데이터베이스 서버의 IP 주소)
- Port: 3306 (기본 MySQL 포트)
- Database: jpabegin (사용할 데이터베이스 이름)
- User: root (MySQL 사용자 이름)
- Password: 1157139 (MySQL 사용자 비밀번호)

4. 연결 테스트:

- 모든 정보를 입력한 후 Test Connection 버튼을 클릭하여 연결이 정상적으로 설정되었는지 확인합니다.
- 연결이 성공하면 OK 버튼을 클릭하여 설정을 저장합니다.

5. 쿼리 실행:

- Ctrl + Enter (또는 Cmd + Enter on Mac)를 눌러 쿼리를 실행하고 결과를 확인할 수 있습니다.

---

### main 을 실행했을 때 한글로 보이지 않는 경우

- 인코딩 문제이다.
  
- help - Edic Custom VM Options
  - `-Dfile.encoding=UTF-8` 추가
- 인텔리제이 재부팅
---

### 정리

- EntityManager를 사용해서 엔티티 단위로 CRUD 처리
- 변경은 트랜잭션 범위 안에서 실행
  - persist()
  - 수정
  - remove()
---