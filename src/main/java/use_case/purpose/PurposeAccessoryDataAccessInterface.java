package use_case.purpose;

public interface PurposeAccessoryDataAccessInterface {

    /**
     * Call Gemini API to generate accessory suggestions
     * based on the given purpose.
     *
     * @param purpose user-provided purpose text
     * @return plain-text accessory suggestions, or null on failure
     */
    String generateAccessorySuggestions(String purpose);
}