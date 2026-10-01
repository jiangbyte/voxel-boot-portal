package io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.file;

import io.github.jiangbyte.voxel.boot.portal.api.sys.web.file.param.SysFileObjectNameParam;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.file.result.SysFileResult;
import io.github.jiangbyte.voxel.boot.portal.api.sys.web.file.result.SysFileUrlResult;
import io.github.jiangbyte.voxel.boot.portal.api.web.ApiResponse;
import io.github.jiangbyte.voxel.boot.portal.api.web.IdParam;
import io.github.jiangbyte.voxel.boot.portal.api.web.IdsParam;
import io.github.jiangbyte.voxel.boot.portal.cases.sys.file.IFileCase;
import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileDownloadPayload;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileQueryService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileUrlView;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.file.adapter.port.FileView;
import io.github.jiangbyte.voxel.boot.portal.trigger.http.sys.file.assembler.SysFileResultAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 门户端文件 API：真实上传/流式下载，并按 createdBy 校验归属。
 *
 * Author: Charlie
 */
@Tag(name = "门户端文件 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortalFileController {

    private final IFileCase fileApplicationService;
    private final FileQueryService fileQueryService;
    private final SessionPort sessionPort;

    /** 上传文件到对象存储并登记元数据。 */
    @Operation(summary = "上传文件")
    @PostMapping("/v1/portal/sys/file/upload")
    public ApiResponse<SysFileResult> upload(@RequestParam("file") MultipartFile file,
                                             @RequestParam(value = "storage_provider", required = false) String storageProvider) {
        // 1. 校验 multipart
        if (file == null || file.isEmpty()) {
            throw new DomainException("File is required");
        }
        // 2. 真实上传
        try {
            FileView view = fileApplicationService.upload(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getInputStream(),
                    storageProvider);
            return ApiResponse.ok(SysFileResultAssembler.INSTANCE.toResult(view));
        } catch (IOException ex) {
            throw new DomainException(500, "Failed to read upload stream");
        }
    }

    /** 查询文件详情（须归属当前门户账号）。 */
    @Operation(summary = "查询文件详情")
    @GetMapping("/v1/portal/sys/file/detail")
    public ApiResponse<SysFileResult> detail(@Valid @ModelAttribute IdParam param) {
        // 1. 查询详情
        FileView view = fileQueryService.detail(param.getId());
        // 2. 归属校验
        assertOwnedByCurrent(view);
        return ApiResponse.ok(SysFileResultAssembler.INSTANCE.toResult(view));
    }

    /** 按 ID 批量查询（逐条归属校验）。 */
    @Operation(summary = "按 ID 批量查询")
    @PostMapping("/v1/portal/sys/file/list_by_ids")
    public ApiResponse<List<SysFileResult>> listByIds(@Valid @RequestBody IdsParam param) {
        // 1. 批量加载
        List<FileView> views = fileQueryService.listByIds(param.getIds());
        // 2. 逐条校验归属后装配
        List<SysFileResult> records = new ArrayList<>(views.size());
        for (FileView view : views) {
            assertOwnedByCurrent(view);
            records.add(SysFileResultAssembler.INSTANCE.toResult(view));
        }
        return ApiResponse.ok(records);
    }

    /** 流式下载（须归属当前门户账号）。 */
    @Operation(summary = "下载文件")
    @GetMapping("/v1/portal/sys/file/download")
    public ResponseEntity<InputStreamResource> download(@Valid @ModelAttribute IdParam param) {
        // 1. 归属校验
        FileView view = fileQueryService.detail(param.getId());
        assertOwnedByCurrent(view);
        // 2. 加载流
        FileDownloadPayload payload = fileApplicationService.download(param.getId());
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (StringUtils.hasText(payload.contentType())) {
            try {
                mediaType = MediaType.parseMediaType(payload.contentType());
            } catch (Exception ignored) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }
        String filename = StringUtils.hasText(payload.originalName()) ? payload.originalName() : "download";
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(mediaType);
        if (payload.size() != null && payload.size() >= 0) {
            builder.contentLength(payload.size());
        }
        return builder.body(new InputStreamResource(payload.stream()));
    }

    /** 解析 URL（须归属当前门户账号）。 */
    @Operation(summary = "解析访问 URL")
    @PostMapping("/v1/portal/sys/file/url")
    public ApiResponse<SysFileUrlResult> url(@Valid @RequestBody SysFileObjectNameParam param) {
        // 1. 按 objectName 加载并校验归属
        FileView view = fileQueryService.findByObjectName(param.getObjectName());
        if (view == null) {
            throw new DomainException(404, "File not found");
        }
        assertOwnedByCurrent(view);
        // 2. 走存储解析（禁止 objectName 回退）
        return ApiResponse.ok(toUrlResult(fileApplicationService.resolvePublicUrl(param.getObjectName())));
    }

    /** 预签名 URL。 */
    @Operation(summary = "预签名 URL")
    @PostMapping("/v1/portal/sys/file/presigned_url")
    public ApiResponse<SysFileUrlResult> presignedUrl(@Valid @RequestBody SysFileObjectNameParam param) {
        FileView view = fileQueryService.findByObjectName(param.getObjectName());
        if (view == null) {
            throw new DomainException(404, "File not found");
        }
        assertOwnedByCurrent(view);
        return ApiResponse.ok(toUrlResult(fileApplicationService.resolvePresignedUrl(param.getObjectName())));
    }

    /**
     * 校验文件 createdBy 属于当前门户登录账号。
     */
    private void assertOwnedByCurrent(FileView file) {
        // 1. 空文件视为不存在
        if (file == null) {
            throw new DomainException(404, "File not found");
        }
        // 2. 要求门户登录
        String accountId = sessionPort.currentPortalAccountId();
        if (!StringUtils.hasText(accountId)) {
            throw new DomainException(401, "Not logged in");
        }
        // 3. createdBy 为空或不匹配则拒绝
        if (!StringUtils.hasText(file.createdBy()) || !accountId.equals(file.createdBy())) {
            throw new DomainException(403, "无权访问该文件");
        }
    }

    private static SysFileUrlResult toUrlResult(FileUrlView view) {
        SysFileUrlResult result = new SysFileUrlResult();
        result.setObjectName(view.objectName());
        result.setUrl(view.url());
        result.setOriginalName(view.originalName());
        result.setSize(view.size());
        result.setContentType(view.contentType());
        return result;
    }
}
