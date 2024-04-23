package com.tabling_batch.domain.repository;

import com.tabling_batch.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
