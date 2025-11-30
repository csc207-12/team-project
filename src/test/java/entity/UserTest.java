package entity;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructorStoresBasicFields() {
        User user = new User("Alice", "secret", "Toronto", "F");

        assertEquals("Alice", user.getName());
        assertEquals("secret", user.getPassword());
        assertEquals("Toronto", user.getLocation());
        assertEquals("F", user.getGender());
        assertNull(user.getStyle());
    }

    @Test
    void styleSetterAndGetterWork() {
        User user = new User("Bob", "pw", "Vancouver", "M");

        Map<String, Boolean> style = new HashMap<>();
        style.put("casual", true);
        style.put("formal", false);

        user.setStyle(style);

        assertEquals(style, user.getStyle());
        assertTrue(user.getStyle().get("casual"));
        assertFalse(user.getStyle().get("formal"));
    }

    @Test
    void constructorRejectsEmptyNameOrPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("", "pw", "Toronto", "F"));

        assertThrows(IllegalArgumentException.class,
                () -> new User("Alice", "", "Toronto", "F"));
    }
}
