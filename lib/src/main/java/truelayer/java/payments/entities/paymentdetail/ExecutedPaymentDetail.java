package truelayer.java.payments.entities.paymentdetail;

import static truelayer.java.payments.entities.paymentdetail.Status.EXECUTED;

import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ExecutedPaymentDetail extends PaymentDetail {
    private final Status status = EXECUTED;

    private SourceOfFunds sourceOfFunds;

    private Date executedAt;

    private Optional<AuthorizationFlow> authorizationFlow;
}
