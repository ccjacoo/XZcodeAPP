package edu.heuet.android.logindemo.service.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 因为业务需要，有些字段并不能在同一张表里
 * model层就是整合业务需要的所有字段
 */
public class UserModel {
    private Integer id;
    private String realname;
    @NotNull(message = "学号不能为空")
    private String stunum;
    private String classes;
    @NotNull(message = "手机号不能为空")
    private String telephone;
    private String qq;
    /* 整合加密后的密码字段 */
    @NotNull(message = "密码不能为空")
    private String encryptPassword;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getStunum() {
        return stunum;
    }

    public void setStunum(String stunum) {
        this.stunum = stunum;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String tqq) {
        this.qq = tqq;
    }

    public String getEncryptPassword() {
        return encryptPassword;
    }

    public void setEncryptPassword(String encryptPassword) {
        this.encryptPassword = encryptPassword;
    }
}
