package com.homeease.dto;



import java.util.List;

public class ServiceProviderRegistrationDto {
    private String name;
    private String email;
    private String password;
    private List<Long> selectedServiceIds;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<Long> getSelectedServiceIds() {
		return selectedServiceIds;
	}
	public void setSelectedServiceIds(List<Long> selectedServiceIds) {
		this.selectedServiceIds = selectedServiceIds;
	}

    
}
