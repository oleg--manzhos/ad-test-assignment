package me.manzhos.tests;

import io.qameta.allure.Description;
import io.restassured.path.json.JsonPath;
import me.manzhos.config.BaseTest;
import org.testng.annotations.Test;

import static me.manzhos.asserts.BasketAssertions.assertBasketTotals;


public class DeliveryTests extends BaseTest {

    @Test(groups={"delivery"})
    @Description("amount less than 100: subtotal 99 -> delivery cost 5")
    public void subtotal99_delivery5_total104() {
        // Water: productId "2"
        baskets.addItem(basketId, "2", 99);

        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 99, 0, 5, 104);
    }

    @Test(groups={"delivery"})
    @Description("amount equals 100: subtotal 100 -> free delivery")
    public void subtotal100_freeDelivery() {
        baskets.addItem(basketId, "2", 100);

        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 100, 0, 0, 100);
    }

    @Test(groups={"delivery"})
    @Description("amount more than 100: subtotal 101 -> free delivery")
    public void subtotal101_freeDelivery() {
        baskets.addItem(basketId, "2", 101);

        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 101, 0, 0, 101);
    }

    @Test(groups={"delivery"})
    @Description("discounts can drop order below 100 and enable delivery fee")
    public void subtotal_more100_discounts(){
        // 9 chicken: 108 before discount, discount=9 => 99 after discount
        baskets.addItem(basketId, "3", 9);
        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 108, 9, 5, 99);
    }

    @Test(groups={"delivery"})
    @Description ("multiple products")
    public void add_multiple_products(){
        baskets.addItem(basketId, "1", 2); // subtotal: 5, discount: 0, total: 5
        baskets.addItem(basketId, "2", 3); // subtotal: 3, discount: 0, total: 3
        baskets.addItem(basketId, "4", 2); // subtotal: 6, discount: 3, total: 3
        baskets.addItem(basketId, "3", 1); // subtotal: 12, discount: 0, total: 12
        //check that basket may have no more than 5 breads
        baskets.addItem(basketId, "1", 4);

        JsonPath body = baskets.calculate(basketId);

        assertBasketTotals(body, 26, 3 , 5, 28);
    }
}

