package use_case;

import java.util.List;

public class AccessoryOutputData {
    public final String city;
    public final List<String> accessories;
    public final boolean success;
    public final String message;

    public AccessoryOutputData(String city, List<String> accessories, boolean success, String message) {
        this.city = city;
        this.accessories = accessories;
        this.success = success;
        this.message = message;
    }
}
