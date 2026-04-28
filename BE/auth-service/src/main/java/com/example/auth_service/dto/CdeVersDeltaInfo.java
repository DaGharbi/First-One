package com.example.auth_service.dto;

import java.math.BigDecimal;

public record CdeVersDeltaInfo(
        String codeAgence,
        String libAgence,
        String codeDevise,
        String libDevise,
        String datePass,
        String naturePass,
        String descriptionClient,
        Boolean isBillets,
        BigDecimal montant,
        Short cdeVers
) {
}
