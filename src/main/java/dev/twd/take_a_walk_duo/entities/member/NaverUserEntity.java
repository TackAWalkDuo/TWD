package dev.twd.take_a_walk_duo.entities.member;

import java.util.Date;
import java.util.Objects;

public class NaverUserEntity {
    private String id;
    private String email;
    private String nickname;
    private Date registeredOn;
    private boolean isUser;

    public String getId() {
        return id;
    }

    public NaverUserEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public NaverUserEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public NaverUserEntity setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public Date getRegisteredOn() {
        return registeredOn;
    }

    public NaverUserEntity setRegisteredOn(Date registeredOn) {
        this.registeredOn = registeredOn;
        return this;
    }

    public boolean isUser() {
        return isUser;
    }

    public NaverUserEntity setUser(boolean user) {
        isUser = user;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NaverUserEntity that = (NaverUserEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
