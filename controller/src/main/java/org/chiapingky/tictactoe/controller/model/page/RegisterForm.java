package org.chiapingky.tictactoe.controller.model.page;

public class RegisterForm {
    private String username;
    private String password;

    public RegisterForm() {
    }

    public RegisterForm(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
