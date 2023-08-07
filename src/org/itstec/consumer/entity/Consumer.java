package org.itstec.consumer.entity;

import java.io.Serializable;
import java.util.Date;

public class Consumer implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String username;
	private String password;
	private String mobilephone;
	private String email;
	private String city;
	private String deliveryAddress;
	private long userRights;
	private Date registerTime;
	private String status;//N表示正常，可查询 D表示注销，不可查询

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public long getUserRights() {
		return userRights;
	}

	public void setUserRights(long userRights) {
		this.userRights = userRights;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

}
