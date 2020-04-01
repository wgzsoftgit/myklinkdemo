package com.Flinkkafka;

public class UserPvEntity {
private Long time;
private String  userId;
private Long pvcount;
public UserPvEntity(Long valueOf, String f1, Long f2) {
	// TODO Auto-generated constructor stub
	this.time=valueOf;
	this.userId=f1;
	this.pvcount=f2;
}
public Long getTime() {
	return time;
}
public String getUserId() {
	return userId;
}
public Long getPvcount() {
	return pvcount;
}
public void setTime(Long time) {
	this.time = time;
}
public void setUserId(String userId) {
	this.userId = userId;
}
public void setPvcount(Long pvcount) {
	this.pvcount = pvcount;
}

}
