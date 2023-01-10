package purple.lightning.insterapp

import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import purple.lightning.insterapp.databinding.SettingsListItemRowBinding

class SettingsAdapter(private var list: List<SettingsListItemModel>, private val onUserClickListener:OnUserClickListener):
    RecyclerView.Adapter<SettingsAdapter.ViewHolder>(){

    class ViewHolder(binding: SettingsListItemRowBinding) : RecyclerView.ViewHolder(binding.root){
        val tvSettingsListItemName = binding.tvSettingsListItemName
        val ivSettingsListItemIcon = binding.ivSettingsListItemIcon

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SettingsListItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.tvSettingsListItemName.text = model.settingName
        holder.ivSettingsListItemIcon.setBackgroundResource(model.icon)
        holder.tvSettingsListItemName.setOnClickListener {
            onUserClickListener.onUserClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnUserClickListener{
        fun onUserClickListener(position: Int)
    }
}