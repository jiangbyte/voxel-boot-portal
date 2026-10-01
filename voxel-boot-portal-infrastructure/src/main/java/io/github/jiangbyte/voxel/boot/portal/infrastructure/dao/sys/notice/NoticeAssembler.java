package io.github.jiangbyte.voxel.boot.portal.infrastructure.dao.sys.notice;

import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.Notice;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.NoticeKind;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.NoticeStatus;

/**
 * 公告聚合与持久化对象互转。
 *
 * Author: Charlie
 */
public final class NoticeAssembler {

    private NoticeAssembler() {
    }

    /**
     * 持久化对象转聚合。
     */
    public static Notice toAggregate(NoticePO po) {
        if (po == null) {
            return null;
        }
        return Notice.reconstitute(
                po.getId(),
                po.getKind(),
                po.getTitle(),
                po.getContent(),
                po.getContentType(),
                po.getCategory(),
                po.getSeverity(),
                po.getTargetScope(),
                po.getTargetAccountTypes(),
                po.getTargetAccountIds(),
                po.getTargetDeptIds(),
                po.getTargetRoleIds(),
                po.getPublishLocations(),
                po.getIsPinned(),
                po.getPinnedUntil(),
                po.getSenderAccountType(),
                po.getSenderAccountId(),
                po.getSourceType(),
                po.getSourceId(),
                po.getStatus(),
                po.getPublishAt(),
                po.getRevokedAt(),
                po.getExpireAt(),
                po.getViewCount(),
                po.getExtra());
    }

    /**
     * 聚合转持久化对象。
     */
    public static NoticePO toPO(Notice notice) {
        if (notice == null) {
            return null;
        }
        NoticePO po = new NoticePO();
        po.setId(notice.getId());
        po.setKind(notice.getKind() == null ? NoticeKind.ANNOUNCEMENT.name() : notice.getKind().name());
        po.setTitle(notice.getTitle());
        po.setContent(notice.getContent());
        po.setContentType(notice.getContentType());
        po.setCategory(notice.getCategory());
        po.setSeverity(notice.getSeverity());
        po.setTargetScope(notice.getTargetScope());
        po.setTargetAccountTypes(notice.getTargetAccountTypes());
        po.setTargetAccountIds(notice.getTargetAccountIds());
        po.setTargetDeptIds(notice.getTargetDeptIds());
        po.setTargetRoleIds(notice.getTargetRoleIds());
        po.setPublishLocations(notice.getPublishLocations());
        po.setIsPinned(notice.getPinned());
        po.setPinnedUntil(notice.getPinnedUntil());
        po.setSenderAccountType(notice.getSenderAccountType());
        po.setSenderAccountId(notice.getSenderAccountId());
        po.setSourceType(notice.getSourceType());
        po.setSourceId(notice.getSourceId());
        po.setStatus(notice.getStatus() == null ? NoticeStatus.DRAFT.name() : notice.getStatus().name());
        po.setPublishAt(notice.getPublishAt());
        po.setRevokedAt(notice.getRevokedAt());
        po.setExpireAt(notice.getExpireAt());
        po.setViewCount(notice.getViewCount());
        po.setExtra(notice.getExtra() == null ? "{}" : notice.getExtra());
        return po;
    }
}
