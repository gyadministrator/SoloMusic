package com.android.customer.music.model;

import java.util.List;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/2 16:27
 */
public class SearchMusicModel {

    /**
     * song : [{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"2996099","songname":"海阔天空","resource_type":"0","songid":"87859296","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"Beyond","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"57081DD4AF1F085B90C994"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"160099","songname":"海阔天空","resource_type":"0","songid":"797275","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"周华健","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"49081D6698300858DE4913"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"18099","songname":"海阔天空","resource_type":"0","songid":"245905834","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"刘良骏","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"3107ea839aa095621afb6L"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"10099","songname":"海阔天空-现场版","resource_type":"0","songid":"19610286","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"任贤齐","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"77081D90898B0858DE4B1C"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"5099","songname":"海阔天空","resource_type":"0","songid":"118764007","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"Robynn & Kendy","info":"","resource_provider":"1","control":"1100000000","encrypted_songid":"430771431e7095621b712L"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"4099","songname":"海阔天空","resource_type":"0","encrypted_songid":"","has_mv":"0","resource_type_ext":"0","info":"","yyr_artist":"1","control":"0000000000","artistname":"幼稚园杀手","is_skip_yyr":"0","resource_provider":"1","songid":"73896409","yyr_song_id":"77409"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"1099","songname":"海阔天空","resource_type":"0","songid":"613921282","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"张穆庭","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":""},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"1099","songname":"海阔天空-电吉他版","resource_type":"0","encrypted_songid":"","has_mv":"0","resource_type_ext":"0","info":"","yyr_artist":"1","control":"0000000000","artistname":"MC雪殇","is_skip_yyr":"0","resource_provider":"1","songid":"73984962","yyr_song_id":"165962"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"1099","songname":"海阔天空","resource_type":"0","encrypted_songid":"","has_mv":"0","resource_type_ext":"0","info":"","yyr_artist":"1","control":"0000000000","artistname":"许苏峰（Bragg.Xu）","is_skip_yyr":"0","resource_provider":"1","songid":"73996756","yyr_song_id":"177756"},{"bitrate_fee":"{\"0\":\"0|0\",\"1\":\"0|0\"}","weight":"1099","songname":"海阔天空","resource_type":"0","songid":"565884031","has_mv":"0","yyr_artist":"0","resource_type_ext":"0","artistname":"三师兄","info":"","resource_provider":"1","control":"0000000000","encrypted_songid":"180821BAB472085D021528"}]
     * album : [{"albumname":"海阔天空","weight":"60","artistname":"Beyond","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/88582702/88582702.jpg@s_2,w_40,h_40","albumid":"197864"},{"albumname":"海阔天空","weight":"0","artistname":"幼稚园杀手","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/9a49fa1fea644c58496202e16f2e4eca/664662161/664662161.jpg@s_2,w_40,h_40","albumid":"611651783"},{"albumname":"海阔天空","weight":"0","artistname":"许苏峰（Bragg.Xu）","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/default_album.jpg@s_2,w_40,h_40","albumid":"611657940"},{"albumname":"海阔天空-电吉他版","weight":"0","artistname":"MC雪殇","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/default_album.jpg@s_2,w_40,h_40","albumid":"611657010"},{"albumname":"天路","weight":"0","artistname":"海阔天空","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/25278c18140fe1b99ad54bf752f13eb5/261963785/261963785.jpg@s_2,w_40,h_40","albumid":"14469195"},{"albumname":"海阔天空","weight":"0","artistname":"信乐团","resource_type_ext":"0","artistpic":"http://qukufile2.qianqian.com/data2/pic/88406848/88406848.jpg@s_2,w_40,h_40","albumid":"72973"}]
     * order : artist,song,album
     * error_code : 22000
     * artist : [{"yyr_artist":"0","artistname":"海阔天空","artistid":"2345733","artistpic":"http://qukufile2.qianqian.com/data2/pic/5F1741B07058A32998B93B4DE698450B/252837196/252837196.jpg@s_2,w_48,h_48","weight":"0"}]
     */

    private String order;
    private int error_code;
    private List<SongBean> song;
    private List<AlbumBean> album;
    private List<ArtistBean> artist;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<SongBean> getSong() {
        return song;
    }

    public void setSong(List<SongBean> song) {
        this.song = song;
    }

    public List<AlbumBean> getAlbum() {
        return album;
    }

    public void setAlbum(List<AlbumBean> album) {
        this.album = album;
    }

    public List<ArtistBean> getArtist() {
        return artist;
    }

    public void setArtist(List<ArtistBean> artist) {
        this.artist = artist;
    }

