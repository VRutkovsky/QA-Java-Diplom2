import io.restassured.response.Response;
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
                .header(BurgerTestAPI.apiPostHeaderType, BurgerTestAPI.apiPostHeaderValue)
                .when().get(BurgerTestAPI.apiGetIngredients).as(IngredientsList.class);

        this.ingredients = ingredientsList.data;
    }

    public Ingredient[] getIngredientsMenu(){
        return this.ingredients;
    }

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
                .header(BurgerTestAPI.apiPostHeaderType, BurgerTestAPI.apiPostHeaderValue)
                .and().body(strJson)
                .when().post(BurgerTestAPI.apiOrderCreate)
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

    public ValidatableResponse burgerOrderCreateNoAuth(int numIngredients) {
        String strJson = "{\n    \"ingredients\": [";
        String separator;

        for (int i = 0; i < numIngredients; i++) {
            if(i < numIngredients -1) {separator = ", ";} else {separator = " ";}
            strJson = strJson + "\"" + ingredients[i]._id + "\"" + separator;
        }
        strJson = strJson + "]\n}";

        ValidatableResponse response = given()
                .header(BurgerTestAPI.apiPostHeaderType, BurgerTestAPI.apiPostHeaderValue)
                .and().body(strJson)
                .when().post(BurgerTestAPI.apiOrderCreate)
                .then();

        return response;
    }

    public ValidatableResponse burgerOrderCreateInvalidHash(String strHash) {
        String strJson = "{\n    \"ingredients\": [";

        strJson = strJson + "\"" + strHash + "\" ";

        strJson = strJson + "]\n}";

        ValidatableResponse response = given()
                .auth().oauth2(accessToken.substring(7))
                .header(BurgerTestAPI.apiPostHeaderType, BurgerTestAPI.apiPostHeaderValue)
                .and().body(strJson)
                .when().post(BurgerTestAPI.apiOrderCreate)
                .then();

        this.burgerName = "";
        this.burgerOrderNumber = 0;

        return response;
    }

    public ValidatableResponse burgerUserOrderList(String accessToken) {

        ValidatableResponse response = given()
                .auth().oauth2(accessToken.substring(7))
                .header(BurgerTestAPI.apiPostHeaderType, BurgerTestAPI.apiPostHeaderValue)
                .when().get(BurgerTestAPI.apiUserOrdersList)
                .then();

        return response;
    }

    public ValidatableResponse burgerUserOrderList() {

        ValidatableResponse response = given()
                .header(BurgerTestAPI.apiPostHeaderType, BurgerTestAPI.apiPostHeaderValue)
                .when().get(BurgerTestAPI.apiUserOrdersList)
                .then();

        return response;
    }

}
