package interface_adapter.login;

// State info representiong login process
public class LoginState {
    private String username = "";
    private String errorMessage = "";

    public LoginState() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}