    public static class SongBean {
        /**
         * bitrate_fee : {"0":"0|0","1":"0|0"}
         * weight : 2996099
         * songname : 海阔天空
         * resource_type : 0
         * songid : 87859296
         * has_mv : 0
         * yyr_artist : 0
         * resource_type_ext : 0
         * artistname : Beyond
         * info :
         * resource_provider : 1
         * control : 0000000000
         * encrypted_songid : 57081DD4AF1F085B90C994
         * is_skip_yyr : 0
         * yyr_song_id : 77409
         */

        private String bitrate_fee;
        private String weight;
        private String songname;
        private String resource_type;
        private String songid;
        private String has_mv;
        private String yyr_artist;
        private String resource_type_ext;
        private String artistname;
        private String info;
        private String resource_provider;
        private String control;
        private String encrypted_songid;
        private String is_skip_yyr;
        private String yyr_song_id;

        public String getBitrate_fee() {
            return bitrate_fee;
        }

        public void setBitrate_fee(String bitrate_fee) {
            this.bitrate_fee = bitrate_fee;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getSongname() {
            return songname;
        }

        public void setSongname(String songname) {
            this.songname = songname;
        }

        public String getResource_type() {
            return resource_type;
        }

        public void setResource_type(String resource_type) {
            this.resource_type = resource_type;
        }

        public String getSongid() {
            return songid;
        }

        public void setSongid(String songid) {
            this.songid = songid;
        }

        public String getHas_mv() {
            return has_mv;
        }

        public void setHas_mv(String has_mv) {
            this.has_mv = has_mv;
        }

        public String getYyr_artist() {
            return yyr_artist;
        }

        public void setYyr_artist(String yyr_artist) {
            this.yyr_artist = yyr_artist;
        }

        public String getResource_type_ext() {
            return resource_type_ext;
        }

        public void setResource_type_ext(String resource_type_ext) {
            this.resource_type_ext = resource_type_ext;
        }

        public String getArtistname() {
            return artistname;
        }

        public void setArtistname(String artistname) {
            this.artistname = artistname;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getResource_provider() {
            return resource_provider;
        }

        public void setResource_provider(String resource_provider) {
            this.resource_provider = resource_provider;
        }

        public String getControl() {
            return control;
        }

        public void setControl(String control) {
            this.control = control;
        }

        public String getEncrypted_songid() {
            return encrypted_songid;
        }

        public void setEncrypted_songid(String encrypted_songid) {
            this.encrypted_songid = encrypted_songid;
        }

        public String getIs_skip_yyr() {
            return is_skip_yyr;
        }

        public void setIs_skip_yyr(String is_skip_yyr) {
            this.is_skip_yyr = is_skip_yyr;
        }

        public String getYyr_song_id() {
            return yyr_song_id;
        }

        public void setYyr_song_id(String yyr_song_id) {
            this.yyr_song_id = yyr_song_id;
        }
    }

    public static class AlbumBean {
        /**
         * albumname : 海阔天空
         * weight : 60
         * artistname : Beyond
         * resource_type_ext : 0
         * artistpic : http://qukufile2.qianqian.com/data2/pic/88582702/88582702.jpg@s_2,w_40,h_40
         * albumid : 197864
         */

        private String albumname;
        private String weight;
        private String artistname;
        private String resource_type_ext;
        private String artistpic;
        private String albumid;

        public String getAlbumname() {
            return albumname;
        }

        public void setAlbumname(String albumname) {
            this.albumname = albumname;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getArtistname() {
            return artistname;
        }

        public void setArtistname(String artistname) {
            this.artistname = artistname;
        }

        public String getResource_type_ext() {
            return resource_type_ext;
        }

        public void setResource_type_ext(String resource_type_ext) {
            this.resource_type_ext = resource_type_ext;
        }

        public String getArtistpic() {
            return artistpic;
        }

        public void setArtistpic(String artistpic) {
            this.artistpic = artistpic;
        }

        public String getAlbumid() {
            return albumid;
        }

        public void setAlbumid(String albumid) {
            this.albumid = albumid;
        }
    }

    public static class ArtistBean {
        /**
         * yyr_artist : 0
         * artistname : 海阔天空
         * artistid : 2345733
         * artistpic : http://qukufile2.qianqian.com/data2/pic/5F1741B07058A32998B93B4DE698450B/252837196/252837196.jpg@s_2,w_48,h_48
         * weight : 0
         */

        private String yyr_artist;
        private String artistname;
        private String artistid;
        private String artistpic;
        private String weight;

        public String getYyr_artist() {
            return yyr_artist;
        }

        public void setYyr_artist(String yyr_artist) {
            this.yyr_artist = yyr_artist;
        }

        public String getArtistname() {
            return artistname;
        }

        public void setArtistname(String artistname) {
            this.artistname = artistname;
        }

        public String getArtistid() {
            return artistid;
        }

        public void setArtistid(String artistid) {
            this.artistid = artistid;
        }

        public String getArtistpic() {
            return artistpic;
        }

        public void setArtistpic(String artistpic) {
            this.artistpic = artistpic;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }
    }
}
