package com.example.dragon.exampleproject;

import com.example.dragon.exampleproject.drawable.DrawablesActivity;
import com.example.dragon.exampleproject.paper.PagerActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class MenuData {
    public ArrayList<MenuItemData> mainMenuItems = new ArrayList<>();
    private Class showMenuActivityClass;

    public MenuData(Class showMenuActivityClass){
        this.showMenuActivityClass = showMenuActivityClass;
        mainMenuItems.add(createDrawableSubMenu());
        mainMenuItems.add(createWidgetSubMenu());
        mainMenuItems.add(new MenuItemData("WifiDisplay",WifiDisplayActivity.class,new ArrayList<MenuItemData>()));
        mainMenuItems.add(new MenuItemData("Camera2WifiDisplay",Camera2WifiDisplayActivity.class,new ArrayList<MenuItemData>()));
        mainMenuItems.add(new MenuItemData("Orientation",OrientationActivity.class,new ArrayList<MenuItemData>()));
        mainMenuItems.add(new MenuItemData("DataBinding",DataBindingActivity.class,new ArrayList<MenuItemData>()));
    }

    private MenuItemData createDrawableSubMenu(){
        MenuItemData drawableSubMenu = new MenuItemData( "Drawable submenu", showMenuActivityClass, new ArrayList<MenuItemData>());
        drawableSubMenu.subMenuItems.add(new MenuItemData("Rainbow Drawable", DrawablesActivity.class));
        return drawableSubMenu;
    }
    private MenuItemData createWidgetSubMenu(){
        MenuItemData drawableSubMenu = new MenuItemData( "Widget submenu", showMenuActivityClass, new ArrayList<MenuItemData>());
        drawableSubMenu.subMenuItems.add(new MenuItemData("HVLPager", PagerActivity.class));
        return drawableSubMenu;
    }

    public static class MenuItemData implements Serializable {
        public MenuItemData(String title, Class aClass){
            this(title,aClass,null);
        }
        public MenuItemData(String title, Class aClass, ArrayList<MenuItemData> sub){
            this.title = title;
            this.aClass = aClass;
            this.subMenuItems = sub;
        }
        public ArrayList<MenuItemData> subMenuItems;
        public String title;
        public Class aClass;
    }
}
