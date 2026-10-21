package edu.cibertec.platon.gateway.auth.repository;

import edu.cibertec.platon.gateway.auth.dto.AuthAccountUser;
import edu.cibertec.platon.gateway.auth.entity.AccountUser;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AccountUserRepository extends ReactiveCrudRepository<AccountUser, Long> {

    @Query("""
            SELECT
                iduser AS "idUser",
                username,
                password,
                u.idrole AS "idRole",
                idstatus AS "idStatus",
                email
            FROM account.users u
            WHERE username = :username
            """)
    Mono<AuthAccountUser> findByUsername(String username);
}
