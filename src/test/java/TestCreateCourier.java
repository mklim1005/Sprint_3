import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.http.HttpRequest;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestCreateCourier {
    public String login = "ttttt";
    public String password = "12345";
    public String firstName = "VinniPuh";
    Courier courier = new Courier(login, password, firstName);


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

   // @Test
   // public void testCreateCourierCanBeCreated1(){
        //ScooterRegisterCourier courier = new ScooterRegisterCourier();
       // ArrayList<String> newCourier = courier.registerNewCourierAndReturnLoginPassword();
       // for (int i = 0; i < newCourier.size(); i++) {
       //     System.out.println(newCourier.get(i));
       // }
   // }

    @Test
    public void testCreateCourierCanBeCreated(){
    ;
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



    @After
    public void deleteCourier(){
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        responseLogin.then().assertThat().statusCode(200);
         String id = responseLogin.body().asString().substring(6,11);
        System.out.println(id);

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

}
