import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class TestLogInCourier {

    public String login = RandomStringUtils.randomAlphabetic(10);
    public String password = RandomStringUtils.randomAlphabetic(10);
    public String firstName = RandomStringUtils.randomAlphabetic(10);
    Courier courier;

    @Before
    public void setUp() {
        courier = new Courier(login, password, firstName);
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        //create courier
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @After
    public void deleteCourier(){
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");

        int id;
        try {
            id = responseLogin.body().jsonPath().getInt("id");
        } catch (Exception e) {
            // skip
            System.out.println("skip courier deletion");
            return;
        }

        Response responseDelete = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .delete("/api/v1/courier/" + id);
        responseDelete.then().assertThat().statusCode(200)
                .and()
                .assertThat().body("ok",equalTo(true));
    }


    @Test
    public void testLogInCourierCanLogIn(){
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogin.then().assertThat().statusCode(200);
    }

    @Test
    public void testLogInCantLogInWithoutLogin(){
        courier.setLogin("");
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogin.then().assertThat().statusCode(400)
                .and()
                .assertThat().body("message",equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void testLogInCantLogInWithoutPassword(){
        courier.setPassword("");
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogin.then().assertThat().statusCode(400)
                .and()
                .assertThat().body("message",equalTo("Недостаточно данных для входа"));
    }
    @Test
    public void testLogInWrongPassword(){
        courier.setPassword("HelloVinniPuh");
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogin.then().assertThat().statusCode(404)
                .and()
                .assertThat().body("message",equalTo("Учетная запись не найдена"));
    }

    @Test
    public void testLogInWrongLogIn(){
        courier.setLogin("HelloVinniPuh");
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogin.then().assertThat().statusCode(404)
                .and()
                .assertThat().body("message",equalTo("Учетная запись не найдена"));
    }
    @Test
    public void testLogInCourierNotExist(){
        courier.setLogin(RandomStringUtils.randomAlphabetic(10));
        courier.setPassword(RandomStringUtils.randomAlphabetic(10));
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogin.then().assertThat().statusCode(404)
                .and()
                .assertThat().body("message",equalTo("Учетная запись не найдена"));
    }

    @Test
    public void testLogInReturnID(){
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogin.then().assertThat().statusCode(200);
        responseLogin.then().assertThat().body("id", notNullValue());
    }

}
