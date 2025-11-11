package interface_adapter.style;
/** View interface for style preferences. */
public interface StyleView {
    void onStyleSaveSuccess(String message);
    void onStyleSaveFailure(String message);
}
