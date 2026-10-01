package io.github.jiangbyte.voxel.boot.portal.domain.kernel;

/**
 * 领域异常：不变量被破坏时抛出，由接口层映射为统一 API 响应。
 *
 * Author: Charlie
 */
public class DomainException extends RuntimeException {

    private final int code;

    public DomainException(String message) {
        this(400, message);
    }

    public DomainException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
