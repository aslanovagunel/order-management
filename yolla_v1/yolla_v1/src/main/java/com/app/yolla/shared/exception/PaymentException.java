package com.app.yolla.shared.exception;

/**
 * Ödəniş Exception Sinfi
 * <p>
 * Ödəniş əlaqəli xətalar üçün xüsusi exception
 */
class PaymentException extends RuntimeException {

    private String paymentMethod;
    private String transactionId;

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, String paymentMethod, String transactionId) {
        super(message);
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }
}