package com.team.comma.domain.user.following.repository;

import com.team.comma.domain.user.detail.repository.UserDetailRepository;
import com.team.comma.domain.user.following.domain.Following;
import com.team.comma.domain.user.following.dto.FollowingResponse;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.domain.user.user.domain.User;
import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.user.repository.UserRepository;
import com.team.comma.global.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FollowingRepositoryTest {

    @Autowired
    private FollowingRepository followingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Test
    @DisplayName("나를 팔로우한 사용자 탐색")
    public void searchFollowedMeUser() {
        // given
        User toUser = userRepository.save(User.builder().email("toUser1").build());
        User fromUser = userRepository.save(User.builder().email("fromEmail").build());

        Following follow = Following.builder()
                .userTo(toUser)
                .userFrom(fromUser)
                .blockFlag(false)
                .build();

        followingRepository.save(follow);

        // when
        User result = followingRepository.getFollowedMeUserByEmail(toUser.getId(), fromUser.getEmail()).orElse(null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(toUser.getId());
    }

    @Test
    @DisplayName("나를 팔로우한 사용자 탐색 없음")
    public void searchFollowedMeUser_none() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .userFrom(fromUser)
                .blockFlag(false)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);


        // when
        User result = followingRepository.getFollowedMeUserByEmail(1L , "fromEmails").orElse(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("나를 팔로우한 사람 차단")
    public void blockFollowedUser() {
        // given
        User toUser = userRepository.save(User.builder().email("toUser1").build());
        User fromUser = userRepository.save(User.builder().email("fromEmail").build());

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(false)
                .userFrom(fromUser)
                .build();

        Following following = followingRepository.save(follow);

        // when
        followingRepository.blockFollowedUser(following.getId());

        // then
        User result = followingRepository.getBlockedUser(toUser.getId(), fromUser.getEmail()).orElse(null);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("나를 팔로우한 사람 차단 해제")
    public void unblockFollowedUser() {
        // given
        User toUser = userRepository.save(User.builder().email("toUser1").build());
        User fromUser = userRepository.save(User.builder().email("fromEmail").build());

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(true)
                .userFrom(fromUser)
                .build();
        Following following = followingRepository.save(follow);

        // when
        followingRepository.unblockFollowedUser(following.getId());

        // then
        User result = followingRepository.getFollowedMeUserByEmail(toUser.getId(), fromUser.getEmail()).orElse(null);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("삭제된 사용자 확인")
    public void isBlockedUser() {
        // given
        User toUser = userRepository.save(User.builder().email("toUser1").build());
        User fromUser = userRepository.save(User.builder().email("fromEmail").build());

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(true)
                .userFrom(fromUser)
                .build();

        followingRepository.save(follow);

        // when
        User result = followingRepository.getBlockedUser(toUser.getId(), fromUser.getEmail()).orElse(null);

        // then
        assertThat(result.getId()).isEqualTo(toUser.getId());
    }

    @Test
    public void 팔로우_저장 () {
        // given
        User toUser = User.builder()
                .email("toUser1")
                .build();
        User fromUser = User.builder()
                .email("fromUser")
                .build();

        Following follow = Following.createFollowingToFrom(toUser,fromUser);

        // when
        Following result = followingRepository.save(follow);

        // then
        assertThat(result.getUserTo()).isEqualTo(follow.getUserTo());
    }

    @Test
    public void 팔로잉_리스트_조회() {
        // given
        User user = User.createUser("userEmail");
        User targetUser = User.createUser("targetUserEmail");
        userRepository.save(user);
        userRepository.save(targetUser);

        UserDetail userDetail1 = UserDetail.buildUserDetail(user);
        UserDetail userDetail2 = UserDetail.buildUserDetail(targetUser);
        userDetailRepository.save(userDetail1);
        userDetailRepository.save(userDetail2);

        Following follow1 = Following.createFollowingToFrom(user, targetUser);
        followingRepository.save(follow1);
        Following follow2 = Following.createFollowingToFrom(targetUser, user);
        followingRepository.save(follow2);

        // when
        List<FollowingResponse> result = followingRepository.getFollowingToUserListByFromUser(user);

        // then
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void 팔로워_리스트_조회() {
        // given
        User user = User.createUser("userEmail");
        User targetUser = User.createUser("targetUserEmail");
        userRepository.save(user);
        userRepository.save(targetUser);

        UserDetail userDetail1 = UserDetail.buildUserDetail(user);
        UserDetail userDetail2 = UserDetail.buildUserDetail(targetUser);
        userDetailRepository.save(userDetail1);
        userDetailRepository.save(userDetail2);

        Following follow1 = Following.createFollowingToFrom(user, targetUser);
        followingRepository.save(follow1);
        Following follow2 = Following.createFollowingToFrom(targetUser, user);
        followingRepository.save(follow2);

        // when
        List<FollowingResponse> result = followingRepository.getFollowingFromUserListByToUser(user);

        // then
        assertThat(result.size()).isEqualTo(1);

    }

}
