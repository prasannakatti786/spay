package com.spay.wallet.credentials;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
    @Query("select c from Credential c where c.usernameEmail =:username or c.usernamePhone =:username")
    Optional<Credential> findByUsernameEmailOrUsernamePhone(String username);

    @Transactional
    @Modifying
    @Query("update Credential c set c.sessionId = ?1 where c.id = ?2")
    void updateSessionIdById(String sessionId, Long id);

    @Query("select c.sessionId from Credential c where  c.id =:id")
    String sessionIdWithCredential(Long id);

    @Query("select c from Credential c where c.userId = ?1")
    Optional<Credential> findByUserId(String userId);

    @Query("select (count(c) > 0) from Credential c where c.usernameEmail = ?1")
    boolean existsByUsernameEmail(String usernameEmail);

    @Query("select (count(c) > 0) from Credential c where c.usernamePhone = ?1")
    boolean existsByUsernamePhone(String usernamePhone);
}