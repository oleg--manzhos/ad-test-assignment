package me.manzhos.tests;

import io.qameta.allure.Description;
import io.restassured.path.json.JsonPath;
import me.manzhos.config.BaseTest;
import org.testng.annotations.Test;

import static me.manzhos.asserts.BasketAssertions.assertBasketTotals;

public class DiscountsTests extends BaseTest {

    /*
    * Please note. All the tests in this class are the subject for data provider, like
    *
    *@DataProvider(name = "discounts")
    public Object[][] discounts() {
        return new Object[][]{
        * // productId, productQuantity, subtotal, discount, deliveryFee, totalPrice
                {"4", 1, 3, 0 , 5, 8},
                {"4", 2, 6, 3, 5, 8},
                {"4", 4, 12, 6, 5, 11},
                etc...
        };
    * I intentionally skipped data provider usage to show the logic of why I've chosen these specific values.
    * */

    @Test(groups={"discounts"})
    @Description("mango B1P1: qty=1 -> pay for 1")
    public void mango_buy1_pay1(){

        baskets.addItem(basketId, "4", 1);
        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 3, 0, 5, 8);
    }

    @Test(groups={"discounts"})
    @Description("mango B2P1: qty=2 -> pay for 1")
    public void mango_buy2_pay1(){

        baskets.addItem(basketId, "4", 2);
        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 6, 3, 5, 8);
    }

    @Test(groups={"discounts"})
    @Description("mango B4P2: qty=4 -> pay for 2")
    public void mango_buy4_pay2(){

        baskets.addItem(basketId, "4", 4);
        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 12, 6, 5, 11);
    }

    @Test(groups={"discounts"})
    @Description("mango B4P2: qty=4 -> pay for 2")
    public void mango_buy100_pay50(){

        baskets.addItem(basketId, "4", 100);
        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 300, 150, 0, 150);
    }

    @Test(groups={"discounts"})
    @Description("chicken: qty=2 -> no discount yet")
    public void chicken_buy2_noDiscount(){
        baskets.addItem(basketId, "3", 2);
        JsonPath body = baskets.calculate(basketId);

        // 2 * 12 = 24, 3rd item is missing => discount 0
        // Chicken is a very buggy product. Every amount brings unexpected results
        assertBasketTotals(body, 24, 0, 5, 29);
    }

    @Test(groups={"discounts"})
    @Description("chicken: qty=3 -> 3rd is 25% off")
    public void chicken_buy3_25perc_discount() {
        baskets.addItem(basketId, "3", 3);
        JsonPath body = baskets.calculate(basketId);

        // 3*12=36, 25% of discount = 3 => subtotal(after discounts) = 33
        //this test fails, because the discount isn't applied
        assertBasketTotals(body, 36, 3, 5, 38);
    }

    @Test(groups={"discounts"})
    @Description("chicken: qty=6 -> discount applies to 3rd and 6th")
    public void chicken_buy6__discount() {
        baskets.addItem(basketId, "3", 6);
        JsonPath body = baskets.calculate(basketId);

        // 6*12=72, discount for 2 items: 2*3 = 6 => subtotal=66
        assertBasketTotals(body, 72, 6, 5, 71);
    }
}
