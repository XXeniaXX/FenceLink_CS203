package com.example.FenceLink.token;

import java.util.List;

public class JWKS {
    private List<JWK> keys;

    // Getter and Setter
    public List<JWK> getKeys() {
        return keys;
    }

    public void setKeys(List<JWK> keys) {
        this.keys = keys;
    }
}
