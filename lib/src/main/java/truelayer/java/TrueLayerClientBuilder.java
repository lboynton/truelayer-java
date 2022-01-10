package truelayer.java;

import static java.util.Optional.ofNullable;

/**
 * Builder class for TrueLayerClient instances. This is deliberately not managed
 * with Lombok annotations as its building phase is customized and slightly deviate from
 * the way Lombok builds stuff.
 */
public class TrueLayerClientBuilder {
    private ClientCredentialsOptions clientCredentialsOptions;

    private SigningOptions signingOptions;

    private boolean useSandbox;

    public TrueLayerClientBuilder() {

    }

    public TrueLayerClientBuilder clientCredentialsOptions(ClientCredentialsOptions credentialsOptions) {
        this.clientCredentialsOptions = credentialsOptions;
        return this;
    }

    public TrueLayerClientBuilder signingOptions(SigningOptions signingOptions) {
        this.signingOptions = signingOptions;
        return this;
    }

    public TrueLayerClientBuilder useSandbox() {
        this.useSandbox = true;
        return this;
    }

    public TrueLayerClient build() {
        var client = new TrueLayerClient(
                this.clientCredentialsOptions,
                ofNullable(this.signingOptions),
                this.useSandbox
        );
        return client;
    }
}