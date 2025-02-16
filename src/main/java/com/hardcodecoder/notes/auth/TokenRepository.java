package com.hardcodecoder.notes.auth;

import com.hardcodecoder.notes.auth.model.Token;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Long> {

    @NonNull
    @Query("SELECT * FROM TOKEN WHERE ACCOUNT_ID = :accountId")
    Optional<Token> findByAccountId(long accountId);
}