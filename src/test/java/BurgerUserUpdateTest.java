import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BurgerUserUpdateTest {
    private BurgerUser burgerUser;
    private ValidatableResponse response;

    @Before
    public void setUp() {
        RestAssured.baseURI = BurgerTestAPI.apiBasicURL;
    }

    @After
    public void cleanUp() {
        //Test data clean-up
        response = burgerUser.apiUserDelete();
    }

    @Test
    @DisplayName("User Update. Happy path")
    @Description("Token and Data provided, response code 200, Success field is true")
    public void userUpdateSuccessTest() {
        // Test data initialization
        burgerUser = new BurgerUser();
        burgerUser.apiUserCreate();

        //Test Run
        response = burgerUser.apiUserUpdate(burgerUser.getLogin(), "TestNameUpdate");

        Assert.assertEquals("User update is not successful. Status code is incorrect.", SC_OK, response.extract().statusCode());

        Assert.assertTrue("User update failed. Success code should be true", response.extract().path("success"));

    }
    @Test
    @DisplayName("User Update. No authorization")
    @Description("Token not provided, response code 401, Success field is false")
    public void userUpdateNoAuthTest() {
        // Test data initialization
        burgerUser = new BurgerUser();
        burgerUser.apiUserCreate();

        //Test Run
        response = burgerUser.apiUserUpdateNoAuth(burgerUser.getLogin(), "TestNameUpdate");

        Assert.assertEquals("User update expected to be failed. Status code is incorrect.", SC_UNAUTHORIZED, response.extract().statusCode());

        Assert.assertFalse("User update expected to be failed. Success code should be false", response.extract().path("success"));

        String strExpected = "You should be authorised";
        Assert.assertEquals("User update expected to be failed. Response message is incorrect.", strExpected, response.extract().path("message"));
    }
}
