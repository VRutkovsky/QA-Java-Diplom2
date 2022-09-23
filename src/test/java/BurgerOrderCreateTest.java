import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BurgerOrderCreateTest {
    private BurgerUser burgerUser;
    private BurgerOrder burgerOrder;
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

    @Test
    @DisplayName("Order Create. Happy path")
    @Description("Order created with status 'Done', response code 200, Success field is true")
    public void orderCreatePositiveTest(){
        int numIngredients = 3;

        // Test data initialization
        burgerUser = new BurgerUser();
        burgerUser.apiUserCreate();
        burgerOrder = new BurgerOrder(burgerUser.getAccessToken());

        // Check that burger ingredients are available
        BurgerOrder.Ingredient[] ingredientMenu = burgerOrder.getIngredientsMenu();
        Assert.assertTrue("Test failed. Ingredients are not available for the order", ingredientMenu.length > 0);

        if(numIngredients > ingredientMenu.length ){
            numIngredients = ingredientMenu.length;
        }

        //Create order
        ValidatableResponse response = burgerOrder.burgerOrderCreate(numIngredients);

        Assert.assertEquals("Order creation failed. Invalid status code.", SC_OK, response.extract().statusCode());

        Assert.assertTrue("Order creation failed. Invalis response success field value.", response.extract().path("success"));

        Assert.assertEquals("", "done", response.extract().path("order.status"));

        Assert.assertTrue("Order creation failed. Order number is not returned", burgerOrder.getBurgerOrderNumber() > 0);
    }

    @Test
    @DisplayName("Order Create. No Authorization")
    @Description("API Document do not describe the case in terms of system response. Assume that response Status code == 200 and 'success' == true but status is missed is correct.")
    public void orderCreateNoAuthTest(){
        int numIngredients = 3;

        // Test data initialization
        burgerUser = new BurgerUser();
        burgerUser.apiUserCreate();
        burgerOrder = new BurgerOrder(burgerUser.getAccessToken());

        // Check that burger ingredients are available
        BurgerOrder.Ingredient[] ingredientMenu = burgerOrder.getIngredientsMenu();
        Assert.assertTrue("Test failed. Ingredients are not available for the order", ingredientMenu.length > 0);

        if(numIngredients > ingredientMenu.length ){
            numIngredients = ingredientMenu.length;
        }

        //Create order
        ValidatableResponse response = burgerOrder.burgerOrderCreateNoAuth(numIngredients);

        Assert.assertEquals("Order creation failed. Invalid status code.", SC_OK, response.extract().statusCode());

        Assert.assertTrue("Order creation failed. Invalid response success field value.", response.extract().path("success"));

        Assert.assertTrue("Expected no order status returned. But order status exists.", Objects.isNull(response.extract().path("order.status")));
    }

    @Test
    @DisplayName("Order Create. No ingredients")
    @Description("Order is not created, response code 400, Success field is false")
    public void orderCreateNoIngredientsTest(){
        int numIngredients = 0;

        // Test data initialization
        burgerUser = new BurgerUser();
        burgerUser.apiUserCreate();
        burgerOrder = new BurgerOrder(burgerUser.getAccessToken());

        //Create order
        ValidatableResponse response = burgerOrder.burgerOrderCreate(numIngredients);

        Assert.assertEquals("Order creation expected to be failed. Invalid status code.", SC_BAD_REQUEST, response.extract().statusCode());

        Assert.assertFalse("Order creation expected to be failed. Invalid response success field value.", response.extract().path("success"));
    }

    @Test
    @DisplayName("Order Create. Invalid hash")
    @Description("Order is not created, response code 500")
    public void orderCreateInvalidHashTest(){

        // Test data initialization
        burgerUser = new BurgerUser();
        burgerUser.apiUserCreate();
        burgerOrder = new BurgerOrder(burgerUser.getAccessToken());

        //Create order
        ValidatableResponse response = burgerOrder.burgerOrderCreateInvalidHash("zzzzzzzzzz");

        Assert.assertEquals("Order creation expected to be failed. Invalid status code.", SC_INTERNAL_SERVER_ERROR, response.extract().statusCode());
    }

}
