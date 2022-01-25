package truelayer.java.payments.entities;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(Include.NON_NULL)
public class StartAuthorizationFlowRequest {

    private ProviderSelection providerSelection;

    private Redirect redirect;

    @Builder
    @Getter
    @ToString
    @EqualsAndHashCode
    @JsonInclude(Include.NON_NULL)
    public static class ProviderSelection {}

    @Builder
    @Getter
    @ToString
    @EqualsAndHashCode
    @JsonInclude(Include.NON_NULL)
    public static class Redirect {
        String returnUri;
    }
}
