package me.manzhos.config;

import me.manzhos.heplers.BasketClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected BasketClient baskets;
    protected String basketId;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        TestConfig.init(); // sets RestAssured.baseURI from BASE_URL env
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        baskets = new BasketClient();
        basketId = baskets.createBasket();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (baskets != null && basketId != null && !basketId.isBlank()) {
            baskets.deleteSafe(basketId);
        }
    }
}
