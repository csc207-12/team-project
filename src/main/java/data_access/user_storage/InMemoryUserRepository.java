package data_access.user_storage;

import entity.User;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of UserRepository for testing purposes.
 * Provides a simple HashMap-based storage that can be easily reset between tests.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getName(), user);
    }

    @Override
    public User findByUsername(String username) {
        return users.get(username);
    }

    @Override
    public boolean exists(String username) {
        return users.containsKey(username);
    }

}

