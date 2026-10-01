package io.github.jiangbyte.voxel.boot.portal.cases.profile.identity;

import java.util.List;

/**
 * 实名选项视图。
 *
 * Author: Charlie
 */
public record RealNameCaseOptionsView(
        List<BusinessOption> businessTypes,
        List<String> documentTypes
) {
    public record BusinessOption(String businessType, String label, List<String> channels) {
    }
}
