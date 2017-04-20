package cn.sealiu.calendouer.bean;

/**
 * Created by liuyang
 * on 2017/1/15.
 */

public class CelebrityBaseBean {
    private String alt;
    private ImagesBean avatars;
    private String name;
    private String id;

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public ImagesBean getAvatars() {
        return avatars;
    }

    public void setAvatars(ImagesBean avatars) {
        this.avatars = avatars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
