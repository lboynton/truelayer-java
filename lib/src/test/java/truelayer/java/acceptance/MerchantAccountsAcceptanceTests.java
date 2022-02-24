package truelayer.java.acceptance;

import static truelayer.java.TestUtils.assertNotError;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import truelayer.java.entities.CurrencyCode;
import truelayer.java.http.entities.ApiResponse;
import truelayer.java.merchantaccounts.entities.*;
import truelayer.java.merchantaccounts.entities.sweeping.Frequency;
import truelayer.java.merchantaccounts.entities.sweeping.SweepingSettings;

@DisplayName("Merchant accounts acceptance tests")
public class MerchantAccountsAcceptanceTests extends AcceptanceTests {

    @SneakyThrows
    @Test
    @DisplayName("It should get the list of merchant accounts for the given client")
    public void itShouldGetTheListOfMerchantAccounts() {
        ApiResponse<ListMerchantAccountsResponse> merchantAccountsResponse =
                tlClient.merchantAccounts().listMerchantAccounts().get();

        assertNotError(merchantAccountsResponse);
    }

    @SneakyThrows
    @Test
    @DisplayName("It should get the a merchant accounts by id")
    public void itShouldGetAMerchantAccountById() {
        ApiResponse<ListMerchantAccountsResponse> merchantAccountsResponse =
                tlClient.merchantAccounts().listMerchantAccounts().get();
        String merchantAccountId =
                merchantAccountsResponse.getData().getItems().get(0).getId();
        ApiResponse<truelayer.java.merchantaccounts.entities.MerchantAccount> getMerchantAccountByIdResponse =
                tlClient.merchantAccounts()
                        .getMerchantAccountById(merchantAccountId)
                        .get();

        assertNotError(getMerchantAccountByIdResponse);
    }

    @SneakyThrows
    @Test
    @DisplayName("It should get the list of transactions for a given merchant account")
    public void itShouldGetTheListOfTransactions() {
        ApiResponse<ListMerchantAccountsResponse> merchantAccountsResponse =
                tlClient.merchantAccounts().listMerchantAccounts().get();
        String merchantAccountId = merchantAccountsResponse.getData().getItems().stream()
                .filter(m -> m.getCurrency().equals(CurrencyCode.GBP))
                .findFirst()
                .get()
                .getId();
        ApiResponse<GetTransactionsResponse> getTransactionsResponse = tlClient.merchantAccounts()
                .getTransactions(merchantAccountId, "2021-03-01T00:00:00.000Z", "2022-03-01T00:00:00.000Z", null)
                .get();

        assertNotError(getTransactionsResponse);
    }

    @SneakyThrows
    @Test
    @DisplayName("It should get the sweeping settings for the given merchant account")
    public void itShouldGetTheSweepingSettings() {
        ApiResponse<ListMerchantAccountsResponse> merchantAccountsResponse =
                tlClient.merchantAccounts().listMerchantAccounts().get();
        String merchantAccountId = merchantAccountsResponse.getData().getItems().stream()
                .filter(m -> m.getCurrency().equals(CurrencyCode.GBP))
                .findFirst()
                .get()
                .getId();

        ApiResponse<SweepingSettings> getSweepingSettingsResponse = tlClient.merchantAccounts()
                .getSweepingSettings(merchantAccountId)
                .get();

        assertNotError(getSweepingSettingsResponse);
    }

    @SneakyThrows
    @Test
    @DisplayName("It should update the sweeping settings for the given merchant account")
    public void itShouldUpdateTheSweepingSettings() {
        ApiResponse<ListMerchantAccountsResponse> merchantAccountsResponse =
                tlClient.merchantAccounts().listMerchantAccounts().get();
        String merchantAccountId = merchantAccountsResponse.getData().getItems().stream()
                .filter(m -> m.getCurrency().equals(CurrencyCode.GBP))
                .findFirst()
                .get()
                .getId();

        UpdateSweepingRequest updateSweepingRequest = UpdateSweepingRequest.builder()
                .maxAmountInMinor(100)
                .frequency(Frequency.DAILY)
                .currency(CurrencyCode.GBP)
                .build();
        ApiResponse<SweepingSettings> updateSweepingResponse = tlClient.merchantAccounts()
                .updateSweeping(merchantAccountId, updateSweepingRequest)
                .get();

        assertNotError(updateSweepingResponse);
    }

    @SneakyThrows
    @Test
    @DisplayName("It should disable sweeping for the given merchant account")
    public void itShouldDisableSweeping() {
        ApiResponse<ListMerchantAccountsResponse> merchantAccountsResponse =
                tlClient.merchantAccounts().listMerchantAccounts().get();
        String merchantAccountId = merchantAccountsResponse.getData().getItems().stream()
                .filter(m -> m.getCurrency().equals(CurrencyCode.GBP))
                .findFirst()
                .get()
                .getId();

        ApiResponse<Void> disableSweepingResponse =
                tlClient.merchantAccounts().disableSweeping(merchantAccountId).get();

        assertNotError(disableSweepingResponse);
    }

    @SneakyThrows
    @Test
    @DisplayName("It should get the payment sources for the given merchant account")
    public void itShouldGetPaymentSources() {
        ApiResponse<ListMerchantAccountsResponse> merchantAccountsResponse =
                tlClient.merchantAccounts().listMerchantAccounts().get();
        MerchantAccount merchantAccount = merchantAccountsResponse.getData().getItems().stream()
                .filter(m -> m.getCurrency().equals(CurrencyCode.GBP))
                .findFirst()
                .get();

        // todo manage this properly
        String aUserId = "4c5f09d8-fcb0-46f4-9f43-df58b158d980";

        ApiResponse<GetPaymentSourcesResponse> getPaymentSources = tlClient.merchantAccounts()
                .getPaymentSources(merchantAccount.getId(), aUserId)
                .get();

        assertNotError(getPaymentSources);
    }
}
