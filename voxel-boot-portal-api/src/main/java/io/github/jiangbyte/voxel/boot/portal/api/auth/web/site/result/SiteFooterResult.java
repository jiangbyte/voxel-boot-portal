package io.github.jiangbyte.voxel.boot.portal.api.auth.web.site.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 站点页脚公开信息：版权与备案。
 *
 * Author: Charlie
 */
@Schema(description = "站点页脚")
@Data
public class SiteFooterResult {

    private String copyrightText = "";
    private String copyrightUrl = "";
    /** ICP 备案号 */
    private String icpNumber = "";
    /** ICP 备案查询链接 */
    private String icpUrl = "";
    /** 公安备案号 */
    private String psbNumber = "";
    /** 公安备案查询链接 */
    private String psbUrl = "";
}
