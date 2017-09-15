package com.wjbolles;

public interface AdminMarketConfig {
    String getTreasuryAccount();

    double getSalesTax();

    double getMaxPercentBasePrice();

    boolean getUseFloatingPrices();

    void setTreasuryAccount(String treasuryAccount);

    void setSalesTax(double salesTax);

    void setMaxPercentBasePrice(double maxPercentBasePrice);

    void setUseFloatingPrices(boolean useFloatingPrices);
}
