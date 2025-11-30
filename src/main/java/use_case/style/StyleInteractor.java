package use_case.style;

import data_access.user_storage.PendingUserStorage;
import entity.User;
import data_access.user_storage.UserRepository;


// Interactor for saving user style preferences.

public class StyleInteractor implements StyleInputBoundary {
    private final UserRepository repository;
    private final StyleOutputBoundary presenter;

    public StyleInteractor(UserRepository repository, StyleOutputBoundary presenter) {
        this.repository = repository;
        this.presenter = presenter;
    }

    @Override
    public void saveStylePreferences(StyleInputData input) {
        // retrieve user from pending storage
        User user = PendingUserStorage.getInstance().getPendingUser(input.getUsername());

        // Update style preferences
        user.setStyle(input.getStylePreferences());

        // Save user to database
        repository.save(user);

        // Remove from pending storage
        PendingUserStorage.getInstance().removePendingUser(input.getUsername());

        presenter.present(new StyleOutputData(true, "Style preferences saved successfully!", input.getUsername()));
    }
}
