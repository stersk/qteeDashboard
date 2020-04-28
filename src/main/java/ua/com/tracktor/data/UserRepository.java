package ua.com.tracktor.data;

import org.springframework.data.repository.CrudRepository;
import ua.com.tracktor.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    Optional<User> findById(Long id);
}