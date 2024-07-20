# 7/20

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

## lec 4

### 엔티티 매핑

- 기본 애노테이션

    - @Entity : 엔티티 클래스에 설정, 필수
    - @Table : 매핑할 테이블 지정
    - @Id : 식별자 속성에 설정, 필수
    - @Column : 매핑할 칼럼명 지정
      - 지정하지 않으면 필드면/프로퍼티명 사용
    - @Enumerated : enum 타입 매핑할 때 설정

- @Table
  - 애노테이션을 생략하면 클래스 이름과 동일한 이름에 매핑
  - 속성
    - name : 테이블 이름 (생략하면 클래스 이름과 동일한 이름)
    - catalog : 카탈로그 이름 (ex. MySQL DB 이름)
    - schema : 스키마 이름 (예, 오라클 스키마 이름)

  - 예
    - @Table
    - @Table(name="hotel_info")
    - @Table(catalog="point", name = "point_history")
    - @Table(schema="crm", name = "cust_stat")

- @Enumeratd
  - 열거 타입을 매핑할 때 사용
  - 설정 값
    - EnumType.STRING : enum 타입 값 이름을 저장
      - 문자열 타입 칼럼에 매핑
    - EnumType.ORDINAL(기본값) : enum 타입의 값의 순서를 저장
      - 숫자 타입 칼럼에 매핑
---
### 매핑 예시

```java
@Entity // 매핑 대상 엔티티
@Table(name = "hotel_info") // hotel_info 테이블에 매핑
public class Hotel {

    @Id // 식별자로 매핑
    @Column(name = "hotel_id") // hotel_id 칼럼에 매핑
    private String id;

    @Column(name = "nm") // nm 칼럼에 매핑
    private String name;
    
    private int year; // year 칼럼에 매핑
    
    @Enumerated(EnumType.STRING) //열거타입 이름을 값으로 저장
    private Grade grade; // grade 칼럼에 매핑
     
    private LocalDateTime created; // created 칼럼에 매핑
    
    @Column(name = "modified") // modified 칼럼에 매핑
    private LocalDateTime lastModified;

    // ... 생성자, getter
}
```
---
### 엔티티 클래스 제약 조건 (스펙 기준)

- @Entity 사용
- @Id 사용
- 인자 없는 기본 생성자 필요
- 기본 생성자는 public, protected
- 최상위 클래스여야 함
- final 이면 안됨

    ```java
    @Entity 
    @Table(name = "hotel_info") 
    public class Hotel {

        @Id // 식별자로 매핑
        @Column(name = "hotel_id") 
        private String id;

        protected Hotel() {
            // 인자 없는 기본 생성자
        }
        // ...
        // ...
    }
    ```
---
### 접근 타입

- 두 개 접근 타입
  - 필드 접근 : 필드 값을 사용해서 매핑
  - 프로퍼티 접근 : getter/setter 메서드를 사용해서 매핑

- 설정 방법
  - @Id를 필드에 붙이면 필드 접근
  - @Id를 getter 메서드에 붙이면 프로퍼티 접근
  - @Access를 사용해서 명시적으로 지정
    - 클래스/개별 필드에 적용 가능
    - @Access(AccessType.PROPERTY) / @Access(AccessType.FIELD)

- 필드 접근 선호
  - 불필요한 setter 메서드를 만들 필요 없음
---
### 정리

- 주요 매핑 애노테이션
  - @Entity, @Table, @Id, @Column, @Enumerated
- 엔티티 클래스 제약
  - 기본 생성자 필요 등 몇 가지 제약 있음
- 접근 타입
  - 필드 접근(*), 프로퍼티 접근
---

## lec 5
### 식별자 생성 방식

- 직접 할당
  - @Id 설정 대상에 직접 값 설정
  - 사용자가 이력한 값, 규칙에 따라 생성한 값 등
  - 예) 이메일, 주문 번호

  - 저장하기 전에 생성자 할당, 보통 생성 시점에 전달

- 식별 칼럼 방식
  - DB의 식별 칼럼에 매핑
    - DB가 식별자를 생성하므로 객체 생성시에 식별값을 설정하지 않음
  - 설정 방식
    - @GeneratedValue(strategy=GenerationType.IDENTITY) 설정
  - INSERT 쿼리를 실행해야 식별자를 알 수 있음
    - EntityManager#persist() 호출 시점에 INSERT 쿼리 실행
    - persist() 실행할 때 객체에 식별자 값 할당됨

- 시퀀스 사용 방식

- 테이블 사용 방식(많이 안사용함)
  - 테이블을 시퀀스처럼 사용
    - 테이블에 엔티티를 위한 키 보관
    - 해당 테이블을 이용해서 다음 식별자 생성
  - 설정 방식
    - @TableGenerator로 테이블 생성기 설정
    - @GeneratedValue의 generator로 테이블 생성기 지정
  - EntityManager#persist() 호출 시점에 테이블 사용
    - persist() 할 때 테이블을 이용해서 식별자 구하고 이를 엔티티에 할당
    - INSERT 쿼리는 실행하지 않음
---


