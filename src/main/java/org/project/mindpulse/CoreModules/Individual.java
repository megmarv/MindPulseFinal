package org.project.mindpulse.CoreModules;

public class Individual {

    private int userId;
    private String goodName;
    private String username;
    private String password;

    public Individual(int userId, String goodName, String username, String password) {
        this.userId = userId;
        this.goodName = goodName;
        this.username = username;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
