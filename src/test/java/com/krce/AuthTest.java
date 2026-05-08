package com.krce;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Locale;
import java.util.Map;

public class AuthTest {
    private String name;
    private String email;
    private String password;
    private String accesstoken;

    @BeforeClass
    public void setUp(){
        RestAssured.baseURI="https://api.escuelajs.co/api/v1";
    }

    @Test(priority = 1)
    public void testRegister(){
        name="user_"+System.currentTimeMillis();
        email=name+"@gmail.com";
        password="1234567890";
        String avatar="https://picsum.photos/800";
        Map body=Map.of(
                "name",name,
                "email",email,
                "password",password,
                "avatar",avatar
        );
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", Matchers.notNullValue());
    }

    @Test(priority = 2)
    public void testLogin(){
        Map body=Map.of(
                "email",email,
                "password",password
        );
        Response response=RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/login");
        response.then()
                .log().all()
                .statusCode(201)
                .body("access_token",Matchers.notNullValue())
                .body("refresh_token",Matchers.notNullValue());
        //token value=string accesstoken
        accesstoken=response.jsonPath().getString("access_token");
    }

    @Test(priority = 3)
    public void testGetUser(){
        RestAssured.given()
                .header("Authorization","Bearer "+accesstoken)
                .when()
                .get("/auth/profile")
                .then()
                .log().all()
                .statusCode(200)
                .body("id",Matchers.notNullValue())
                .body("name",Matchers.equalTo(name));
    }
}

//click target (left side) ->surefire suite -> double click of index.html -> choose any browser
// -> times (you will see all the test cases running times