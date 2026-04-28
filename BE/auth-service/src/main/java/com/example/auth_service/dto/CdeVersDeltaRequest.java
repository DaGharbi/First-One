package com.example.auth_service.dto;

import java.math.BigDecimal;

public record CdeVersDeltaRequest(
        String codeAgence,
        String codeDevise,
        String datePass,
        String naturePass,
        String descriptionClient,
        Boolean isBillets,
        BigDecimal montant,
        Short cdeVers
) {
}
