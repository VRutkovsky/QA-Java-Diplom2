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

public class BurgerUserCreateTest {
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

    // Positive Test
    // User created successfully

    @Test
    @DisplayName("Create User. Happy path")
    @Description("Data provided, response code 200, Login successful")
    public void userCreateSuccessTest() {
        // Test data initialization
        burgerUser = new BurgerUser();

        // Test run
        ValidatableResponse response = burgerUser.apiUserCreate();

        //Status Code check
        int statusCode = response.extract().statusCode();
        Assert.assertEquals("User creation failed. Response code is incorrect " ,SC_OK, statusCode);

        //Response body check
        boolean isUserCreated = response.extract().path("success");
        assertTrue("User creation failed. Body content is incorrect", isUserCreated );

        //Courier verification via login
        response = burgerUser.apiUserLogin();

        Assert.assertEquals("User creation is not successful. Login attempt is failed.", SC_OK, response.extract().statusCode());

    }

    // Negative Tests
    // User duplicate rejected
    @Test
    @DisplayName("Create User duplicate")
    @Description("Create User two times, response code 403")
    public void userCreateDuplicateTest() {
        // Test data initialization
        burgerUser = new BurgerUser();

        // Test run - create User first time
        ValidatableResponse response = burgerUser.apiUserCreate();

        //Make sure, that courier is created
        int statusCode = response.extract().statusCode();
        Assert.assertEquals("User creation failed. Response code is incorrect",SC_OK, statusCode);

        // Test run - create courier second time
        response = burgerUser.apiUserCreate();

        //Check Status Code
        statusCode = response.extract().statusCode();
        Assert.assertEquals("User duplication check failed. Response code is incorrect", SC_FORBIDDEN, statusCode);

        //Check Response body
        String strExpected = "User already exists";
        String strResponse = response.extract().path("message");
        Assert.assertEquals("User duplication check failed. Response message is different", strExpected, strResponse);
    }

    // User Data not enough: email
    @Test
    @DisplayName("Create User - no login")
    @Description("Create User without login, response code 403")
    public void userCreateNoLoginTest(){
        // Test data initialization
        burgerUser = new BurgerUser(null, "12345", "fhvbd");

        // Test run
        ValidatableResponse response = burgerUser.apiUserCreate(true);

        //Status Code check
        int statusCode = response.extract().statusCode();
        Assert.assertEquals("User rejection failed. Response code is incorrect",SC_FORBIDDEN , statusCode);

        //Response body check
        String strExpected = "Email, password and name are required fields";
        String strResponse = response.extract().path("message");
        assertEquals("User rejection failed. Body content is incorrect", strExpected, strResponse );

    }

    // User Data not enough: Password
    @Test
    @DisplayName("Create user - no password")
    @Description("Create user without password, response code 403")
    public void userCreateNoPasswordTest(){
        // Test data initialization
        burgerUser = new BurgerUser("User12123@yandex.ru", null, "fhvbd");

        // Test run
        ValidatableResponse response = burgerUser.apiUserCreate(true);

        //Status Code check
        int statusCode = response.extract().statusCode();
        Assert.assertEquals("User rejection failed. Response code is incorrect",SC_FORBIDDEN, statusCode);

        //Response body check
        String strExpected = "Email, password and name are required fields";
        String strResponse = response.extract().path("message");
        assertEquals("Courier rejection failed. Body content is incorrect", strExpected, strResponse );

    }

    // User Data not enough: Name
    @Test
    @DisplayName("Create User - no Name")
    @Description("Create User without Name should be rejected, response code 403")
    public void userCreateNoFirstNameTest(){
        // Test data initialization
        burgerUser = new BurgerUser("User12123@yandex.ru", "12345", null);

        // Test run
        ValidatableResponse response = burgerUser.apiUserCreate(true);

        //Status Code check
        int statusCode = response.extract().statusCode();
        Assert.assertEquals("Courier creation failed. Response code is incorrect", SC_FORBIDDEN, statusCode);

        //Response body check
        String strExpected = "Email, password and name are required fields";
        String strResponse = response.extract().path("message");
        Assert.assertEquals("User . Body content is incorrect", strExpected, strResponse );
    }

}
