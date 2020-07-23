package com.aircall.pager.domain;

public class Target {

    private final String id;
    private final Type type;
    private final String email;
    private final String phoneNumber;

    public enum Type {EMAIL,SMS}

    public static Target newSMSTarget(String id, String phoneNumber){
        return new Target(id, Type.SMS, null, phoneNumber);
    }

    public static Target newMailTarget(String id, String email){
        return new Target(id, Type.EMAIL, email, null);
    }

    public Target(String id, Type type, String email, String phoneNumber) {
        this.id = id;
        this.type = type;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Type getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return "Target{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
