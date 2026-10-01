package io.github.jiangbyte.voxel.boot.portal.cases.iam.account;

import java.time.LocalDateTime;

/**
 * 跨上下文账号快照 DTO。
 *
 * Author: Charlie
 */
public class AccountInfo {

    private String id;
    private String accountType;
    private String accountStatus;
    private String passwordHash;
    private String latestLoginIp;
    private LocalDateTime latestLoginTime;
    private String latestLoginDevice;
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    private String cancelReason;
    private String primaryIdentifier;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getLatestLoginIp() {
        return latestLoginIp;
    }

    public void setLatestLoginIp(String latestLoginIp) {
        this.latestLoginIp = latestLoginIp;
    }

    public LocalDateTime getLatestLoginTime() {
        return latestLoginTime;
    }

    public void setLatestLoginTime(LocalDateTime latestLoginTime) {
        this.latestLoginTime = latestLoginTime;
    }

    public String getLatestLoginDevice() {
        return latestLoginDevice;
    }

    public void setLatestLoginDevice(String latestLoginDevice) {
        this.latestLoginDevice = latestLoginDevice;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getPrimaryIdentifier() {
        return primaryIdentifier;
    }

    public void setPrimaryIdentifier(String primaryIdentifier) {
        this.primaryIdentifier = primaryIdentifier;
    }
}
