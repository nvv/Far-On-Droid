package com.mediafire.sdk;

public interface MediaFireCredentialsStore {

    int TYPE_NONE = 0;
    int TYPE_EMAIL = 1;
    int TYPE_EKEY = 2;
    int TYPE_FACEBOOK = 3;
    int TYPE_TWITTER = 4;

    /**
     * clears credentials
     */
    void clear();

    /**
     * sets credentials to email
     * @param credentials
     */
    void setEmail(EmailCredentials credentials);

    /**
     * sets credentials to ekey
     * @param credentials
     */
    void setEkey(EkeyCredentials credentials);

    /**
     * sets credentials to facebook
     * @param credentials
     */
    void setFacebook(FacebookCredentials credentials);

    /**
     * sets credentials to twitter
     * @param credentials
     */
    void setTwitter(TwitterCredentials credentials);

    /**
     * gets type stored
     * @return
     */
    int getTypeStored();

    /**
     * gets email credentials
     * @return null if no email credentials are stored
     */
    EmailCredentials getEmailCredentials();

    /**
     * gets ekey credentials
     * @return null if no ekey credentials are stored
     */
    EkeyCredentials getEkeyCredentials();

    /**
     * gets facebook credentials
     * @return null if no facebook credentials are stored
     */
    FacebookCredentials getFacebookCredentials();

    /**
     * gets twitter credentials
     * @return null if no twitter credentials are stored
     */
    TwitterCredentials getTwitterCredentials();

    class EmailCredentials {

        private final String email;
        private final String password;

        public EmailCredentials(String email, String password) {

            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return "EmailCredentials{" +
                    "email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EmailCredentials that = (EmailCredentials) o;

            if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null) return false;
            return !(getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null);

        }

        @Override
        public int hashCode() {
            int result = getEmail() != null ? getEmail().hashCode() : 0;
            result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
            return result;
        }
    }

    class EkeyCredentials {

        private final String ekey;
        private final String password;

        public EkeyCredentials(String ekey, String password) {

            this.ekey = ekey;
            this.password = password;
        }

        public String getEkey() {
            return ekey;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return "EkeyCredentials{" +
                    "ekey='" + ekey + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EkeyCredentials that = (EkeyCredentials) o;

            if (getEkey() != null ? !getEkey().equals(that.getEkey()) : that.getEkey() != null) return false;
            return !(getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null);

        }

        @Override
        public int hashCode() {
            int result = getEkey() != null ? getEkey().hashCode() : 0;
            result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
            return result;
        }
    }

    class FacebookCredentials {

        private final String facebookAccessToken;

        public FacebookCredentials(String facebookAccessToken) {

            this.facebookAccessToken = facebookAccessToken;
        }

        public String getFacebookAccessToken() {
            return facebookAccessToken;
        }

        @Override
        public String toString() {
            return "FacebookCredentials{" +
                    "facebookAccessToken='" + facebookAccessToken + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FacebookCredentials that = (FacebookCredentials) o;

            return !(getFacebookAccessToken() != null ? !getFacebookAccessToken().equals(that.getFacebookAccessToken()) : that.getFacebookAccessToken() != null);

        }

        @Override
        public int hashCode() {
            return getFacebookAccessToken() != null ? getFacebookAccessToken().hashCode() : 0;
        }
    }

    class TwitterCredentials {

        private final String oauthToken;
        private final String oauthTokenSecret;

        public TwitterCredentials(String oauthToken, String oauthTokenSecret) {

            this.oauthToken = oauthToken;
            this.oauthTokenSecret = oauthTokenSecret;
        }

        public String getTwitterOauthToken() {
            return oauthToken;
        }

        public String getTwitterOauthTokenSecret() {
            return oauthTokenSecret;
        }

        @Override
        public String toString() {
            return "TwitterCredentials{" +
                    "oauthToken='" + oauthToken + '\'' +
                    ", oauthTokenSecret='" + oauthTokenSecret + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TwitterCredentials that = (TwitterCredentials) o;

            if (oauthToken != null ? !oauthToken.equals(that.oauthToken) : that.oauthToken != null) return false;
            return !(oauthTokenSecret != null ? !oauthTokenSecret.equals(that.oauthTokenSecret) : that.oauthTokenSecret != null);

        }

        @Override
        public int hashCode() {
            int result = oauthToken != null ? oauthToken.hashCode() : 0;
            result = 31 * result + (oauthTokenSecret != null ? oauthTokenSecret.hashCode() : 0);
            return result;
        }
    }
}
