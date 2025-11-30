package data_access.user_storage;

import entity.User;

public interface UserRepository {
    void save(User user);
    User findByUsername(String username);
    boolean exists(String username);
}

