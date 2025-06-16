package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseConduit extends CrudRepository<User, Long> {
}
