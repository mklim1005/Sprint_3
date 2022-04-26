import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestLogInCourier {
    ScooterRegisterCourier newCourier = new ScooterRegisterCourier();
    ArrayList<String> newCourierWithCredentials = newCourier.registerNewCourierAndReturnLoginPassword();
    Courier courier = new Courier(newCourierWithCredentials.get(0),newCourierWithCredentials.get(1));

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
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
    public void testLogInCurierNotExist(){
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
      int id =  responseLogin.body().jsonPath().get("id");
    }

}
