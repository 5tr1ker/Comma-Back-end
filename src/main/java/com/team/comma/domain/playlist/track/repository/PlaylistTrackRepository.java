package com.team.comma.domain.playlist.track.repository;

import com.team.comma.domain.playlist.playlist.domain.Playlist;
import com.team.comma.domain.playlist.track.domain.PlaylistTrack;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long>,
    PlaylistTrackRepositoryCustom {

    List<PlaylistTrack> findAllByPlaylist(Playlist playlist);

    int deletePlaylistTrackByTrackIdAndPlaylistId(Long trackId, Long playlistId);

    Optional<PlaylistTrack> findByPlaylistIdAndTrackId(Long playlistId, Long trackId);

    @Query("SELECT MAX(pt.playSequence) FROM PlaylistTrack pt WHERE pt.playlist.id = :playlistId")
    Optional<Integer> findMaxPlaySequenceByPlaylistId(@Param("playlistId") Long playlistId);

}
