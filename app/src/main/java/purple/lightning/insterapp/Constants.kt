package purple.lightning.insterapp

object Constants {
    fun getSettingsList():ArrayList<SettingsListItemModel>{
        var settingsList = ArrayList<SettingsListItemModel>()
        settingsList.add(SettingsListItemModel("Change Password", R.drawable.ic_baseline_lock_reset_24))
        settingsList.add(SettingsListItemModel("Forgot Password", R.drawable.ic_baseline_lock_person_24))
        settingsList.add(SettingsListItemModel("Email", R.drawable.ic_baseline_mail_outline_24))
        settingsList.add(SettingsListItemModel("Logout", R.drawable.ic_baseline_logout_24))

        return settingsList
    }
}