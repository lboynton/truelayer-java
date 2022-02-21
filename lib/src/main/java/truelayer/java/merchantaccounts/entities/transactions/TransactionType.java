package truelayer.java.merchantaccounts.entities.transactions;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TransactionType {
    PAYOUT("payout"),
    PAYMENT("payment");

    @JsonValue
    private final String transactionType;
}

