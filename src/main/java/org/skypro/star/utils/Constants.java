package org.skypro.star.utils;
/**
 * Константы типов продуктов и транзакций, используемые в правилах рекомендаций
 * и запросах динамических правил.
 */
public final class Constants {
    public static final String PRODUCT_TYPE_DEBIT = "DEBIT";
    public static final String PRODUCT_TYPE_SAVING = "SAVING";
    public static final String PRODUCT_TYPE_INVEST = "INVEST";
    public static final String PRODUCT_TYPE_CREDIT = "CREDIT";

    public static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    public static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";

    private Constants() {}
}
