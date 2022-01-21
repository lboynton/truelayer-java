package truelayer.java;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static truelayer.java.TestUtils.assertNotError;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.*;
import truelayer.java.payments.entities.CountryCode;
import truelayer.java.payments.entities.CreatePaymentRequest;
import truelayer.java.payments.entities.CustomerSegment;
import truelayer.java.payments.entities.ReleaseChannel;
import truelayer.java.payments.entities.beneficiary.MerchantAccount;
import truelayer.java.payments.entities.paymentmethod.BankTransfer;
import truelayer.java.payments.entities.paymentmethod.provider.ProviderFilter;
import truelayer.java.payments.entities.paymentmethod.provider.UserSelectionProvider;

@Tag("acceptance")
public class AcceptanceTests {
    private static TrueLayerClient tlClient;

    @BeforeAll
    public static void setup() {
        tlClient = TrueLayerClient.New()
                .useSandbox()
                .clientCredentials(ClientCredentials.builder()
                        .clientId(System.getenv("TL_CLIENT_ID"))
                        .clientSecret(System.getenv("TL_CLIENT_SECRET"))
                        .build())
                .signingOptions(SigningOptions.builder()
                        .keyId(System.getenv("TL_SIGNING_KEY_ID"))
                        .privateKey(System.getenv("TL_SIGNING_PRIVATE_KEY").getBytes(StandardCharsets.UTF_8))
                        .build())
                .build();
    }

    @Test
    @DisplayName("It should create a payment")
    public void shouldCreateAPayment() {
        var paymentRequest = buildPaymentRequest();

        var createPaymentResponse = tlClient.payments().createPayment(paymentRequest);

        assertNotError(createPaymentResponse);
    }

    @Test
    @DisplayName("It should create a payment and open it in TL HPP")
    @SneakyThrows
    public void shouldCreateAPaymentAndOpenItInHPP() {
        var paymentRequest = buildPaymentRequest();
        var createPaymentResponse = tlClient.payments().createPayment(paymentRequest);
        assertNotError(createPaymentResponse);

        var hppPageRequest = HttpRequest.newBuilder(tlClient.hpp()
                        .getHostedPaymentPageLink(
                                createPaymentResponse.getData().getId(),
                                createPaymentResponse.getData().getPaymentToken(),
                                URI.create("http://localhost")))
                .GET()
                .build();
        var hppResponse = getHttpClient().send(hppPageRequest, HttpResponse.BodyHandlers.ofString());

        assertTrue(hppResponse.statusCode() >= 200 && hppResponse.statusCode() < 300);
    }

    @Test
    @DisplayName("It should create a payment and get it by id")
    @SneakyThrows
    public void shouldGetAPaymentById() {
        var paymentRequest = buildPaymentRequest();
        var createPaymentResponse = tlClient.payments().createPayment(paymentRequest);
        assertNotError(createPaymentResponse);

        var getPaymentByIdResponse =
                tlClient.payments().getPayment(createPaymentResponse.getData().getId());

        assertNotError(getPaymentByIdResponse);
    }

    private CreatePaymentRequest buildPaymentRequest() {
        return CreatePaymentRequest.builder()
                .amountInMinor(100)
                .currency("GBP")
                .paymentMethod(BankTransfer.builder()
                        .provider(UserSelectionProvider.builder()
                                .filter(ProviderFilter.builder()
                                        .countries(List.of(CountryCode.GB))
                                        .releaseChannel(ReleaseChannel.GENERAL_AVAILABILITY)
                                        .customerSegments(List.of(CustomerSegment.RETAIL))
                                        .providerIds(List.of("mock-payments-gb-redirect"))
                                        .build())
                                .build())
                        .build())
                .beneficiary(MerchantAccount.builder()
                        .id("e83c4c20-b2ad-4b73-8a32-ee855362d72a")
                        .build())
                .user(CreatePaymentRequest.User.builder()
                        .name("Andrea Di Lisio")
                        .type(CreatePaymentRequest.User.Type.NEW)
                        .email("andrea@truelayer.com")
                        .build())
                .build();
    }

    private HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }
}
