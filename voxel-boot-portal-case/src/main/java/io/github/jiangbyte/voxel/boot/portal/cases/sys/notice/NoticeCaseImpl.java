package io.github.jiangbyte.voxel.boot.portal.cases.sys.notice;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.Notice;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.notice.adapter.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * 公告写用例：创建、更新、删除、发布、撤回与置顶。
 *
 * Author: Charlie
 */
@Service
@RequiredArgsConstructor
public class NoticeCaseImpl implements INoticeCase {

    private final NoticeRepository noticeRepository;


    /**
     * 创建公告草稿。
     */
    @Transactional
    public void create(CreateNoticeCommand command) {
        // 1. 工厂创建草稿聚合
        Notice notice = Notice.create(
                command.kind(),
                command.title(),
                command.content(),
                command.contentType(),
                command.category(),
                command.severity(),
                command.targetScope(),
                command.targetAccountTypes(),
                command.targetAccountIds(),
                command.targetDeptIds(),
                command.targetRoleIds(),
                command.publishLocations(),
                command.pinned(),
                command.pinnedUntil(),
                command.expireAt(),
                command.extra());
        // 2. 持久化
        noticeRepository.save(notice);
    }

    /**
     * 更新公告。
     */
    @Transactional
    public void update(UpdateNoticeCommand command) {
        // 1. 加载聚合
        Notice notice = require(command.id());
        // 2. 聚合内更新并保存
        notice.update(
                command.kind(),
                command.title(),
                command.content(),
                command.contentType(),
                command.category(),
                command.severity(),
                command.targetScope(),
                command.targetAccountTypes(),
                command.targetAccountIds(),
                command.targetDeptIds(),
                command.targetRoleIds(),
                command.publishLocations(),
                command.pinned(),
                command.pinnedUntil(),
                command.expireAt(),
                command.extra());
        noticeRepository.save(notice);
    }

    /**
     * 批量删除。
     */
    @Transactional
    public void delete(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个删除
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            noticeRepository.findById(id).ifPresent(notice -> {
                notice.markDeleted();
                noticeRepository.remove(notice);
            });
        }
    }

    /**
     * 批量发布。
     */
    @Transactional
    public void publish(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个发布
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            Notice notice = require(id);
            notice.publish();
            noticeRepository.save(notice);
        }
    }

    /**
     * 批量撤回。
     */
    @Transactional
    public void revoke(List<String> ids) {
        // 1. 空列表直接返回
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // 2. 逐个撤回
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            Notice notice = require(id);
            notice.revoke();
            noticeRepository.save(notice);
        }
    }

    /**
     * 置顶/取消置顶。
     */
    @Transactional
    public void pin(String id, Integer pinned, LocalDateTime pinnedUntil) {
        // 1. 加载聚合
        Notice notice = require(id);
        // 2. 设置置顶并保存
        notice.pin(pinned, pinnedUntil);
        noticeRepository.save(notice);
    }

    private Notice require(String id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new DomainException(404, "Notice not found"));
    }
}
