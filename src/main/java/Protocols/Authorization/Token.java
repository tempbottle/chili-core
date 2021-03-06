package Protocols.Authorization;

import java.time.Instant;

/**
 * @author Robin Duda
 *         used to request authentication by token.
 */
public class Token {
    private String key;
    private long expiry;
    private String domain;

    public Token() {
    }

    public Token(TokenFactory factory, String domain) {
        try {
            this.domain = domain;
            this.expiry = Instant.now().getEpochSecond() + 3600 * 24 * 31;
            this.key = factory.signToken(domain, this.expiry);
        } catch (Throwable e) {
            throw new RuntimeException("Token factory failed to generate token.");
        }
    }

    public String getKey() {
        return key;
    }

    public Token setKey(String key) {
        this.key = key;
        return this;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}