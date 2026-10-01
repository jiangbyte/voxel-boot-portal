package io.github.jiangbyte.voxel.boot.portal.domain.kernel;

/**
 * 业务标志位工具：统一 0/1，禁止 boolean 建模。
 *
 * Author: Charlie
 */
public final class Flags {

    private Flags() {
    }

    /** 是否为开启（1）。 */
    public static boolean isOn(Integer value) {
        return value != null && value == 1;
    }

    /** 是否为关闭（0 或空）。 */
    public static boolean isOff(Integer value) {
        return !isOn(value);
    }

    /** 归一化为 0 或 1。 */
    public static int of(Integer value) {
        return isOn(value) ? 1 : 0;
    }

    /** 由布尔语义构造标志位。 */
    public static int of(boolean value) {
        return value ? 1 : 0;
    }
}
