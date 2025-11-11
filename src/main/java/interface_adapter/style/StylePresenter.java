package interface_adapter.style;
import use_case.style.StyleOutputBoundary;
import use_case.style.StyleOutputData;
/** Presenter that transforms style output data into view calls. */
public class StylePresenter implements StyleOutputBoundary {
    private final StyleView view;
    public StylePresenter(StyleView view) {
        this.view = view;
    }
    @Override
    public void present(StyleOutputData output) {
        if (output.isSuccess()) {
            view.onStyleSaveSuccess(output.getMessage());
        } else {
            view.onStyleSaveFailure(output.getMessage());
        }
    }
}
