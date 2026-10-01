package io.github.jiangbyte.voxel.boot.portal.cases.sys.notice;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.Notice;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.repository.NoticeRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用例接口。
 *
 * Author: Charlie
 */
public interface INoticeCase {
    void create(CreateNoticeCommand command);
    void update(UpdateNoticeCommand command);
    void delete(List<String> ids);
    void publish(List<String> ids);
    void revoke(List<String> ids);
    void pin(String id, Integer pinned, LocalDateTime pinnedUntil);
}
