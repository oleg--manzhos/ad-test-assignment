package me.manzhos.tests;

import io.restassured.path.json.JsonPath;
import jdk.jfr.Description;
import me.manzhos.config.BaseTest;
import org.testng.annotations.Test;

import static me.manzhos.asserts.BasketAssertions.assertBasketTotals;

public class LimitationsTests extends BaseTest {

    @Test(groups={"limitations"})
    @Description("bread: total 1 -> delivery 1")
    public void bread_total1_deliver1(){

        baskets.addItem(basketId, "1", 1);
        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 2.5, 0, 5, 7.5);
    }

    @Test(groups={"limitations"})
    @Description("bread: total 5 -> delivery 5")
    public void bread_total_max_5(){
        baskets.addItem(basketId, "1", 5);
        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 12.5, 0, 5, 17.5);
    }

    @Test(groups={"limitations"})
    @Description("bread: total 6 -> business rule violation")
    public void bread_total_6(){
        baskets.addItem(basketId, "1", 6);
        JsonPath body = baskets.calculate(basketId);
        //this is the incorrect behaviour: fee is 5 :)
        assertBasketTotals(body, 0, 0, 0, 0);
    }
}
