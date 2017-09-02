package com.vpaliy.melophile.ui.personal;

import com.vpaliy.domain.model.Playlist;
import com.vpaliy.domain.model.Track;
import com.vpaliy.domain.model.User;
import com.vpaliy.melophile.App;
import com.vpaliy.melophile.R;
import com.vpaliy.melophile.di.component.DaggerViewComponent;
import com.vpaliy.melophile.di.module.PresenterModule;
import com.vpaliy.melophile.ui.base.BaseFragment;
import com.vpaliy.melophile.ui.base.adapters.LimitedPlaylistsAdapter;
import com.vpaliy.melophile.ui.base.adapters.LimitedTracksAdapter;
import com.vpaliy.melophile.ui.base.adapters.PlaylistsAdapter;
import com.vpaliy.melophile.ui.base.adapters.TracksAdapter;
import java.util.List;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import static com.vpaliy.melophile.ui.personal.PersonalContract.Presenter;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.OnClick;

public class PersonalFragment extends BaseFragment
        implements PersonalContract.View{

    private Presenter presenter;

    @BindView(R.id.personal_media)
    protected RecyclerView personalMedia;

    private PersonalAdapter adapter;

    @Override
    public void initializeDependencies() {
        DaggerViewComponent.builder()
                .applicationComponent(App.appInstance().appComponent())
                .presenterModule(new PresenterModule())
                .build().inject(this);
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_personal;
    }

    @Inject
    @Override
    public void attachPresenter(@NonNull Presenter presenter) {
        this.presenter=presenter;
        this.presenter.attachView(this);
        this.presenter.attachManager(getLoaderManager());
    }

    @Override
    public void onStart() {
        super.onStart();
        if(presenter!=null){
            if(adapter.getItemCount()==1){
                presenter.start();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(presenter!=null){
            presenter.stop();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(view!=null){
            adapter=new PersonalAdapter(getContext(),rxBus);
            personalMedia.setAdapter(adapter);
        }
    }

    @Override
    public void showPlaylistHistory(@NonNull List<Playlist> playlists) {
        LimitedPlaylistsAdapter playlistsAdapter=new LimitedPlaylistsAdapter(getContext(),rxBus);
        playlistsAdapter.setData(playlists);
        adapter.addItem(PersonalAdapter.CategoryWrapper.wrap(getString(R.string.playlist_history_label), playlistsAdapter));
    }

    @Override
    public void showTrackHistory(@NonNull List<Track> tracks) {
        LimitedTracksAdapter tracksAdapter = new LimitedTracksAdapter(getContext(), rxBus);
        tracksAdapter.setData(tracks);
        adapter.addItem(PersonalAdapter.CategoryWrapper.wrap(getString(R.string.track_history_label), tracksAdapter));
    }

    @Override
    public void showMyself(User user) {
        adapter.setUser(user);
    }
}
