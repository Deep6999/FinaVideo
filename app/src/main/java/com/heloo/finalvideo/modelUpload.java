package com.heloo.finalvideo;

public class modelUpload {
    private String title,vurl,gmailofuploader,Discription;

    public String Discription() {
        return Discription;
    }

    public void setNameofuploader(String Discription) {
        this.Discription = Discription;
    }

    public modelUpload(String title, String vurl, String gmailofuploader, String Discription) {
        this.title = title;
        this.vurl = vurl;
        this.gmailofuploader = gmailofuploader;
        this.Discription = Discription;
    }

    public String getGmailofuploader() {
        return gmailofuploader;
    }

    public void setGmailofuploader(String gmailofuploader) {
        this.gmailofuploader = gmailofuploader;
    }

    public String getDiscription() {
        return Discription;
    }

    public void setDiscription(String discription) {
        Discription = discription;
    }

    public modelUpload(String title, String vurl, String gmailofuploader) {
        this.title = title;
        this.vurl = vurl;
        this.gmailofuploader = gmailofuploader;
    }

    public modelUpload() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVurl() {
        return vurl;
    }

    public void setVurl(String vurl) {
        this.vurl = vurl;
    }

    public modelUpload(String title, String vurl) {
        this.title = title;
        this.vurl = vurl;
    }
}
