package me.manzhos.heplers;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class BasketClient {

    private static final String BASE = "/api/baskets";

    public String createBasket() {
        Response res = given()
                .when().log().all()
                .post(BASE);

        res.then().statusCode(201);

        JsonPath jp = res.jsonPath();

        return jp.getString("data.id");
    }

    public Response addItem(String basketId, String productId, Object quantity) {
        Map<String, Object> payload = Map.of(
                "productId", productId,
                "quantity", quantity
        );

        return given()
                .contentType(ContentType.JSON)
                .body(payload).log().all()
                .when().post(BASE + "/{basketId}/items", basketId)
                .then().log().all()
                .extract().response();
    }

    public JsonPath calculate(String basketId) {
        Response res = given()
                .contentType(ContentType.JSON)
                .when().log().all()
                .get(BASE + "/{basketId}/calculate", basketId);

        res.statusCode();
        res.then().log().all();
        return res.jsonPath();
    }

    public void deleteSafe(String basketId) {
        try {
            Response res = given()
                    .when().log().all()
                    .contentType(ContentType.JSON)
                    .delete(BASE + "/{basketId}", basketId)
                    .then().log().all()
                    .extract().response();

            int sc = res.statusCode();
            if (!(sc == 200 || sc == 204 || sc == 404)) {
                System.out.println("deleteSafe unexpected status=" + sc);
            }
        } catch (Exception e) {
            System.out.println("deleteSafe failed: " + e.getMessage());
        }
    }
}

