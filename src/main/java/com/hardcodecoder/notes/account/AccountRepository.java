package com.hardcodecoder.notes.account;

import com.hardcodecoder.notes.account.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {}