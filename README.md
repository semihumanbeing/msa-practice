# msa-practice
Spring Cloud로 개발하는 마이크로서비스 애플리케이션(MSA) 을 공부했다.

### 구현 정보
Java 11, Spring  2.4.2, Spring cloud 2020.0.0 <br>
Spring Cloud 를 사용해 클라우드 기반의 아키텍쳐로 쇼핑몰을 구현했다. <br>
구현된 모듈은 프론트는 없고 서버와 backend api로 이루어져있다.<br>
테스트는 Postman을 사용했다.

### 구현내용
1. Netflix의 Eureka 서버 모듈<br>
2. API gateway<br>
ㄴ Load Balancer를 사용하여 각각의 microservice uri에 따라 Routing 설정<br>
ㄴ AuthorizationHeaderFilter를 적용해 권한이 없는 사용자는 403 에러 리턴<br>
3. user 서비스 <br>
ㄴ H2, JPA 기반 JWT 토큰과 Spring security 사용<br>
ㄴ 비밀번호는 서버에 encrypt 되어 저장됨<br>
ㄴ Entity를 리턴할 때 ModelMapper 라이브러리를 사용하여 DTO로 변환하여 반환한다.<br>
ㄴ post /user, get /users, get /users/{userId}<br>
4. order 서비스<br>
ㄴ post /{userId}/orders, get /{userId}/orders <br>
5. catalogs 서비스<br>
ㄴ get /catalog<br>
6. config 서비스<br>
ㄴ Yaml파일에 환경변수를 따로 설정하여 로그인 시 키의 정보나 profile별 다른 설정 정보를 사용할 수 있음<br>
ㄴ Github에 설정 정보를 업로드하여 사용할 수 있음<br>
ㄴ 이 때 DB의 키와 암호와 같은 중요 정보를 암호화하여 올려두면 안전하다.<br>
7. 대칭키와 비대칭키를 사용하여 문서 암호화하기<br>
ㄴ 대칭키
- config 서비스에 bootstrap 디펜던시를 추가하여 key 정보를 입력한다.<br>
- config 서비스의 포트에서 post /encrypt 로 암호화된 키를 발급하고 사용할 수 있다.<br>
ㄴ 비대칭 키
- 새로운 키 만들기<br>
keytool -genkeypair -alias apiEncryptionKey -keyalg RSA -dname "CN=semi, OU=API Development, L=Seoul, C=KR" -keypass "test1234" -keystore apiEncryptionKey.jks -storepass "test1234" <br>
- 만든 키의 정보 보기<br>
keytool -list -keystore .\apiEncryptionKey.jks -v <br>
- jks 키를 퍼블릭 키 cer파일로 내보내기<br>
keytool -export -alias apiEncryptionKey -keystore apiEncryptionKey.jks -rfc -file trustServer.cer<br>
- 퍼블릭 키를 jks파일로 가져오기<br>
keytool -import -alias trustServer -file trustServer.cer -keystore publicKey.jks<br>
8. AMQP(Advanced Message Queuing Protocol) 프로토콜을 사용하여 마이크로서비스를 동기화하기<br>
ㄴ AMQP를 구현한 브로커로는 RabbitMQ를 사용했다. (도커를 사용)<br>
```
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 --restart=unless-stopped rabbitmq:management
```
ㄴ @SpringBootApplication 어노테이션에 @AutoConfigure 덕분에 디펜던시 설정만 하면 자동으로 RabbitMQ 서버에 등록된다 <br>

9. Microservice간 통신을 하기 위해 RestTemplate와 FeignClient를 사용<br>
ㄴ RestTemplate는 직관적으로 확인가능하고 FeignClient는 좀더 가독성 좋은 코드를 작성할 수 있었다.<br>
ㄴ User 서비스에서 유저를 조회했을 때 Order 서비스의 api를 요청하고 반환받을 수 있다!<br>
ㄴ ErrorDecoder를 사용해 만약 order서비스에서 에러가 났을 때 user서비스는 반환 하고 order서비스만 나오지 않도록 예외처리 가능<br>
ㄴ 여러개의 Order 서비스가 있을 때 이를 동기화 하기 위해서는 Kafka를 사용하여 DB를 동기화한다.<br>
