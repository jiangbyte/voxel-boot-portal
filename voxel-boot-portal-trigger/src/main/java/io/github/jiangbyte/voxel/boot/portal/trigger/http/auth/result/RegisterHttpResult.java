package io.github.jiangbyte.voxel.boot.portal.trigger.http.auth.result;

import io.github.jiangbyte.voxel.boot.portal.cases.auth.RegisterResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "注册响应")
@Data
public class RegisterHttpResult {
    private String accountId;
    private String account;
    private String accountType;

    public static RegisterHttpResult from(RegisterResult result) {
        if (result == null) return null;
        RegisterHttpResult http = new RegisterHttpResult();
        http.setAccountId(result.getAccountId());
        http.setAccount(result.getAccount());
        http.setAccountType(result.getAccountType());
        return http;
    }
}
