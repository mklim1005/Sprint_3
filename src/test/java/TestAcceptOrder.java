import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class TestAcceptOrder {
    public String login = RandomStringUtils.randomAlphabetic(10);
    public String password = RandomStringUtils.randomAlphabetic(10);
    public String firstName = RandomStringUtils.randomAlphabetic(10);
    Courier courier;
    Order order;
    int courierId;
    int trackOrder;
    int orderId;

    @Before
    public void setUp() {
        courier = new Courier(login, password, firstName);
        order = new Order("Vinni","Puh","Moscow",4,"+7839276",5,"2022-04-30","Haha");
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        //create courier
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        //logIn courier and get his Id
        Response responseLogin =   given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        courierId = responseLogin.body().jsonPath().getInt("id");
        System.out.println("courierId" + courierId);
        //create order and get track
        Response responseOrder =   given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
        trackOrder = responseOrder.body().jsonPath().getInt("track");
        System.out.println("trackOrder" + trackOrder);
        //get orderId
        Response getOrderByTrackResponse =   given()
                .header("Content-type", "application/json")
                .queryParam("t",trackOrder)
                .get("/api/v1/orders/track");
        orderId = getOrderByTrackResponse.body().jsonPath().getInt("order.id");
        System.out.println("orderId" + orderId);

    }

    @Test
    public void testAcceptOrderReturnsTrue() {
        Response responseAcceptOrder = given()
                .header("Content-type", "application/json")
                .queryParam("courierId", courierId)
                .put("/api/v1/orders/accept/" + orderId);
        responseAcceptOrder.then().assertThat().statusCode(200)
                .and()
                .assertThat().body("ok", equalTo(true));
    }

    @Test
    public void testAcceptOrderWithoutCourierIdReturnsError(){
        Response responseAcceptOrder = given()
                .header("Content-type", "application/json")
                .put("/api/v1/orders/accept/" + orderId);
        responseAcceptOrder.then().assertThat().statusCode(400)
                .and()
                .assertThat().body("code",equalTo(400))
                .and()
                .assertThat().body("message",equalTo("Недостаточно данных для поиска"));
    }

    @Test
    public void testAcceptOrderWithWrongCourierIdReturnsError(){
        int randomId = 99999;
        System.out.println("randomId" + randomId);
        Response responseAcceptOrder = given()
                .header("Content-type", "application/json")
                .queryParam("courierId", randomId)
                .put("/api/v1/orders/accept/" + orderId);
        responseAcceptOrder.then().assertThat().statusCode(404)
                .and()
                .assertThat().body("code",equalTo(404))
                .and()
                .assertThat().body("message",equalTo("Курьера с таким id не существует"));
    }

    @Test
    public void testAcceptOrderWithoutOrderIdReturnsError(){
        Response responseAcceptOrder = given()
                .header("Content-type", "application/json")
                .queryParam("courierId", courierId)
                .put("/api/v1/orders/accept/");
        responseAcceptOrder.then().assertThat().statusCode(404)
                .and()
                .assertThat().body("code",equalTo(404))
                .and()
                .assertThat().body("message",equalTo("Not Found."));
    }

    @Test
    public void testAcceptOrderWithWrongOrderIdReturnsError(){
        int randomId = 99999;
        System.out.println("randomId" + randomId);
        Response responseAcceptOrder = given()
                .header("Content-type", "application/json")
                .queryParam("courierId", courierId)
                .put("/api/v1/orders/accept/" + randomId);
        responseAcceptOrder.then().assertThat().statusCode(404)
                .and()
                .assertThat().body("code",equalTo(404))
                .and()
                .assertThat().body("message",equalTo("Заказа с таким id не существует"));
    }


}
