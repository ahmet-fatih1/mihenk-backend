package com.rentacar.core.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component("currentUserProvider")
public class CurrentUserProvider implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        // TODO: Aşama 2 — SecurityContextHolder'dan gerçek kullanıcıyı oku
        return Optional.of(1L);
    }
}
