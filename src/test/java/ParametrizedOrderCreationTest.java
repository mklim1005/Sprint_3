import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;


@RunWith(Parameterized.class)
public class ParametrizedOrderCreationTest {
    private final Order order;

    public ParametrizedOrderCreationTest(Order order) {
        this.order = order;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Parameterized.Parameters
    public static Object[][] getData() {

        Order orderWithoutColor = new Order();
        Order orderWithBlackColor = new Order();
        orderWithBlackColor.setColor(List.of("BLACK"));
        Order orderWithGreyColor = new Order();
        orderWithGreyColor.setColor(List.of("GREY"));
        Order orderWithBothColors = new Order();
        orderWithBothColors.setColor(List.of("GREY","BLACK"));

        return new Object[][] {
                { orderWithoutColor },
                { orderWithBlackColor },
                { orderWithGreyColor },
                { orderWithBothColors },
        };
    }
    @Test
    public void shouldCreateOrder() {
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
