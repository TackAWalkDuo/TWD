package dev.twd.take_a_walk_duo.entities.member;

import java.util.Date;
import java.util.Objects;

public class KakaoUserEntity {
    private String id;
    private String email;
    private String nickname;
    private Date registeredOn;
    private boolean isUser;

    public String getEmail() {
        return email;
    }

    public KakaoUserEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getId() {
        return id;
    }

    public KakaoUserEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public KakaoUserEntity setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public Date getRegisteredOn() {
        return registeredOn;
    }

    public KakaoUserEntity setRegisteredOn(Date registeredOn) {
        this.registeredOn = registeredOn;
        return this;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KakaoUserEntity that = (KakaoUserEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
