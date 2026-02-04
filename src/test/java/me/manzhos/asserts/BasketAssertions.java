package me.manzhos.asserts;

import io.restassured.path.json.JsonPath;
import org.testng.asserts.SoftAssert;

public class BasketAssertions {

    public static void assertBasketTotals(JsonPath body,
                                          double subtotal,
                                          double discount,
                                          double delivery,
                                          double total) {

        SoftAssert soft = new SoftAssert();

        soft.assertEquals(body.getDouble("data.subtotal"), subtotal, "subtotal");
        soft.assertEquals(body.getDouble("data.totalDiscount"), discount, "totalDiscount");
        soft.assertEquals(body.getDouble("data.deliveryFee"), delivery, "deliveryFee");
        soft.assertEquals(body.getDouble("data.total"), total, "total");

        soft.assertAll();
    }
}
