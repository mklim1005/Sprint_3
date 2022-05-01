import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestListOfOrders {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void testListOfOrders() {
        Response response = given()
                .header("Content-type", "application/json")
                .queryParam("page",0)
                .queryParam("limit",2)
                .get("/api/v1/orders");
        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("orders", notNullValue());

        OrderListResponse orderListResponse = response.body().as(OrderListResponse.class);
        assertEquals(orderListResponse.orders.size(),2);

        OrderAsModelResponse order = orderListResponse.orders.get(0);
        assertTrue(order.getId() > 0);
        assertEquals(order.getFirstName(),"Чарли");
        assertEquals(order.getLastName(),"Мур");
        assertEquals(order.getAddress(),"Ул Котиков, 12");
        assertEquals(order.getMetroStation(),92);
        assertEquals(order.getPhone(),"89036001020");
        assertEquals(order.getRentTime(),1);
        assertEquals(order.getComment(),"Приезжайте поскорее, уж больно нужен самокат");
    }
}
