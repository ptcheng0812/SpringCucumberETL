package etl.data;

public class Secret {
    private String typeOfAuth;
    private String basicUsername;
    private String basicPassword;
    private String oAuth1ConsumerKey;
    private String oAuth1ConsumerSecret;
    private String oAuth1Token;
    private String oAuth1TokenSecret;
    private String oAuth2Token;
    private String jwtToken;

    // Private constructor to enforce usage of builder
    public Secret() {}

    public String getTypeOfAuth() {return typeOfAuth;}

    public String getBasicUsername() {return basicUsername;}

    public String getBasicPassword() {return basicPassword;}

    public String getOAuth1ConsumerKey() {return oAuth1ConsumerKey;}

    public String getOAuth1ConsumerSecret() {return oAuth1ConsumerSecret;}

    public String getOAuth1Token() {return oAuth1Token;}

    public String getOAuth1TokenSecret() {return oAuth1TokenSecret;}

    public String getOAuth2Token() {return oAuth2Token;}

    public String getJwtToken() {return jwtToken;}

    // Getter methods
    public static class SecretBuilder {
        private String typeOfAuth;
        private String basicUsername;
        private String basicPassword;
        private String oAuth1ConsumerKey;
        private String oAuth1ConsumerSecret;
        private String oAuth1Token;
        private String oAuth1TokenSecret;
        private String oAuth2Token;
        private String jwtToken;

        public SecretBuilder() {}

        public SecretBuilder typeOfAuth(String typeOfAuth) {
            this.typeOfAuth = typeOfAuth;
            return this;
        }

        public SecretBuilder basicUsername(String basicUsername) {
            this.basicUsername = basicUsername;
            return this;
        }

        public SecretBuilder basicPassword(String basicPassword) {
            this.basicPassword = basicPassword;
            return this;
        }

        public SecretBuilder oAuth1ConsumerKey(String oAuth1ConsumerKey) {
            this.oAuth1ConsumerKey = oAuth1ConsumerKey;
            return this;
        }

        public SecretBuilder oAuth1ConsumerSecret(String oAuth1ConsumerSecret) {
            this.oAuth1ConsumerSecret = oAuth1ConsumerSecret;
            return this;
        }

        public SecretBuilder oAuth1Token(String oAuth1Token) {
            this.oAuth1Token = oAuth1Token;
            return this;
        }

        public SecretBuilder oAuth1TokenSecret(String oAuth1TokenSecret) {
            this.oAuth1TokenSecret = oAuth1TokenSecret;
            return this;
        }

        public SecretBuilder oAuth2Token(String oAuth2Token) {
            this.oAuth2Token = oAuth2Token;
            return this;
        }

        public SecretBuilder jwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
            return this;
        }

        public Secret build() {
            Secret secret = new Secret();
            secret.typeOfAuth = this.typeOfAuth;
            secret.basicUsername = this.basicUsername;
            secret.basicPassword = this.basicPassword;
            secret.oAuth1ConsumerKey = this.oAuth1ConsumerKey;
            secret.oAuth1ConsumerSecret = this.oAuth1ConsumerSecret;
            secret.oAuth1Token = this.oAuth1Token;
            secret.oAuth1TokenSecret = this.oAuth1TokenSecret;
            secret.oAuth2Token = this.oAuth2Token;
            secret.jwtToken = this.jwtToken;
            return secret;
        }
    }

    public static SecretBuilder builder() {
        return new SecretBuilder();
    }
}
