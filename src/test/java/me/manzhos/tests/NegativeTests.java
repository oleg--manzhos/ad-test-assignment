package me.manzhos.tests;

import io.restassured.path.json.JsonPath;
import jdk.jfr.Description;
import me.manzhos.config.BaseTest;
import org.testng.annotations.Test;

import static me.manzhos.asserts.BasketAssertions.assertBasketTotals;

public class NegativeTests extends BaseTest {

    @Test(groups={"negative"})
    @Description("product amount: 0 products are added")
    public void product_amount_0(){
        baskets.addItem(basketId, "2", 0);
        JsonPath body = baskets.calculate(basketId);
        //this is the incorrect behaviour: fee is 5 :)
        assertBasketTotals(body, 0, 0, 0, 0);
    }

    @Test(groups={"negative"})
    @Description("product amount: non-numeric product amount is added")
    public void product_amount_char(){
        baskets.addItem(basketId, "2", 'a');
        JsonPath body = baskets.calculate(basketId);
        //this is the incorrect behaviour: subtotal is null
        assertBasketTotals(body, 0, 0, 0, 0);
    }

    @Test(groups={"negative"})
    @Description("product amount: non-integer product amount are added")
    public void product_amount_float(){
        baskets.addItem(basketId, "2", 0.5);
        JsonPath body = baskets.calculate(basketId);
        //this is the incorrect behaviour: subtotal is null
        assertBasketTotals(body, 0, 0, 0, 0);
    }
}
