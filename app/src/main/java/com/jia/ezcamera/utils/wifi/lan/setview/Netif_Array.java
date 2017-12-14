package com.jia.ezcamera.utils.wifi.lan.setview;




import com.jia.ezcamera.utils.wifi.lan.gsonclass.Netif;

import java.util.HashMap;


public class Netif_Array {
    private static Netif_Array na = null;
    public HashMap<String, Netif> myNetifMap;

    private Netif_Array() {
        myNetifMap = new HashMap<String, Netif>();
    }

    public static Netif_Array getInstance() {
        if (na == null) {
            na = new Netif_Array();
        }
        return na;
    }

    public void clearMap() {
        if (myNetifMap != null) {
            myNetifMap.clear();
        }
    }

    public void addValue(int net_type, Netif netif) {
        if (myNetifMap != null) {
            myNetifMap.put("" + net_type, netif);
        } else {
            myNetifMap = new HashMap<String, Netif>();
            myNetifMap.put("" + net_type, netif);
        }
    }

    public Netif getValue(int net_type) {
        if (myNetifMap != null) {
            return myNetifMap.get("" + net_type);
        }
        return null;
    }
}
