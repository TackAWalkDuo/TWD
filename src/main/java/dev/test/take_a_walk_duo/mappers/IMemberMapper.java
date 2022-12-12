package dev.test.take_a_walk_duo.mappers;

import dev.test.take_a_walk_duo.entities.member.EmailAuthEntity;
import dev.test.take_a_walk_duo.entities.member.UserEntity;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IMemberMapper {
    int insertUser(UserEntity user);

    int updateUser(UserEntity user);

    int updateEmailAuth(EmailAuthEntity emailAuth);

    int insertEmailAuth(EmailAuthEntity emailAuth);

    UserEntity selectUserByEmail(@Param(value = "email") String email);

    UserEntity selectUserByNameContact(
            @Param(value = "name") String name,
            @Param(value = "contact") String contact);

    EmailAuthEntity selectEmailAuthByEmailCodeSalt(
            @Param(value = "email") String email,
            @Param(value = "code") String code,
            @Param(value = "salt") String salt);

    EmailAuthEntity selectEmailAuthByIndex(@Param(value = "index") int index);
}
