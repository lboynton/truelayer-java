package truelayer.java.payments.entities.paymentdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Value;

@Getter
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationFlow {

    Configuration configuration;
}