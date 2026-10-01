package io.github.jiangbyte.voxel.boot.portal.domain.profile.identity.adapter.port;

/**
 * 实名敏感字段加解密、哈希与脱敏端口。
 *
 * Author: Charlie
 */
public interface IdentityCryptoPort {

    String encrypt(String plaintext);

    String decrypt(String ciphertext);

    String hashDocumentNo(String documentType, String documentNo);

    String maskRealName(String realName);

    String maskDocumentNo(String documentNo);
}
