package com.fyang21117.smelldata.view;

public class Item {
    private  int id;
    private String name;
    private String info;
    private int image_photo;

    public Item(){
        super();
    }
    public Item(int id, String name){
        super();
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public int getImage_photo() {
        return image_photo;
    }
    public void setImage_photo(int image_photo) {
        this.image_photo = image_photo;
    }
    @Override
    public String toString(){
        return "Item[id="+id+",name="+name+"]";
    }
}
