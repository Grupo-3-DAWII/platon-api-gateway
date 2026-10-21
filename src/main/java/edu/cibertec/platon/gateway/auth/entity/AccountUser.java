package edu.cibertec.platon.gateway.auth.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "users", schema = "account")
public class AccountUser {

    @Id
    @Column("iduser")
    private Long idUser;

    private String name;

    private String lastname;

    private String username;

    private String password;

    @Column("idrole")
    private Long idRole;

    @Column("idstatus")
    private Long idStatus;

    private String email;
}
