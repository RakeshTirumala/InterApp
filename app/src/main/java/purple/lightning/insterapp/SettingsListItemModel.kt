package purple.lightning.insterapp

import android.graphics.drawable.Icon


data class SettingsListItemModel(
    var settingName:String,
    var icon: Int
){
    fun getsettingName():String{
        return settingName
    }

    fun setId(settingName:String){
        this.settingName = settingName
    }

    fun geticon():Int{
        return icon
    }

    fun seticon(icon: Int){
        this.icon = icon
    }
}


