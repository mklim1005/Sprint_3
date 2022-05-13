import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestGetOrderByTrack {
    Order order;
    int trackOrder;

    @Before
    public void setUp() {
        order = new Order();
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        //create order and get track
        Response responseOrder =   given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
        trackOrder = responseOrder.body().jsonPath().getInt("track");
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
    public void testGetOrderIdByTrackReturnsObject(){
        OrderAsModelResponse order =   given()
                .header("Content-type", "application/json")
                .queryParam("t",trackOrder)
                .get("/api/v1/orders/track")
                .body().as(OrderAsResponse.class).getOrderAsModelResponse();
        assertTrue(order.getId() > 0);
        assertEquals(order.getFirstName(),"Vinni");
        assertEquals(order.getLastName(),"Puh");
        assertEquals(order.getAddress(),"Moscow");
        assertEquals(order.getMetroStation(),5);
        assertEquals(order.getPhone(),"+79032349473");
        assertEquals(order.getRentTime(),5);
        assertEquals(order.getComment(),"haha");
    }
    @Test
    public void testGetOrderIdByTrackWithoutTrackNumberReturnsError(){
        Response getOrderByTrackResponse =   given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders/track");
        getOrderByTrackResponse.then().assertThat().statusCode(400)
                .and()
                .assertThat().body("code",equalTo(400))
                .and()
                .assertThat().body("message",equalTo("Недостаточно данных для поиска"));
    }

    @Test
    public void testGetOrderIdByTrackWithWrongTrackReturnsError(){
        int randomId = 99999;
        Response getOrderByTrackResponse =   given()
                .header("Content-type", "application/json")
                .queryParam("t",randomId)
                .get("/api/v1/orders/track");
        getOrderByTrackResponse.then().assertThat().statusCode(404)
                .and()
                .assertThat().body("code",equalTo(404))
                .and()
                .assertThat().body("message",equalTo("Заказ не найден"));
    }
}
