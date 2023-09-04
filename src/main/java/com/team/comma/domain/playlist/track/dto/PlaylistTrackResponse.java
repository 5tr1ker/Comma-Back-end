package com.team.comma.domain.playlist.track.dto;

import com.team.comma.domain.track.artist.dto.TrackArtistResponse;
import com.team.comma.domain.track.track.domain.Track;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public final class PlaylistTrackResponse {

    private final long trackId;
    private final String trackTitle;
    private final Integer durationTimeMs;
    private final String albumImageUrl;
    private final boolean trackAlarmFlag;

    private final List<TrackArtistResponse> trackArtistList;

    private PlaylistTrackResponse(Track track, boolean trackAlarmFlag, List<TrackArtistResponse> trackArtistList) {
        this.trackId = track.getId();
        this.trackTitle = track.getTrackTitle();
        this.durationTimeMs = track.getDurationTimeMs();
        this.albumImageUrl = track.getAlbumImageUrl();
        this.trackAlarmFlag = trackAlarmFlag;
        this.trackArtistList = new ArrayList<>(trackArtistList);
    }

    public static PlaylistTrackResponse of(Track track, boolean trackAlarmFlag, List<TrackArtistResponse> trackArtistList) {
        return new PlaylistTrackResponse(track, trackAlarmFlag, trackArtistList);
    }

    public List<TrackArtistResponse> getTrackArtistList() {
        return Collections.unmodifiableList(trackArtistList);
    }

}
