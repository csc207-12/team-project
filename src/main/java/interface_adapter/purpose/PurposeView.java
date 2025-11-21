package interface_adapter.purpose;

public interface PurposeView {

    void onPurposeAccessorySuccess(String suggestionsText);

    void onPurposeAccessoryFailure(String errorMessage);
}
