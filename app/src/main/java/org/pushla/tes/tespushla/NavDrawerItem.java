package org.pushla.tes.tespushla;

/**
 * Created by Laila L on 29/03/2015.
 */

public class NavDrawerItem {

    private String title;
    private int icon;


    public NavDrawerItem(){}

    public NavDrawerItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }


    public int getIcon(){
        return this.icon;
    }

    public String getTitle(){
        return this.title;
    }
}
