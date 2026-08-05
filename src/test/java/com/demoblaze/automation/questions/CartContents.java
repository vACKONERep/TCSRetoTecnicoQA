package com.demoblaze.automation.questions;

import com.demoblaze.automation.ui.CartPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Custom Question: returns the product names currently displayed in the cart.
 * Encapsulates how cart content is read so assertions stay declarative.
 */
public class CartContents implements Question<List<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartContents.class);

    public static Question<List<String>> displayed() {
        return new CartContents();
    }

    /**
     * Soft-friendly presence check for a single product title in the cart table.
     */
    public static Question<Boolean> includes(String productName) {
        return actor -> {
            actor.attemptsTo(
                    WaitUntil.the(CartPage.CART_PRODUCT_BY_NAME.of(productName), isVisible())
                            .forNoMoreThan(10).seconds()
            );
            boolean present = CartPage.CART_PRODUCT_BY_NAME.of(productName)
                    .resolveFor(actor)
                    .isVisible();
            LOGGER.info("Cart contains '{}'? {}", productName, present);
            return present;
        };
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        actor.attemptsTo(
                WaitUntil.the(CartPage.CART_PRODUCT_ROWS, isVisible())
                        .forNoMoreThan(10).seconds()
        );

        // Cart row structure: image | title | price | Delete
        List<String> titles = CartPage.CART_PRODUCT_ROWS.resolveAllFor(actor).stream()
                .map(row -> row.findElements(By.tagName("td")))
                .filter(cells -> cells.size() >= 2)
                .map(cells -> cells.get(1).getText().trim())
                .filter(title -> !title.isEmpty())
                .collect(Collectors.toList());

        LOGGER.info("Products found in cart: {}", titles);
        return titles;
    }
}
