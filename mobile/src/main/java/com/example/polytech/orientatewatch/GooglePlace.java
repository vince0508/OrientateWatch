package com.example.polytech.orientatewatch;


public class GooglePlace {
    private String name;
    private String open;
    private String adress;
    private String dureeT;
    private double lat;
    private double longi;
    private String price;
    private String rating;
    private String iconURL;


    public GooglePlace() {
        this.name = "";
        this.open = "Not Known";
        this.adress="";
        this.dureeT="";
        this.lat=0;
        this.longi=0;
        this.price="";
        this.rating="";
        this.iconURL="";
    }



    public void setIcon(String i) {
        this.iconURL = i;
    }

    public String getIcon() {
        return iconURL;
    }

    public void setRate(String r) {
        this.rating = r;
    }

    public String getRate() {
        return rating;
    }

    public void setPrice(String p) {
        this.price = p;
    }

    public String getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Double getLat() {
        return lat;
    }


    public void setLong(double longi) {
        this.longi = longi;
    }

    public Double getLong() {
        return longi;
    }


    public void setOpenNow(String open) {
        this.open = open;
    }

    public String getOpenNow() {
        return open;
    }

    public boolean isOpenNow() {
        return(open=="YES");
    }

    public boolean isNotOpenNow() {
        return(open=="NO");
    }

    public boolean isNotInformed() {
        return(open=="Not Known");
    }

    public void setAddr(String addr) {
        this.adress = addr;
    }

    public String getAddr() {
        return adress;
    }

    public void setDuree(String t) {
        this.dureeT = t;
    }

    public String getDuree() {
        return dureeT;
    }
}
