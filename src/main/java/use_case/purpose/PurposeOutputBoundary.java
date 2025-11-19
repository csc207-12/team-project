package use_case.purpose;

public interface PurposeOutputBoundary {
    void presentSuccess(PurposeOutputData data);
    void presentFailure(String errorMessage);
}
