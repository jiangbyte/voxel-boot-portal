package io.github.jiangbyte.voxel.boot.portal.cases.sys.weakpassword;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.WeakPassword;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.weakpassword.adapter.repository.WeakPasswordRepository;
import java.util.List;

/**
 * 用例接口。
 *
 * Author: Charlie
 */
public interface IWeakPasswordCase {
    void create(CreateWeakPasswordCommand command);
    void update(UpdateWeakPasswordCommand command);
    void delete(List<String> ids);
}
