package cn.sealiu.calendouer.bean;

/**
 * Created by liuyang
 * on 2017/4/20.
 */

public class WorksBean {
    private String[] roles;
    private MovieBaseBean subject;

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public MovieBaseBean getSubject() {
        return subject;
    }

    public void setSubject(MovieBaseBean subject) {
        this.subject = subject;
    }
}
