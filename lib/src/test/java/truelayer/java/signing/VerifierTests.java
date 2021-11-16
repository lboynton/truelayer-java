package truelayer.java.signing;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VerifierTests {

    private static final String KEYS_LOCATION = "src/test/resources/keys/";

    private static final String A_SIGNATURE = "eyJhbGciOiJFUzUxMiIsImtpZCI6ImFfa2V5X2lkIiwidGxfdmVyc2lvbiI6IjIiLCJ0bF9oZWFkZXJzIjoiSWRlbXBvdGVuY3ktS2V5In0=..Aam_czHB89OBWZux0BVYwEIrhoZFfFYJFJoX4T0WvGAO79LWdbLs472HzfWVhQzGstPOr3gMWSQxL682baocWmbkAcOVY7oGOvtM7DbTJ40SFUolEhP7KCJFzcLqxViBAgaIEOw-bFogHL3BirrFJcobi_xS2m_rT2TDlaBm5wby0NYG";
    private static final String A_PAYLOAD = "{\"foo\":\"bar\"}";
    private static final String A_KEY_ID = "a_key_id";
    private static final String A_METHOD = "POST";
    private static final String A_PATH = "/test";
    private static final Map<String, String> headers = Map.of("Idempotency-Key", "idemp_key");

    @Test
    @SneakyThrows
    @DisplayName("It should verify a static signature")
    public void VerifierShouldVerifyStaticSignature() {
        var verifierBuilder = new Verifier.Builder(A_KEY_ID, A_SIGNATURE, Files.readAllBytes(Path.of(new StringBuilder(KEYS_LOCATION).append("ec512-public-key.pem").toString())))
                .addHttpMethod(A_METHOD)
                .addPath(A_PATH)
                .addBody(A_PAYLOAD);

        headers.forEach(verifierBuilder::addHeader);
        assertTrue(verifierBuilder.verify());
    }

    @Test
    @SneakyThrows
    @DisplayName("It should verify a generated signature")
    public void VerifierShouldVerifyGeneratedSignature() {
        var signatureBuilder = new Signer.Builder(A_KEY_ID, Files.readAllBytes(Path.of(new StringBuilder(KEYS_LOCATION).append("ec512-private-key.pem").toString())))
                .addHttpMethod(A_METHOD)
                .addPath(A_PATH)
                .addBody(A_PAYLOAD);
        headers.forEach(signatureBuilder::addHeader);
        var signature = signatureBuilder.getSignature();

        var verifierBuilder = new Verifier.Builder(A_KEY_ID, signature, Files.readAllBytes(Path.of(new StringBuilder(KEYS_LOCATION).append("ec512-public-key.pem").toString())))
                .addHttpMethod(A_METHOD)
                .addPath(A_PATH)
                .addBody(A_PAYLOAD);

        headers.forEach(verifierBuilder::addHeader);
        assertTrue(verifierBuilder.verify());
    }

}