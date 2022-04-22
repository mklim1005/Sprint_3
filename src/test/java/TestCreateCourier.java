import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestCreateCourier {
    public String login = RandomStringUtils.randomAlphabetic(10);
    public String password = RandomStringUtils.randomAlphabetic(10);
    public String firstName = RandomStringUtils.randomAlphabetic(10);
    Courier courier = new Courier(login, password, firstName);


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
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
    public void testCreateCourierReturn201(){
        Response response =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201);
    }

    @Test
    public void testCantCreateTwoSimilarCouriers(){
        Response response =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201);

        Response response1 = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response1.then().assertThat().statusCode(409)
                .and()
                .assertThat().body("code",equalTo(409))
                .and()
                .assertThat().body("message",equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    public void testCreateCourierWithoutPasswordAndReturnError(){
        courier.setPassword("");
        Response response =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(400)
                .and()
                .assertThat().body("message",equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void testCreateCourierWithoutLoginAndReturnError(){
        courier.setLogin("");
        Response response =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(400)
                .and()
                .assertThat().body("message",equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void testCreateCourierReturnTrue(){
        Response response =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201)
                .and()
                .assertThat().body("ok",equalTo(true));
    }

    @Test
    public void testCreateCourierWithSameLogin(){
                    given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");

        courier.setPassword("Hello");
        courier.setFirstName("Vinni Puh");

        Response response =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(409)
                .and()
                .assertThat().body("message",equalTo("Этот логин уже используется. Попробуйте другой."));
    }
}
