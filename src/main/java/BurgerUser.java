import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

public class BurgerUser {
    private String login;
    private String password;
    private String firstName;
    private String accessToken;
    private String refreshToken;

    private boolean isLoginSuccess = false;

    public BurgerUser(){
        int i = (int) (Math.random() * 9999);
        login = "Test_User_" + i + "@yandex.ru";
        password = "Password" + i;
        firstName = "Ivan" + i;
    }
    public BurgerUser(String login, String password, String firstName){
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAccessToken(){
        return this.accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean isLoginSuccess(){
        return this.isLoginSuccess;
    }

    @Step("User Create")
    public ValidatableResponse apiUserCreate(boolean dropField) {
        String strLogin;
        String strPassword;
        String strFirstName;

        if(login == null) {strLogin = "";} else strLogin = login;
        if(password == null) {strPassword = "";} else strPassword = password;
        if(firstName == null) {strFirstName = "";} else strFirstName = firstName;

        String json = "{";
        String separator = "";

        if(dropField) {
            if(!strLogin.isBlank() && !strLogin.isEmpty()) {
                json = json + "\"email\": \"" + strLogin + "\"";
                separator = ", ";
            }
            if (!strPassword.isBlank() && !strPassword.isEmpty()){
                json = json + separator + "\"password\": \"" + strPassword + "\"";
                separator = ", ";
            }
            if(!strFirstName.isBlank() && !strFirstName.isEmpty()){
                json = json + separator + "\"name\": \"" + strFirstName + "\"";
            }
            json = json + "}";
        } else {
            json = "{\"email\": \"" + strLogin + "\", \"password\": \"" + strPassword + "\", \"name\": \"" + strFirstName + "\"}";
        }
        ValidatableResponse response = given().header(BurgerEndpoints.apiPostHeaderType, BurgerEndpoints.apiPostHeaderValue).and().body(json).when().post(BurgerEndpoints.apiUserCreate).then();

        if(response.extract().statusCode() == SC_OK) {
            setAccessToken(response);
            setRefreshToken(response);
        }

        return response;
    }

    public ValidatableResponse apiUserCreate() {
        return apiUserCreate(false);
    }

    public void setAccessToken(ValidatableResponse response){
        this.accessToken = response.extract().path("accessToken");
    }
    public void setRefreshToken(ValidatableResponse response){
        this.refreshToken = response.extract().path("refreshToken");
    }

    @Step("User Login")
    public ValidatableResponse apiUserLogin(String strLogin, String strPassword){
        if(strLogin == null) {strLogin = "";}
        if(strPassword == null) {strPassword = "";}

        String json = "{\"email\": \"" + strLogin + "\", \"password\": \"" + strPassword + "\"}";

        ValidatableResponse response = given().header(BurgerEndpoints.apiPostHeaderType, BurgerEndpoints.apiPostHeaderValue).and().body(json).when().post(BurgerEndpoints.apiUserLogin).then();

        if(response.extract().statusCode() == SC_OK) {
            setAccessToken(response);
            setRefreshToken(response);
        }

        return response;
    }
    public ValidatableResponse apiUserLogin(){
        return apiUserLogin(login, password);
    }

    @Step("User Delete")
    public ValidatableResponse apiUserDelete(){
        ValidatableResponse response = null;
        if(isLoginSuccess){
            response = given().auth().oauth2(accessToken).and().when().delete(BurgerEndpoints.apiUserDelete).then();
        }
        return response;
    }

    @Step("User Update")
    public ValidatableResponse apiUserUpdate(String strEmail, String strName) {

        String json = "{\n    \"email\": \"" + strEmail + "\",\n    \"name:\": \"" + strName + "\"\n}";

        ValidatableResponse response = given()
                .auth().oauth2(accessToken.substring(7))
                .header(BurgerEndpoints.apiPostHeaderType, BurgerEndpoints.apiPostHeaderValue)
                .and().body(json)
                .when().patch(BurgerEndpoints.apiUserUpdate)
                .then();

        return response;
    }
    @Step("User Update No Authorization")
    public ValidatableResponse apiUserUpdateNoAuth(String strEmail, String strName) {

        String json = "{\n    \"email\": \"" + strEmail + "\",\n    \"name:\": \"" + strName + "\"\n}";

        ValidatableResponse response = given()
                .header(BurgerEndpoints.apiPostHeaderType, BurgerEndpoints.apiPostHeaderValue)
                .and().body(json)
                .when().patch(BurgerEndpoints.apiUserUpdate)
                .then();

        return response;
    }
}

