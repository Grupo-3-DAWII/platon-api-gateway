package edu.cibertec.platon.gateway.auth.dto;

public record AuthAccountUser(
        Long idUser,
        String username,
        String password,
        Long idRole,
        Long idStatus,
        String email
) {
}
