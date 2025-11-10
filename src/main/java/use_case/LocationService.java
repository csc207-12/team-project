package use_case;

/**
 * Abstract location service to get current city name.
 */
public interface LocationService {
    String getCurrentCity() throws Exception;
}
