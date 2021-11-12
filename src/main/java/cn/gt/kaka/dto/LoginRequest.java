package cn.gt.kaka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;



public class LoginRequest {

    // This field will map with login request json's username key.
	@JsonProperty(value = "username", required = true)
    private String username;
	
    // This field will  map with login request json's password key.
	@JsonProperty(value = "password", required = true)
    private String password;
	
	public LoginRequest() {}

	public LoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

    /**
     * Return username from the loginRequest
     * @return username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Return Password from the loginRequest
     * @return
     */
    public String getPassword() {
        return password;
    }
    
    
}