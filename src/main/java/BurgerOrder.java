import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;


public class BurgerOrder {
    private String accessToken;
    private Ingredient[] ingredients;
    private int burgerOrderNumber;
    private String burgerName;

    public class Ingredient {
        String _id;
        String name;
        String type;
        int proteins;
        int fat;
        int carbohydrates;
        int calories;
        int price;
        String image;
        String image_mobile;
        String image_large;
        int __v;
    }

    private class IngredientsList{
        boolean success;
        Ingredient[] data;
    }
    public BurgerOrder(String accessToken){
        this.accessToken = accessToken;
        initIngresientsList();
    }

    private void initIngresientsList(){
         IngredientsList ingredientsList = given()
                .header(BurgerEndpoints.apiPostHeaderType, BurgerEndpoints.apiPostHeaderValue)
                .when().get(BurgerEndpoints.apiGetIngredients).as(IngredientsList.class);

        this.ingredients = ingredientsList.data;
    }

    public Ingredient[] getIngredientsMenu(){
        return this.ingredients;
    }

    @Step("Order Create")
    public ValidatableResponse burgerOrderCreate(int numIngredients) {
        String strJson = "{\n    \"ingredients\": [";
        String separator;

        for (int i = 0; i < numIngredients; i++) {
            if(i < numIngredients -1) {separator = ", ";} else {separator = " ";}
            strJson = strJson + "\"" + ingredients[i]._id + "\"" + separator;
        }
        strJson = strJson + "]\n}";

        ValidatableResponse response = given()
                .auth().oauth2(accessToken.substring(7))
                .header(BurgerEndpoints.apiPostHeaderType, BurgerEndpoints.apiPostHeaderValue)
                .and().body(strJson)
                .when().post(BurgerEndpoints.apiOrderCreate)
                .then();

        if((response.extract().statusCode() == SC_OK) && (!Objects.isNull(response.extract().path("name")))){
            this.burgerName = response.extract().path("name");
            this.burgerOrderNumber = response.extract().path("order.number");
        } else {
            this.burgerName = "";
            this.burgerOrderNumber = 0;
        }

        return response;
    }
    public int getBurgerOrderNumber(){
        return this.burgerOrderNumber;
    }

    @Step("Order Create No Auth")
    public ValidatableResponse burgerOrderCreateNoAuth(int numIngredients) {
        String strJson = "{\n    \"ingredients\": [";
        String separator;

        for (int i = 0; i < numIngredients; i++) {
            if(i < numIngredients -1) {separator = ", ";} else {separator = " ";}
            strJson = strJson + "\"" + ingredients[i]._id + "\"" + separator;
        }
        strJson = strJson + "]\n}";

        ValidatableResponse response = given()
                .header(BurgerEndpoints.apiPostHeaderType, BurgerEndpoints.apiPostHeaderValue)
                .and().body(strJson)
                .when().post(BurgerEndpoints.apiOrderCreate)
                .then();

        return response;
    }

    @Step("Order Create Invalid hash")
    public ValidatableResponse burgerOrderCreateInvalidHash(String strHash) {
        String strJson = "{\n    \"ingredients\": [";

        strJson = strJson + "\"" + strHash + "\" ";

        strJson = strJson + "]\n}";

        ValidatableResponse response = given()
                .auth().oauth2(accessToken.substring(7))
                .header(BurgerEndpoints.apiPostHeaderType, BurgerEndpoints.apiPostHeaderValue)
                .and().body(strJson)
                .when().post(BurgerEndpoints.apiOrderCreate)
                .then();

        this.burgerName = "";
        this.burgerOrderNumber = 0;

        return response;
    }

    @Step("User Order List")
    public ValidatableResponse burgerUserOrderList(String accessToken) {

        ValidatableResponse response = given()
                .auth().oauth2(accessToken.substring(7))
                .header(BurgerEndpoints.apiPostHeaderType, BurgerEndpoints.apiPostHeaderValue)
                .when().get(BurgerEndpoints.apiUserOrdersList)
                .then();

        return response;
    }

    @Step("Burger Order List no token")
    public ValidatableResponse burgerUserOrderList() {

        ValidatableResponse response = given()
                .header(BurgerEndpoints.apiPostHeaderType, BurgerEndpoints.apiPostHeaderValue)
                .when().get(BurgerEndpoints.apiUserOrdersList)
                .then();

        return response;
    }

}
