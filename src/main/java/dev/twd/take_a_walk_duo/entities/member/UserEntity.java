package dev.twd.take_a_walk_duo.entities.member;

import java.util.Date;
import java.util.Objects;

public class UserEntity {
    private String email;
    private String password;
    private String nickname;
    private String name;
    private String contact;
    private int birthYear;
    private int birthMonth;
    private int birthDay;
    private String gender;
    private String haveDog;
    private String species;
    private String addressPostal;
    private String addressPrimary;
    private String addressSecondary;
    private Date registeredOn;
    private Boolean isAdmin;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public UserEntity setBirthYear(int birthYear) {
        this.birthYear = birthYear;
        return this;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public UserEntity setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
        return this;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public UserEntity setBirthDay(int birthDay) {
        this.birthDay = birthDay;
        return this;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHaveDog() {
        return haveDog;
    }

    public void setHaveDog(String haveDog) {
        this.haveDog = haveDog;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }


    public String getAddressPostal() {
        return addressPostal;
    }

    public void setAddressPostal(String addressPostal) {
        this.addressPostal = addressPostal;
    }

    public String getAddressPrimary() {
        return addressPrimary;
    }

    public void setAddressPrimary(String addressPrimary) {
        this.addressPrimary = addressPrimary;
    }

    public String getAddressSecondary() {
        return addressSecondary;
    }

    public void setAddressSecondary(String addressSecondary) {
        this.addressSecondary = addressSecondary;
    }

    public Date getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(Date registeredOn) {
        this.registeredOn = registeredOn;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

}
