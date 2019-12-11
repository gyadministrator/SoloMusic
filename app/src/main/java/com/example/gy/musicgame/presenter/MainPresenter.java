package com.example.gy.musicgame.presenter;

import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.RecommendMusicModel;
import com.example.gy.musicgame.view.MainView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/2 18:33
 */
public class MainPresenter<V extends MainView> extends BasePresenter {
    private V view;

    public MainPresenter(V view) {
        this.view = view;
    }

    public List<String> getTitles() {
        return mTitle;
    }

    public List<Integer> getTypes() {
        return mType;
    }

    public void getRecommendList() {
        mParams.put("method", Constants.METHOD_RECOMMEND);
        mParams.put("song_id", "87757");
        mParams.put("num", 6);
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.BASE_URL);
        Observable<RecommendMusicModel> observable = api.recommend(mParams);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RecommendMusicModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RecommendMusicModel recommendMusicModel) {
                        view.success(recommendMusicModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.fail(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
