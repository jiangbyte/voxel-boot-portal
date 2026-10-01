package io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port;

/**
 * 图形验证码签发结果。
 *
 * Author: Charlie
 */
public class CaptchaResult {

    private String captchaId;
    private String imageType;
    private String imageBase64;

    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
