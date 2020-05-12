package com.asktown.cupboard.data.model

class IngredientCard(name: String, type: String, imgLocation: String) {

    var mName = name
    var mType = type
    var mImgLocation = imgLocation

    public fun getName(): String {
        return mName;
    }

    public fun getType(): String {
        return mType;
    }

    public fun getImgLocation(): String {
        return mImgLocation;
    }

}