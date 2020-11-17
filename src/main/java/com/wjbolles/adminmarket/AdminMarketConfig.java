package com.wjbolles.adminmarket;

public interface AdminMarketConfig {
    String getTreasuryAccount();

    double getSalesTax();

    double getMaxPercentBasePrice();

    boolean getUseFloatingPrices();

    String getStorage();

    void setTreasuryAccount(String treasuryAccount);

    void setSalesTax(double salesTax);

    void setMaxPercentBasePrice(double maxPercentBasePrice);

    void setUseFloatingPrices(boolean useFloatingPrices);

    void setStorage(String storage);
}
