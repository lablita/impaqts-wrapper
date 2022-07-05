package it.drwolf.impaqts.wrapper.dto;

public class TokenClassDTO {
	private String token;
	private String clazz;

	public String getClazz() {
		return this.clazz;
	}

	public String getToken() {
		return this.token;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
