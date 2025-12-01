package use_case.outfit_image_generation;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OutfitImageGenerationInteractorTest {

    @Test
    void testFailureWhenOutfitsNull() {
        FakeDAO dao = new FakeDAO();
        FakePresenter presenter = new FakePresenter();

        OutfitImageGenerationInteractor interactor =
                new OutfitImageGenerationInteractor(dao, presenter);

        interactor.generateImages(null);

        assertTrue(presenter.failureCalled);
        assertEquals("No outfits provided.", presenter.message);
    }

    @Test
    void testFailureWhenDaoReturnsEmpty() {
        FakeDAO dao = new FakeDAO();
        dao.returnEmpty = true;
        FakePresenter presenter = new FakePresenter();

        OutfitImageGenerationInteractor interactor =
                new OutfitImageGenerationInteractor(dao, presenter);

        interactor.generateImages(List.of("Outfit 1"));

        assertTrue(presenter.failureCalled);
        assertEquals("Gemini did not return any images.", presenter.message);
    }

    @Test
    void testSuccess() {
        FakeDAO dao = new FakeDAO();
        dao.fakeImage = "BASE64IMAGE";
        FakePresenter presenter = new FakePresenter();

        OutfitImageGenerationInteractor interactor =
                new OutfitImageGenerationInteractor(dao, presenter);

        interactor.generateImages(List.of("Outfit 1"));

        assertFalse(presenter.failureCalled);
        assertTrue(presenter.successCalled);
        assertEquals(1, presenter.outputData.getBase64Images().size());
        assertEquals("BASE64IMAGE", presenter.outputData.getBase64Images().get(0));
    }


    // ===== Fake DAO =====
    static class FakeDAO implements OutfitImageGenerationDataAccessInterface {

        boolean returnEmpty = false;
        String fakeImage = "";

        @Override
        public List<String> generateImages(List<String> outfits) {
            if (returnEmpty) return List.of();
            return List.of(fakeImage);
        }
    }

    // ===== Fake Presenter =====
    static class FakePresenter implements OutfitImageGenerationOutputBoundary {

        boolean successCalled = false;
        boolean failureCalled = false;
        String message = "";
        OutfitImageGenerationOutputData outputData;

        @Override
        public void prepareSuccessView(OutfitImageGenerationOutputData data) {
            successCalled = true;
            outputData = data;
        }

        @Override
        public void prepareFailureView(String error) {
            failureCalled = true;
            message = error;
        }
    }
}
