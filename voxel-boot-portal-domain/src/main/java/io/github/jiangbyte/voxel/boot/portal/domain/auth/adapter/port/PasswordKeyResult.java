package io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port;

/**
 * RSA 密码传输公钥结果。
 *
 * Author: Charlie
 */
public class PasswordKeyResult {

    private String keyId;
    private String publicKey;

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
