package com.example.gy.musicgame.api;


import com.example.gy.musicgame.model.LoginVo;
import com.example.gy.musicgame.model.LrcModel;
import com.example.gy.musicgame.model.MusicModel;
import com.example.gy.musicgame.model.PlayMusicModel;
import com.example.gy.musicgame.model.RecipeDetailModel;
import com.example.gy.musicgame.model.RecipeSearchModel;
import com.example.gy.musicgame.model.RecipeTypeModel;
import com.example.gy.musicgame.model.RecommendMusicModel;
import com.example.gy.musicgame.model.RegisterVo;
import com.example.gy.musicgame.model.SearchMusicModel;
import com.example.gy.musicgame.model.SingerInfoModel;
import com.example.gy.musicgame.model.SplashModel;

import java.io.File;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * 数据接口
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/2 15:55
 */
public interface Api {

    /**
     * 获取歌曲列表
     * method=baidu.ting.billboard.billList&type=1&size=10&offset=0
     * 参数： type = 1-新歌榜,2-热歌榜,11-摇滚榜,12-爵士,16-流行,21-欧美金曲榜,22-经典老歌榜,23-情歌对唱榜,24-影视金曲榜,25-网络歌曲榜
     * size = 10 //返回条目数量
     * offset = 0 //获取偏移
     *
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<MusicModel> list(@QueryMap Map<String, Object> params);

    /**
     * 搜索
     * method=baidu.ting.search.catalogSug&query=海阔天空
     *
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<SearchMusicModel> search(@QueryMap Map<String, Object> params);


    /**
     * method=baidu.ting.song.play&songid=877578
     * 获取音乐播放地址
     *
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<PlayMusicModel> play(@QueryMap Map<String, Object> params);

    /**
     * method=baidu.ting.song.getRecommandSongList&song_id=877578&num=5
     * 参数： song_id = 877578
     * num = 5//返回条目数量
     *
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<RecommendMusicModel> recommend(@QueryMap Map<String, Object> params);

    /**
     * method=baidu.ting.song.lry&songid=877578
     *
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<LrcModel> lrc(@QueryMap Map<String, Object> params);

    /**
     * method=baidu.ting.artist.getInfo&tinguid=877578
     *
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<SingerInfoModel> getInfo(@QueryMap Map<String, Object> params);

    /**
     * 获取启动页
     * format=js&idx=0&n=1
     *
     * @param params 参数
     * @return
     */
    @GET("HPImageArchive.aspx")
    Observable<SplashModel> getSplash(@QueryMap Map<String, Object> params);

    /**
     * 用户登录
     *
     * @param loginVo 登录信息
     * @return
     */
    @POST("auth/login")
    Observable<Map> login(@Body LoginVo loginVo);

    /**
     * 用户注册
     *
     * @param registerVo 注册信息
     * @return
     */
    @POST("auth/register")
    Observable<Map> register(@Body RegisterVo registerVo);

    /**
     * 检测手机是否注册
     *
     * @param mobile 手机号
     * @return
     */
    @GET("auth/checkMobile")
    Observable<Map> checkMobile(@Query("mobile") String mobile);

    /**
     * 获取用户信息
     *
     * @param token token
     * @return
     */
    @GET("auth/info")
    Observable<Map> userInfo(@Header("back-manager-token") String token);

    /**
     * 检测版本更新
     *
     * @param token       token
     * @param packageName 包名
     * @param versionCode code
     * @return
     */
    @GET("apk/update")
    Observable<Map> apkUpdate(@Header("back-manager-token") String token, @Query("packageName") String packageName, @Query("versionCode") Integer versionCode);


    /**
     * 上传文件
     *
     * @param token token
     * @param file  文件
     * @return
     */
    @Multipart
    @POST("file/upload")
    Observable<Map> uploadFile(@Header("back-manager-token") String token, @Part MultipartBody.Part file);

    /**
     * 更换头像
     *
     * @param token token
     * @param image 头像
     * @return
     */
    @GET("auth/changeImage")
    Observable<Map> changeImage(@Header("back-manager-token") String token, @Query("image") String image);

    /**
     * 修改密码
     *
     * @param token       token
     * @param password    原密码
     * @param newPassword 新密码
     * @return
     */
    @GET("auth/changePassword")
    Observable<Map> changePassword(@Header("back-manager-token") String token, @Query("password") String password, @Query("newPassword") String newPassword);

    /**
     * 忘记密码
     *
     * @param mobile      手机号
     * @param newPassword 新密码
     * @return
     */
    @GET("auth/forgetPassword")
    Observable<Map> forgetPassword(@Query("mobile") String mobile, @Query("newPassword") String newPassword);


    /**
     * 菜谱相关接口
     *
     * @param key
     * @return
     */
    @GET("v1/cook/category/query")
    Observable<RecipeTypeModel> getRecipeType(@Query("key") String key);

    @GET("v1/cook/menu/search")
    Observable<RecipeSearchModel> getRecipeSearchMenu(@Query("key") String key, @Query("cid") String cid, @Query("name") String name, @Query("page")
            int page, @Query("size") int size);

    @GET("v1/cook/menu/query")
    Observable<RecipeDetailModel> getRecipeDetail(@Query("key") String key, @Query("id") String id);


    /**
     * 查找账户
     *
     * @param account 账户
     * @return
     */
    @GET("auth/queryByAccount")
    Observable<Map> queryByAccount(@Query("account") String account);

    /**
     * 通知列表
     *
     * @param token token
     * @return
     */
    @GET("message/list")
    Observable<Map> noticeList(@Header("back-manager-token") String token, @Query("currentPage") Integer currentPage, @Query("pageSize") Integer pageSize);
}
