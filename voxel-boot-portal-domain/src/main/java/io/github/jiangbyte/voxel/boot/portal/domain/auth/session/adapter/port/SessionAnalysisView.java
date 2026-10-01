package io.github.jiangbyte.voxel.boot.portal.domain.auth.session.adapter.port;

/**
 * 在线会话分析统计。
 *
 * Author: Charlie
 */
public class SessionAnalysisView {

    private int onlineAccountCount;
    private int onlineTokenCount;
    private int adminAccountCount;
    private int portalAccountCount;
    private int oneHourNewCount;
    private int maxTokenCount;

    public int getOnlineAccountCount() {
        return onlineAccountCount;
    }

    public void setOnlineAccountCount(int onlineAccountCount) {
        this.onlineAccountCount = onlineAccountCount;
    }

    public int getOnlineTokenCount() {
        return onlineTokenCount;
    }

    public void setOnlineTokenCount(int onlineTokenCount) {
        this.onlineTokenCount = onlineTokenCount;
    }

    public int getAdminAccountCount() {
        return adminAccountCount;
    }

    public void setAdminAccountCount(int adminAccountCount) {
        this.adminAccountCount = adminAccountCount;
    }

    public int getPortalAccountCount() {
        return portalAccountCount;
    }

    public void setPortalAccountCount(int portalAccountCount) {
        this.portalAccountCount = portalAccountCount;
    }

    public int getOneHourNewCount() {
        return oneHourNewCount;
    }

    public void setOneHourNewCount(int oneHourNewCount) {
        this.oneHourNewCount = oneHourNewCount;
    }

    public int getMaxTokenCount() {
        return maxTokenCount;
    }

    public void setMaxTokenCount(int maxTokenCount) {
        this.maxTokenCount = maxTokenCount;
    }
}
