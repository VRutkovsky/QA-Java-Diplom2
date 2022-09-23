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

public class BurgerUserLoginTest {
    private BurgerUser burgerUser;
    private ValidatableResponse response;

    @Before
    public void setUp() {
        RestAssured.baseURI = BurgerEndpoints.apiBasicURL;
    }

    @After
    public void cleanUp() {
        //Test data clean-up
        response = burgerUser.apiUserDelete();
    }
    //Positive Test
    //User Logged in successfully. Courier ID is returned.
    @Test
    @DisplayName("User login. Happy path")
    @Description("Data provided, response code 200, Success field is true")
    public void userLoginSuccessTest() {

        // Test data initialization
        burgerUser = new BurgerUser();
        burgerUser.apiUserCreate();

        //Test Run
        response = burgerUser.apiUserLogin();

        Assert.assertEquals("User login is not successful. Status code is incorrect.", SC_OK, response.extract().statusCode());

        Assert.assertTrue("User login failed. Success code should be true", response.extract().path("success"));
    }

    //Wrong Login provided
    @Test
    @DisplayName("User login. Wrong login")
    @Description("Wrong Login provided, response code 401")
    public void userWrongLoginTest() {

        // Test data initialization
        burgerUser = new BurgerUser();
        burgerUser.apiUserCreate();

        response = burgerUser.apiUserLogin("bfgbvpoyjjnyhn", burgerUser.getPassword());

        assertEquals("User login rejection failed.", SC_UNAUTHORIZED, response.extract().statusCode());

        //Response body check
        String strExpected = "email or password are incorrect";
        String strResponse = response.extract().path("message");
        assertEquals("Courier login rejection failed. Body content is incorrect", strExpected, strResponse );
    }

    //Wrong Password provided
    @Test
    @DisplayName("User login. Wrong password")
    @Description("Wrong Login provided, response code 401")
    public void userWrongPasswordTest() {

        // Test data initialization
        burgerUser = new BurgerUser();
        burgerUser.apiUserCreate();

        response = burgerUser.apiUserLogin(burgerUser.getLogin(), "urukjkfj");

        assertEquals("User login rejection failed.", SC_UNAUTHORIZED, response.extract().statusCode());

        //Response body check
        String strExpected = "email or password are incorrect";
        String strResponse = response.extract().path("message");
        assertEquals("Courier login rejection failed. Body content is incorrect", strExpected, strResponse );
    }

}
