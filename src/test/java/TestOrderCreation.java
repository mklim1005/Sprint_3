import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestOrderCreation {
    Order order;

    @Before
    public void setUp() {
        order = new Order("Mrr","Puh","Moscow",4,"+7839276",5,"2022-04-30","Haha");
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }


    @Test
    public void testCreateOrderWithoutColor(){
        Response responseOrder =   given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
        responseOrder.then().assertThat().statusCode(201);
        System.out.println(responseOrder.body().jsonPath().getInt("track"));
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
        responseOrder.then().assertThat().statusCode(201);
        responseOrder.then().assertThat().body("track", notNullValue());
    }
}
