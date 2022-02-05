package net.primegames.data;


public enum Period {

    CURRENT,
    PREVIOUS;

    @Override
    public String toString(){
        String name = super.toString();
        return name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
