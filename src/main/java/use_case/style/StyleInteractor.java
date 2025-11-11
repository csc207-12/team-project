package use_case.style;

import data_access.SupabaseUserRepository;
import entity.User;
import use_case.UserRepository;

/**
 * Interactor for saving user style preferences.
 * Saves to in-memory repository first, then uploads complete profile to Supabase.
 */
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
        // Retrieve the user from in-memory repository
        User user = repository.findByUsername(input.getUsername());

        if (user == null) {
            presenter.present(new StyleOutputData(false, "User not found", input.getUsername()));
            return;
        }

        // Update style preferences
        user.setStyle(input.getStylePreferences());

        // This is the ONLY time data goes to Supabase - after all fields are filled
        supabaseRepository.save(user);

        presenter.present(new StyleOutputData(true, "Style preferences saved successfully!", input.getUsername()));
    }
}
