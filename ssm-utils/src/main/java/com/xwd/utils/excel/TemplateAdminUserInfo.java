package com.xwd.utils.excel;

/**
 * Created by ljc on 2015/8/28.
 */
public class TemplateAdminUserInfo {
    @ExcelField(title = "身份证",align = 1,sort = 0)
    private String mobile;
    @ExcelField(title = "居住地址",align = 1,sort = 1)
    private String realname;
    @ExcelField(title = "家庭电话",align = 1,sort = 2)
    private String organization_name;
    @ExcelField(title = "单位名称",align = 1,sort = 3)
    private String organization_type;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }

    public String getOrganization_type() {
        return organization_type;
    }

    public void setOrganization_type(String organization_type) {
        this.organization_type = organization_type;
    }

    @Override
    public String toString() {
        return "TemplateAdminUserInfo{" +
                "mobile='" + mobile + '\'' +
                ", realname='" + realname + '\'' +
                ", organization_name='" + organization_name + '\'' +
                ", organization_type='" + organization_type + '\'' +
                '}';
    }
}
