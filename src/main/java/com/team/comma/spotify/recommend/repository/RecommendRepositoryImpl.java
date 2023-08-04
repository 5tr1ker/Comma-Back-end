package com.team.comma.spotify.recommend.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.spotify.recommend.domain.Recommend;
import com.team.comma.spotify.recommend.dto.RecommendResponse;
import com.team.comma.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
import static com.team.comma.spotify.playlist.domain.QPlaylistTrack.playlistTrack;
import static com.team.comma.spotify.recommend.domain.QRecommend.recommend;

@RequiredArgsConstructor
public class RecommendRepositoryImpl implements RecommendRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RecommendResponse> getRecommendsByToUser(User requestUser) {
        return queryFactory.select(
                        Projections.constructor(
                                RecommendResponse.class,
                                recommend.id,
                                recommend.comment,
                                recommend.fromUser.userDetail.nickname,
                                recommend.fromUser.userDetail.profileImageUrl,
                                recommend.playlist.id,
                                recommend.playlist.playlistTitle,
                                select(playlistTrack.track.albumImageUrl)
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(recommend.playlist))
                                        .orderBy(playlistTrack.playSequence.asc())
                                        .limit(1),
                                select(playlistTrack.count())
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(recommend.playlist)),
                                recommend.playCount
                        ))
                .from(recommend)
                .where(recommend.toUser.eq(requestUser))
                .orderBy(recommend.id.desc())
                .fetch();
    }

    @Override
    public List<RecommendResponse> getRecommendsByFromUser(User requestUser) {
        return queryFactory.select(
                        Projections.constructor(
                                RecommendResponse.class,
                                recommend.id,
                                recommend.comment,
                                recommend.toUser.userDetail.nickname,
                                recommend.toUser.userDetail.profileImageUrl,
                                recommend.playlist.id,
                                recommend.playlist.playlistTitle,
                                select(playlistTrack.track.albumImageUrl)
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(recommend.playlist))
                                        .orderBy(playlistTrack.playSequence.asc())
                                        .limit(1),
                                select(playlistTrack.count())
                                        .from(playlistTrack)
                                        .where(playlistTrack.playlist.eq(recommend.playlist)),
                                recommend.playCount
                        ))
                .from(recommend)
                .where(recommend.fromUser.eq(requestUser))
                .orderBy(recommend.id.desc())
                .fetch();
    }

    @Override
    public long getRecommendCountByToUserAndPlaylist(Recommend reco) {
        return queryFactory
                .select(recommend.count())
                .from(recommend)
                .where(recommend.toUser.eq(reco.getToUser())
                        .and(recommend.playlist.eq(reco.getPlaylist())))
                .fetchFirst();
    }

    @Override
    public long increasePlayCount(long recommendId) {
        return queryFactory.update(recommend)
                .set(recommend.playCount, recommend.playCount.add(1L))
                .where(recommend.id.eq(recommendId))
                .execute();
    }
}
