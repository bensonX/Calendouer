package cn.sealiu.calendouer.bean;

/**
 * Created by liuyang
 * on 2017/4/20.
 */

public class CelebrityBean extends CelebrityBaseBean {
    private String mobile_url;
    private String[] aka_en;
    private String[] aka;
    private String gender;
    private String name_en;
    private String born_place;
    private WorksBean[] works;

    public String getMobile_url() {
        return mobile_url;
    }

    public void setMobile_url(String mobile_url) {
        this.mobile_url = mobile_url;
    }

    public String[] getAka_en() {
        return aka_en;
    }

    public void setAka_en(String[] aka_en) {
        this.aka_en = aka_en;
    }

    public String[] getAka() {
        return aka;
    }

    public void setAka(String[] aka) {
        this.aka = aka;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getBorn_place() {
        return born_place;
    }

    public void setBorn_place(String born_place) {
        this.born_place = born_place;
    }

    public WorksBean[] getWorks() {
        return works;
    }

    public void setWorks(WorksBean[] works) {
        this.works = works;
    }
}
