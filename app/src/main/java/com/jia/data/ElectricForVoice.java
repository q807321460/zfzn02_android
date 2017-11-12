package com.jia.data;

public class ElectricForVoice {
	private String electricCode;
	private String electricName;
	private String roomName;
	private String orderInfo;
	public ElectricForVoice(){}
	public String getElectricCode() {
		return electricCode;
	}
	public void setElectricCode(String electricCode) {
		this.electricCode = electricCode;
	}
	public String getElectricName() {
		return electricName;
	}
	public void setElectricName(String electricName) {
		this.electricName = electricName;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getOrderInfo() {
		return orderInfo;
	}
	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}
	@Override
	public String toString() {
		return "ElectricForVoice [electricCode=" + electricCode + ", electricName=" + electricName + ", roomName="
				+ roomName + ", orderInfo=" + orderInfo + "]";
	};
	
	
}
