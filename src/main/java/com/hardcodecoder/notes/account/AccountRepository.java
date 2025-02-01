package com.hardcodecoder.notes.account;

import com.hardcodecoder.notes.account.model.Account;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    @NonNull
    @Query("SELECT * FROM ACCOUNT WHERE EMAIL = :email")
    Optional<Account> findByEmail(@NonNull String email);
}