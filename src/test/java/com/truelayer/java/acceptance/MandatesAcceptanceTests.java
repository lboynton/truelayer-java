package com.truelayer.java.acceptance;

import static com.truelayer.java.TestUtils.*;
import static com.truelayer.java.recurringpayments.entities.mandate.Constraints.PeriodicLimit.PeriodAlignment.CALENDAR;
import static com.truelayer.java.recurringpayments.entities.mandate.Constraints.PeriodicLimit.PeriodType.MONTH;

import com.truelayer.java.entities.CurrencyCode;
import com.truelayer.java.entities.ProviderFilter;
import com.truelayer.java.entities.User;
import com.truelayer.java.entities.accountidentifier.AccountIdentifier;
import com.truelayer.java.entities.beneficiary.Beneficiary;
import com.truelayer.java.http.entities.ApiResponse;
import com.truelayer.java.payments.entities.*;
import com.truelayer.java.recurringpayments.entities.CreateMandateRequest;
import com.truelayer.java.recurringpayments.entities.CreateMandateResponse;
import com.truelayer.java.recurringpayments.entities.mandate.Constraints;
import com.truelayer.java.recurringpayments.entities.mandate.Mandate;
import com.truelayer.java.recurringpayments.entities.mandatedetail.MandateDetail;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Disabled
@Tag("acceptance")
public class MandatesAcceptanceTests extends AcceptanceTests {

    public static final URI A_REDIRECT_URI = URI.create("https://48941d32f8bd7826841d8224f9390525.m.pipedream.net");
    public static final String PROVIDER_ID = "ob-natwest-vrp-sandbox";

    @Test
    @DisplayName("It should create a mandate")
    @SneakyThrows
    public void itShouldCreateAMandate() {
        // create mandate
        ApiResponse<CreateMandateResponse> createMandateResponse =
                tlClient.mandates().createMandate(createMandateRequest()).get();

        assertNotError(createMandateResponse);

        // start auth flow
        StartAuthorizationFlowRequest startAuthorizationFlowRequest = StartAuthorizationFlowRequest.builder()
                .withProviderSelection()
                .redirect(StartAuthorizationFlowRequest.Redirect.builder()
                        .returnUri(A_REDIRECT_URI)
                        .build())
                .build();

        ApiResponse<StartAuthorizationFlowResponse> startAuthorizationFlowResponse = tlClient.mandates()
                .startAuthorizationFlow(createMandateResponse.getData().getId(), startAuthorizationFlowRequest)
                .get();

        assertNotError(startAuthorizationFlowResponse);

        // submit provider selection
        SubmitProviderSelectionRequest submitProviderSelectionRequest =
                SubmitProviderSelectionRequest.builder().providerId(PROVIDER_ID).build();

        ApiResponse<SubmitProviderSelectionResponse> submitProviderSelectionResponse = tlClient.mandates()
                .submitProviderSelection(createMandateResponse.getData().getId(), submitProviderSelectionRequest)
                .get();

        assertNotError(submitProviderSelectionResponse);
    }

    @Test
    @DisplayName("It should create and get a mandate by id")
    @SneakyThrows
    public void itShouldCreateAndGetAMandate() {
        // create mandate
        ApiResponse<CreateMandateResponse> createMandateResponse =
                tlClient.mandates().createMandate(createMandateRequest()).get();

        assertNotError(createMandateResponse);

        // get mandate by id
        ApiResponse<MandateDetail> getMandateResponse = tlClient.mandates()
                .getMandate(createMandateResponse.getData().getId())
                .get();

        assertNotError(getMandateResponse);
    }

    private CreateMandateRequest createMandateRequest() {
        return CreateMandateRequest.builder()
                .mandate(Mandate.vrpSweepingMandate()
                        .providerFilter(ProviderFilter.builder()
                                .countries(Collections.singletonList(CountryCode.GB))
                                .releaseChannel(ReleaseChannel.PRIVATE_BETA)
                                .build())
                        .beneficiary(Beneficiary.externalAccount()
                                .accountIdentifier(AccountIdentifier.sortCodeAccountNumber()
                                        .accountNumber("10003957")
                                        .sortCode("140662")
                                        .build())
                                .accountHolderName("Andrea Java SDK")
                                .reference("a reference")
                                .build())
                        .build())
                .currency(CurrencyCode.GBP)
                .user(User.builder()
                        .id(UUID.randomUUID().toString())
                        .name("John Smith")
                        .email("john@truelayer.com")
                        .build())
                .constraints(Constraints.builder()
                        .periodicLimits(Collections.singletonList(Constraints.PeriodicLimit.builder()
                                .periodAlignment(CALENDAR)
                                .periodType(MONTH)
                                .maximumAmount(1000)
                                .build()))
                        .build())
                .build();
    }
}