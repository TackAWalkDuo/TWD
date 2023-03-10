package dev.twd.take_a_walk_duo.mappers;

import dev.twd.take_a_walk_duo.entities.member.EmailAuthEntity;
import dev.twd.take_a_walk_duo.entities.member.KakaoUserEntity;
import dev.twd.take_a_walk_duo.entities.member.NaverUserEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IMemberMapper {

    int insertUser(UserEntity user);

    int deleteUser(UserEntity user);

    int deleteUserByEmail(@Param(value = "email") String email);
    int deleteNaverUserByEmail(@Param(value = "email") String email);

    int deleteKakaoUserByEmail(@Param(value = "email") String email);

    int insertNaverUser(NaverUserEntity naverUser);

    int insertKakaoUser(KakaoUserEntity kakaoUser);

    int updateUser(UserEntity user);
    int updateNaverUser(NaverUserEntity naverUser);

    int updateKakaoUser(KakaoUserEntity kakaoUser);

    int updateEmailAuth(EmailAuthEntity emailAuth);

    int insertEmailAuth(EmailAuthEntity emailAuth);

    NaverUserEntity selectNaverUserById(@Param(value = "id") String id);

    KakaoUserEntity selectKakaoUserById(@Param(value = "id") String id);

    NaverUserEntity selectNaverUserByEmail(@Param(value = "email") String email);

    KakaoUserEntity selectKakaoUserByEmail(@Param(value = "email") String email);

    UserEntity selectUserByEmail(@Param(value = "email") String email);

    UserEntity selectUserByNameContact(
            @Param(value = "name") String name,
            @Param(value = "contact") String contact);

    EmailAuthEntity selectEmailAuthByEmailCodeSalt(
            @Param(value = "email") String email,
            @Param(value = "code") String code,
            @Param(value = "salt") String salt);

    EmailAuthEntity selectEmailAuthByIndex(@Param(value = "index") int index);

    UserEntity selectUserByNickname(@Param(value = "nickname") String nickname);
}
