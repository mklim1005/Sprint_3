import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestDeleteCourier {
    public String login = RandomStringUtils.randomAlphabetic(10);
    public String password = RandomStringUtils.randomAlphabetic(10);
    public String firstName = RandomStringUtils.randomAlphabetic(10);
    Courier courier;
    int id;

    @Before
    public void setUp() {
        courier = new Courier(login, password, firstName);
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        id = responseLogin.body().jsonPath().getInt("id");
    }

    @Test
    public void successfulRequestReturnsTrue(){
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
    public void requestWithoutIdReturnError(){
        Response responseDelete = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .delete("/api/v1/courier/");
        responseDelete.then().assertThat().statusCode(404)
                .and()
                .assertThat().body("code",equalTo(404))
                .and()
                .assertThat().body("message",equalTo("Not Found."));
    }

    @Test
    public void requestWithNotExistingId() {
       int  randomId = 99999;
        Response responseDelete = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .delete("/api/v1/courier/" + randomId);
        responseDelete.then().assertThat().statusCode(404)
                .and()
                .assertThat().body("code", equalTo(404))
                .and()
                .assertThat().body("message", equalTo("Курьера с таким id нет."));
    }
}
