package pyc.lopatuxin.hh.apply.dto;

import java.time.Instant;

public record AuthStatusResponse(
        boolean exists,
        Instant lastModified
) {
}
