import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class TestOrderCreation {
    Order order;
    int trackOrder;

    @Before
    public void setUp() {
        order = new Order();
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @After
    public void cancelOrder(){
         String body = "{\"track\":\"" + trackOrder + "\"}";
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .put("/api/v1/orders/cancel");
    }

    @Test
    public void testCreateOrderWithoutColor(){
        Response responseOrder =   given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
        trackOrder = responseOrder.body().jsonPath().getInt("track");
        responseOrder.then().assertThat().statusCode(201);
        responseOrder.then().assertThat().body("track", notNullValue());
    }

    @Test
    public void testCreateOrderWithBlackColor(){
        order.setColor(List.of("BLACK"));
        Response responseOrder =   given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
        trackOrder = responseOrder.body().jsonPath().getInt("track");
        responseOrder.then().assertThat().statusCode(201);
        responseOrder.then().assertThat().body("track", notNullValue());
    }

    @Test
    public void testCreateOrderWithGreyColor(){
        order.setColor(List.of("GREY"));
        Response responseOrder =   given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
        trackOrder = responseOrder.body().jsonPath().getInt("track");
        responseOrder.then().assertThat().statusCode(201);
        responseOrder.then().assertThat().body("track", notNullValue());
    }

    @Test
    public void testCreateOrderWithBothColor(){
        order.setColor(List.of("BLACK","GREY"));
        Response responseOrder =   given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
        trackOrder = responseOrder.body().jsonPath().getInt("track");
        responseOrder.then().assertThat().statusCode(201);
        responseOrder.then().assertThat().body("track", notNullValue());
    }
}
