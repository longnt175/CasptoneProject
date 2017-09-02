package com.vpaliy.melophile.ui.search;

import com.vpaliy.domain.interactor.PlaylistSearch;
import com.vpaliy.domain.interactor.TrackSearch;
import com.vpaliy.domain.interactor.UserSearch;
import static com.vpaliy.melophile.ui.search.SearchContract.View;
import static dagger.internal.Preconditions.checkNotNull;
import com.vpaliy.domain.model.Playlist;
import com.vpaliy.domain.model.Track;
import com.vpaliy.domain.model.User;
import java.util.List;
import com.vpaliy.melophile.di.scope.ViewScope;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import javax.inject.Inject;

@ViewScope
public class SearchPresenter implements SearchContract.Presenter {

    private TrackSearch trackSearchUseCase;
    private PlaylistSearch playlistSearchUseCase;
    private UserSearch userSearchUseCase;
    private LoaderManager manager;
    private View view;

    @Inject
    public SearchPresenter(TrackSearch trackSearchUseCase,
                           PlaylistSearch playlistSearchUseCase,
                           UserSearch userSearchUseCase){
        this.trackSearchUseCase=trackSearchUseCase;
        this.playlistSearchUseCase=playlistSearchUseCase;
        this.userSearchUseCase=userSearchUseCase;
    }

    @Override
    public void attachView(@NonNull View view) {
        this.view=checkNotNull(view);
    }

    @Override
    public void query(String query) {
        trackSearchUseCase.execute(this::catchTracks,this::catchError,query);
        playlistSearchUseCase.execute(this::catchPlaylists,this::catchError,query);
        userSearchUseCase.execute(this::catchUsers,this::catchError,query);
    }

    private void catchTracks(List<Track> result){
        if(result!=null){
            view.showTracks(result);
        }
    }

    private void catchPlaylists(List<Playlist> playlists){
        if(playlists!=null){
            view.showPlaylists(playlists);
        }
    }

    private void catchError(Throwable ex){
        ex.printStackTrace();
        view.showErrorMessage();
    }

    private void catchUsers(List<User> result){
        if(result!=null){
            view.showUsers(result);
        }
    }

    @Override
    public void morePlaylists() {
        playlistSearchUseCase.more(this::appendPlaylists,this::catchError);
    }

    @Override
    public void moreTracks() {
        trackSearchUseCase.more(this::appendTracks,this::catchError);
    }

    @Override
    public void moreUsers() {
        userSearchUseCase.more(this::appendUsers,this::catchError);
    }

    private void appendTracks(List<Track> tracks){
        if(tracks==null||tracks.isEmpty()) return;
        view.appendTracks(tracks);
    }

    private void appendPlaylists(List<Playlist> playlists){
        if(playlists==null||playlists.isEmpty()) return;
        view.showPlaylists(playlists);
    }

    private void appendUsers(List<User> users){
        if(users==null||users.isEmpty()) return;
        view.showUsers(users);
    }

    @Override
    public void attachManager(@NonNull LoaderManager loaderManager) {
        this.manager=checkNotNull(loaderManager);
    }

    @Override
    public void stop() {
        trackSearchUseCase.dispose();
        playlistSearchUseCase.dispose();
        userSearchUseCase.dispose();
        view=null;
    }
}
