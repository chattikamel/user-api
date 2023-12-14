package io.mycompany.user;

import io.mycompany.user.api.User;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.testcontainers.utility.MountableFile.forClasspathResource;

@Testcontainers
@SpringBootTest(webEnvironment = DEFINED_PORT)
class UserApiApplicationTests {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withCopyFileToContainer(forClasspathResource("./data.js"), "/docker-entrypoint-initdb.d/init-script.js");



    @Test
    void when_userIsPosted_withIncompleteInformation_then_operation_should_fail() {

        with().body(User.builder().firstName("first tata").lastName("last toto").build())
                .contentType("application/json")
                .when()
                .request("POST", "/user")
                .then()
                .statusCode(BAD_REQUEST.value())
                .body(containsString("\"age\":\"Age should be positive\""))
                .body(containsString("\"email\":\"Email is mandatory\""));
    }

    @Test
    void when_postedUser_isMinor_and_dosent_live_in_france_then_unauthorized_response_should_be_done() {

        User user = User.builder()
                .firstName("first tata")
                .lastName("last tata")
                .email("tata@gmail.com")
                .age(10)
                .country("America")
                .build();

        with().body(user)
                .contentType("application/json")
                .when()
                .request("POST", "/user")
                .then()
                .statusCode(UNAUTHORIZED.value())
                .body(containsString("Only major and one that lives in france "));
    }

    @Test
    void when_userIsPosted_withCompleteInformation_then_the_user_should_be_added() {

        with().body(User.builder()
                        .firstName("first tata")
                        .lastName("last toto")
                        .age(50)
                        .email("toto.tata@gmail.com")
                        .gender("Female")
                        .build())
                .contentType("application/json")
                .when()
                .request("POST", "/user")
                .then()
                .body(containsString("User created"))
                .statusCode(201);
    }

    @Test
    void when_getUsers_isCalled_then_all_users_should_be_streamed() {
        String response = when().get("/user")
                .then()
                .statusCode(200)
                .extract()
                .asString();


        assertThat(response).contains("{\"id\":\"1\",\"firstName\":\"Luke\",\"lastName\":\"Skywalker\",\"age\":42,\"email\":\"luke.walker@gmail.com\",\"gender\":\"male\"}");
        assertThat(response).contains("{\"id\":\"2\",\"firstName\":\"Obi-Wan\",\"lastName\":\"Kenobi\",\"age\":53,\"email\":\"obi.wan@gmail.com\",\"gender\":\"male\"}");

    }

    @Test
    void when_getUser_isCalled_with_an_unknown_id_then_not_found_response_should_be_done() {
        when().get("/user/{id}", 22)
                .then()
                .statusCode(404);

    }

    @Test
    void when_getUser_isCalled_with_an_id_then_the_user_should_be_fetched() {
        when().get("/user/{id}", 1)
                .then()
                .statusCode(200)
                .body(equalTo("{\"id\":\"1\",\"firstName\":\"Luke\",\"lastName\":\"Skywalker\",\"age\":42,\"email\":\"luke.walker@gmail.com\",\"gender\":\"male\"}"));

    }
}

