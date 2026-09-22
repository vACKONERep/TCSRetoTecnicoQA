package com.demoblaze.automation.models;

/**
 * One purchase example: the two catalogue products plus the buyer loaded from JSON.
 * Built by {@code PurchaseCatalog} from {@code data/purchases.csv} and {@code data/buyers.json}.
 */
public final class PurchaseCase {

    private final String caseId;
    private final String description;
    private final String firstProduct;
    private final String secondProduct;
    private final PurchaseData buyer;

    public PurchaseCase(String caseId, String description, String firstProduct,
                        String secondProduct, PurchaseData buyer) {
        this.caseId = caseId;
        this.description = description;
        this.firstProduct = firstProduct;
        this.secondProduct = secondProduct;
        this.buyer = buyer;
    }

    public String getCaseId() {
        return caseId;
    }

    public String getDescription() {
        return description;
    }

    public String getFirstProduct() {
        return firstProduct;
    }

    public String getSecondProduct() {
        return secondProduct;
    }

    public PurchaseData getBuyer() {
        return buyer;
    }

    @Override
    public String toString() {
        return "PurchaseCase{caseId='" + caseId + "', description='" + description
                + "', firstProduct='" + firstProduct + "', secondProduct='" + secondProduct
                + "', buyer=" + buyer + "}";
    }
}
