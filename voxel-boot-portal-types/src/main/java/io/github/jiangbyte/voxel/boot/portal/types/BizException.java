package io.github.jiangbyte.voxel.boot.portal.types;

import lombok.Getter;

/**
 * 业务异常：携带业务码与提示，由全局异常处理转为 ApiResponse。
 *
 * Author: Charlie
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        this(400, message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
