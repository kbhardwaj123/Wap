package com.example.wap.User;

import java.io.Serializable;

public class UserObject implements Serializable {

    private String name,phone,Uid,notifKey;

    public UserObject(String Uid,String name,String phone) {
        this.Uid = Uid;
        this.name = name;
        this.phone = phone;
    }

    public UserObject(String Uid) {
        this.Uid = Uid;
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

    public String getNotifKey() {
        return notifKey;
    }

    public void setNotifKey(String notifKey) {
        this.notifKey = notifKey;
    }
}
