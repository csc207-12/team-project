package data_access;

import use_case.UserRepository;
import entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple in-memory user repository used while developing the UI and use case.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> store = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        store.put(user.getName(), user);
    }

    @Override
    public User findByUsername(String username) {
        return store.get(username);
    }

    @Override
    public boolean exists(String username) {
        return store.containsKey(username);
    }
}

