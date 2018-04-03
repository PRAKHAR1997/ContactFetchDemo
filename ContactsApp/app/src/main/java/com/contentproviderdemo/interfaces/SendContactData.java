package com.contentproviderdemo.interfaces;

import com.contentproviderdemo.datamodel.ContactData;

import java.util.ArrayList;

/**
 * Created by appinventiv on 7/3/18.
 */

public interface SendContactData {
    void sendData(ArrayList<ContactData> arrayList);
}
