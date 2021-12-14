# QueryDSL 소개
- 실무에서는 조건에 따라서 실행되는 쿼리가 달라지는 동적 쿼리를 많이 사용한다.
- 주문 내역 검색으로 돌아가서 이 예제를 QueryDSL로 바꿔본다.

## QueryDSL로 처리
```java
public List<Order> findAll(OrderSearch orderSearch) {
    QOrder order = QOrder.order;
    QMember member = QMember.member;
    return query
            .select(order)
            .from(order)
            .join(order.member, member)
            .where(statusEq(orderSearch.getOrderStatus()),
                    nameLike(orderSearch.getMemberName()))
            .limit(1000)
            .fetch();
}
private BooleanExpression statusEq(OrderStatus statusCond) {
    if (statusCond == null) {
        return null;
    }
    return order.status.eq(statusCond);
}
private BooleanExpression nameLike(String nameCond) {
    if (!StringUtils.hasText(nameCond)) {
        return null;
    }
    return member.name.like(nameCond);
}
```
## build.gradle에 querydsl 추가
```
//querydsl 추가
buildscript {
	dependencies {
		classpath("gradle.plugin.com.ewerk.gradle.plugins:querydsl-plugin:1.0.10")
	}
}

plugins {
	id 'org.springframework.boot' version '2.4.12'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}

apply plugin: 'io.spring.dependency-management'
apply plugin: "com.ewerk.gradle.plugins.querydsl"

group = 'jpabook'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

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
	implementation 'org.springframework.boot:spring-boot-devtools'
	implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.7.1")
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'com.fasterxml.jackson.datatype:jackson-datatype-hibernate5'

	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	//JUnit4 추가
	testImplementation("org.junit.vintage:junit-vintage-engine") {
		exclude group: "org.hamcrest", module: "hamcrest-core"
	}

	//querydsl 추가
	implementation 'com.querydsl:querydsl-jpa'
	//querydsl 추가
	implementation 'com.querydsl:querydsl-apt'
}

//querydsl 추가
//def querydslDir = 'src/main/generated'
def querydslDir = "$buildDir/generated/querydsl"

querydsl {
	library = "com.querydsl:querydsl-apt"
	jpa = true
	querydslSourcesDir = querydslDir
}

sourceSets {
	main {
		java {
			srcDirs = ['src/main/java', querydslDir]
		}
	}
}

compileQuerydsl{
	options.annotationProcessorPath = configurations.querydsl
}

configurations {
	querydsl.extendsFrom compileClasspath
}

test {
	useJUnitPlatform()
}

```
- QueryDSL은 SQL(JPQL)과 모양이 유사하면서 자바 코드로 동적 쿼리를 편리하게 생성할 수 있다.
- 실무에서는 복잡한 동적 쿼리를 많이 사용하게 되는데, 이 때 Querydsl을 사용하면 높은 개발 생산성을 얻으면서 동시에
쿼리 오류를 컴파일 시점에 빠르게 잡을 수 있다.
- 꼭 동적 쿼리가 아니라 정적 쿼리인 경우에도 다음과 같은 이유로 Querydsl을 사용하는 것이 좋다.
    - 직관적인 문법
    - 컴파일 시점에 빠른 문법 오류 발견
    - 코드 자동완성
    - 코드 재사용 (자바)
    - JPQL new 명령어와는 비교가 안될 정도로 깔끔한 DTO 조회를 지원한다.
