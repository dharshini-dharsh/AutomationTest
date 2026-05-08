package com.krce;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
// for GET request no need to write body(), use query param
public class FakeAPITest {
    @BeforeClass
    public void setup(){
        RestAssured.baseURI = "https://api.escuelajs.co/api/v1";
    }

    @Test
    public void testGetProducts(){
        RestAssured.given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThan(0));
    }

    //[GET] https://api.escuelajs.co/api/v1/products/4
    @Test
    public void productId(){
        RestAssured.given()
                .when()
                .queryParam("id",4)
                .get("/products")
                .then()
                .statusCode(200);
    }

    //[GET] https://api.escuelajs.co/api/v1/products/slug/handmade-fresh-table

    @Test
    public void testFilterProductByPrice(){
        RestAssured.given()
                .queryParam("price",100)
                .when()
                .get("/products/")
                .then()
                .statusCode(200)
                .body("[0].price",Matchers.equalTo(100));
    }

    @Test
    public void testCategories(){
        RestAssured.given()
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .body("$",Matchers.instanceOf(List.class));
    }

    @Test
    public void productRange(){
        RestAssured.given()
                .queryParam("price_min",100)
                .queryParam("price_max",1000)
                .when()
                .get("/products/")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetCategoriesById(){
        RestAssured.given()
                .pathParam("id", 1)
                .when()
                .get("/categories/{id}")
                .then()
                .statusCode(200);
        //.body("id",Matchers.equalTo(1));
    }

    @Test
    public void combiningTest(){
        RestAssured.given()
                .queryParam("title","Genric")
                .queryParam("price_min",200)
                .queryParam("price_max",600)
                .queryParam("categoryId",1)
                .queryParam("limit",10)
                .queryParam("offset",10)
                .when()
                .get("/products")
                .then()
                .statusCode(200);
    }

    // for POST method use post() after when()
    @Test
    public void createCategories(){
        String categoryName = "Sharlife" + System.currentTimeMillis();
        String body = """
                {
                    "name": "%s",
                    "image": "https://placeimg.com/640/480/any"
                }
                """
                .formatted(categoryName);;
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/categories/")
                .then()
                .log().all()
                .statusCode(201)
                .body("name",Matchers.equalTo(categoryName))
                .body("image",Matchers.equalTo("https://placeimg.com/640/480/any"));
    }
}