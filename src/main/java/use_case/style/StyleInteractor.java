package use_case.style;

import data_access.SupabaseUserRepository;
import entity.User;
import use_case.UserRepository;


// Interactor for saving user style preferences.

public class StyleInteractor implements StyleInputBoundary {
    private final UserRepository repository;
    private final StyleOutputBoundary presenter;
    private final SupabaseUserRepository supabaseRepository;

    public StyleInteractor(UserRepository repository, StyleOutputBoundary presenter) {
        this.repository = repository;
        this.presenter = presenter;
        this.supabaseRepository = new SupabaseUserRepository();
    }

    @Override
    public void saveStylePreferences(StyleInputData input) {
        // Retrieve user from in-memory repo
        User user = repository.findByUsername(input.getUsername());

        if (user == null) {
            presenter.present(new StyleOutputData(false, "User not found", input.getUsername()));
            return;
        }

        // Update style preferences
        user.setStyle(input.getStylePreferences());

        // Save user to database
        supabaseRepository.save(user);

        presenter.present(new StyleOutputData(true, "Style preferences saved successfully!", input.getUsername()));
    }
}
