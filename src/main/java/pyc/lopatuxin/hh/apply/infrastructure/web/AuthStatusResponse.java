package pyc.lopatuxin.hh.apply.infrastructure.web;

import java.time.Instant;

public record AuthStatusResponse(
        boolean exists,
        Instant lastModified
) {
}
