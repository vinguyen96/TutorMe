package com.vinguyen.tutorme3;

public class DataObject {
    private String mText1;
    private String mText2;
    private String mUserID;

    DataObject(String text1, String text2, String userID){
        mText1 = text1;
        mText2 = text2;
        mUserID = userID;
    }

    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        this.mUserID = userID;
    }
}