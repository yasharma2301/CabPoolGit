package com.example.megacabpool;

public class User {
    public String name,email,contact,nid,impid;

    public User(){

    }

    public User(String name, String email, String contact,String nid,String impid) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.nid = nid;
        this.impid = impid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNid() {
        return nid;
    }

    public String getImpid() {
        return impid;
    }

    public String getContact() {
        return contact;
    }
}
