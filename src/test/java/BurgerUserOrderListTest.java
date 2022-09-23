import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class BurgerUserOrderListTest {
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
    @DisplayName("Get User Order List. Happy path")
    @Description("Order List is returned, number of orders > 0, response code 200, Success field is true")
    public void userOrderListPositiveTest() {
        int numIngredients = 3;
        ValidatableResponse response;

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

        //Create 2 orders
        response = burgerOrder.burgerOrderCreate(numIngredients);
        Assert.assertTrue("Order is not created. Test can`t be completed.", response.extract().path("success"));
        response = burgerOrder.burgerOrderCreate(numIngredients);
        Assert.assertTrue("Order is not created. Test can`t be completed.", response.extract().path("success"));

        response = burgerOrder.burgerUserOrderList(burgerUser.getAccessToken());

        Assert.assertEquals("Order list is not returned. Invalid status code.", SC_OK, response.extract().statusCode());

        Assert.assertTrue("Order list is not returned. Success field value should be true.", response.extract().path("success"));

        Assert.assertNotNull("Order list is not returned. 'orders' node is not provided.", response.extract().path("orders"));
    }
    @Test
    @DisplayName("Get User Order List. No Auth")
    @Description("Order List is not returned. Response code 401, Success field is false")
    public void userOrderListNoAuthTest() {
        int numIngredients = 3;
        ValidatableResponse response;

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

        //Create 2 orders
        response = burgerOrder.burgerOrderCreate(numIngredients);
        Assert.assertTrue("Order is not created. Test can`t be completed.", response.extract().path("success"));
        response = burgerOrder.burgerOrderCreate(numIngredients);
        Assert.assertTrue("Order is not created. Test can`t be completed.", response.extract().path("success"));

        response = burgerOrder.burgerUserOrderList();

        Assert.assertEquals("Order list is not returned. Invalid status code.", SC_UNAUTHORIZED, response.extract().statusCode());

        Assert.assertFalse("Order list should not be returned. Success field value should be false.", response.extract().path("success"));
    }

}
