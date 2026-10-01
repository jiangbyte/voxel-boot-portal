package io.github.jiangbyte.voxel.boot.portal.cases.sys.feedback;

import io.github.jiangbyte.voxel.boot.portal.domain.kernel.DomainException;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.Feedback;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.feedback.adapter.repository.FeedbackRepository;
import java.util.List;

/**
 * 用例接口（xfg-ddd Case）。
 *
 * Author: Charlie
 */
public interface IFeedbackCase {
    void submit(SubmitFeedbackCommand command);
    void update(UpdateFeedbackCommand command);
    void delete(List<String> ids);
}
