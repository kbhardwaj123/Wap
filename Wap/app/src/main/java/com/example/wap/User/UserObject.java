package com.example.wap.User;

public class UserObject {

    private String name,phone,Uid;

    public UserObject(String Uid,String name,String phone) {
        this.Uid = Uid;
        this.name = name;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
    public String getName() {
        return name;
    }
    public String getUid() {  return Uid; }

    public void setName(String name) {
        this.name = name;
    }
}
