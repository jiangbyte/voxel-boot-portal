package io.github.jiangbyte.voxel.boot.portal.cases.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录页公开配置。
 *
 * Author: Charlie
 */
public class AuthOptionsResult {

    private String accountType;
    private Integer allowAccount = 1;
    private Integer allowEmail = 1;
    private Integer allowPhone = 1;
    private Integer allowOtp = 1;
    private Integer registerEnabled = 0;
    private Integer registerAllowAccount = 0;
    private Integer registerAllowEmail = 0;
    private Integer registerAllowPhone = 0;
    private Integer registerRequireEmail = 0;
    private Integer registerRequirePhone = 0;
    private Integer forceBindEmail = 0;
    private Integer forceBindPhone = 0;
    private List<OauthProviderOption> oauthProviders = new ArrayList<>();
    private String passwordChangeVerifyMethod = "OLD_PASSWORD";
    private String copyrightText = "";
    private String copyrightUrl = "";
    private SiteFooterSnapshot siteFooter = new SiteFooterSnapshot();

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Integer getAllowAccount() {
        return allowAccount;
    }

    public void setAllowAccount(Integer allowAccount) {
        this.allowAccount = allowAccount;
    }

    public Integer getAllowEmail() {
        return allowEmail;
    }

    public void setAllowEmail(Integer allowEmail) {
        this.allowEmail = allowEmail;
    }

    public Integer getAllowPhone() {
        return allowPhone;
    }

    public void setAllowPhone(Integer allowPhone) {
        this.allowPhone = allowPhone;
    }

    public Integer getAllowOtp() {
        return allowOtp;
    }

    public void setAllowOtp(Integer allowOtp) {
        this.allowOtp = allowOtp;
    }

    public Integer getRegisterEnabled() {
        return registerEnabled;
    }

    public void setRegisterEnabled(Integer registerEnabled) {
        this.registerEnabled = registerEnabled;
    }

    public Integer getRegisterAllowAccount() {
        return registerAllowAccount;
    }

    public void setRegisterAllowAccount(Integer registerAllowAccount) {
        this.registerAllowAccount = registerAllowAccount;
    }

    public Integer getRegisterAllowEmail() {
        return registerAllowEmail;
    }

    public void setRegisterAllowEmail(Integer registerAllowEmail) {
        this.registerAllowEmail = registerAllowEmail;
    }

    public Integer getRegisterAllowPhone() {
        return registerAllowPhone;
    }

    public void setRegisterAllowPhone(Integer registerAllowPhone) {
        this.registerAllowPhone = registerAllowPhone;
    }

    public Integer getRegisterRequireEmail() {
        return registerRequireEmail;
    }

    public void setRegisterRequireEmail(Integer registerRequireEmail) {
        this.registerRequireEmail = registerRequireEmail;
    }

    public Integer getRegisterRequirePhone() {
        return registerRequirePhone;
    }

    public void setRegisterRequirePhone(Integer registerRequirePhone) {
        this.registerRequirePhone = registerRequirePhone;
    }

    public Integer getForceBindEmail() {
        return forceBindEmail;
    }

    public void setForceBindEmail(Integer forceBindEmail) {
        this.forceBindEmail = forceBindEmail;
    }

    public Integer getForceBindPhone() {
        return forceBindPhone;
    }

    public void setForceBindPhone(Integer forceBindPhone) {
        this.forceBindPhone = forceBindPhone;
    }

    public List<OauthProviderOption> getOauthProviders() {
        return oauthProviders;
    }

    public void setOauthProviders(List<OauthProviderOption> oauthProviders) {
        this.oauthProviders = oauthProviders;
    }

    public String getPasswordChangeVerifyMethod() {
        return passwordChangeVerifyMethod;
    }

    public void setPasswordChangeVerifyMethod(String passwordChangeVerifyMethod) {
        this.passwordChangeVerifyMethod = passwordChangeVerifyMethod;
    }

    public String getCopyrightText() {
        return copyrightText;
    }

    public void setCopyrightText(String copyrightText) {
        this.copyrightText = copyrightText;
    }

    public String getCopyrightUrl() {
        return copyrightUrl;
    }

    public void setCopyrightUrl(String copyrightUrl) {
        this.copyrightUrl = copyrightUrl;
    }

    public SiteFooterSnapshot getSiteFooter() {
        return siteFooter;
    }

    public void setSiteFooter(SiteFooterSnapshot siteFooter) {
        this.siteFooter = siteFooter;
    }

    /**
     * OAuth 提供商选项。
     */
    public static class OauthProviderOption {
        private String provider;
        private String label;
        private Integer webOAuth = 0;
        private Integer enabled;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Integer getWebOAuth() {
            return webOAuth;
        }

        public void setWebOAuth(Integer webOAuth) {
            this.webOAuth = webOAuth;
        }

        public Integer getEnabled() {
            return enabled;
        }

        public void setEnabled(Integer enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * 页脚快照（与公开 site-footer 字段对齐）。
     */
    public static class SiteFooterSnapshot {
        private String copyrightText = "";
        private String copyrightUrl = "";
        private String icpNumber = "";
        private String icpUrl = "";
        private String psbNumber = "";
        private String psbUrl = "";

        public String getCopyrightText() {
            return copyrightText;
        }

        public void setCopyrightText(String copyrightText) {
            this.copyrightText = copyrightText;
        }

        public String getCopyrightUrl() {
            return copyrightUrl;
        }

        public void setCopyrightUrl(String copyrightUrl) {
            this.copyrightUrl = copyrightUrl;
        }

        public String getIcpNumber() {
            return icpNumber;
        }

        public void setIcpNumber(String icpNumber) {
            this.icpNumber = icpNumber;
        }

        public String getIcpUrl() {
            return icpUrl;
        }

        public void setIcpUrl(String icpUrl) {
            this.icpUrl = icpUrl;
        }

        public String getPsbNumber() {
            return psbNumber;
        }

        public void setPsbNumber(String psbNumber) {
            this.psbNumber = psbNumber;
        }

        public String getPsbUrl() {
            return psbUrl;
        }

        public void setPsbUrl(String psbUrl) {
            this.psbUrl = psbUrl;
        }
    }
}
