package cn.sealiu.calendouer.bean;

/**
 * Created by liuyang
 * on 2017/1/15.
 */

public class MovieBaseBean {
    private RatingBean rating;
    private String[] genres;
    private String title;
    private CelebrityBean[] casts;
    private int collect_count; //看过人数
    private String original_title; //原名
    private String subtype;
    private CelebrityBean[] directors;
    private String year;
    private ImagesBean images;
    private String alt; //条目页URL
    private String id; //条目id

    public RatingBean getRating() {
        return rating;
    }

    public void setRating(RatingBean rating) {
        this.rating = rating;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CelebrityBean[] getCasts() {
        return casts;
    }

    public void setCasts(CelebrityBean[] casts) {
        this.casts = casts;
    }

    public int getCollect_count() {
        return collect_count;
    }

    public void setCollect_count(int collect_count) {
        this.collect_count = collect_count;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public CelebrityBean[] getDirectors() {
        return directors;
    }

    public void setDirectors(CelebrityBean[] directors) {
        this.directors = directors;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public ImagesBean getImages() {
        return images;
    }

    public void setImages(ImagesBean images) {
        this.images = images;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
