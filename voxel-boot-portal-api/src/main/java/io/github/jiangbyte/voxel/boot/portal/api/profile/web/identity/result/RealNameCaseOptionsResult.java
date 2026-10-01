package io.github.jiangbyte.voxel.boot.portal.api.profile.web.identity.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "实名选项")
public class RealNameCaseOptionsResult {
    private List<BusinessOptionResult> businessTypes = new ArrayList<>();
    private List<String> documentTypes = new ArrayList<>();

    @Data
    public static class BusinessOptionResult {
        private String businessType;
        private String label;
        private List<String> channels;
    }
}
