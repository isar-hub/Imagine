import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.isar.imagine.R
import com.isar.imagine.responses.StackViewList

class StackViewAdapter
    (
    private var con : Context,
    private var resource: Int,
    private var list: ArrayList<StackViewList>,
) : ArrayAdapter<Any?>(
    con,resource, list as List<Any?>
) {
    // getCount() is called to return
    // the total number of words to be used
    override fun getCount(): Int {
        return list.size
    }

    // getView() is called to get position,
    // parent and view of the images.
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView = LayoutInflater.from(parent.context).inflate(resource, parent, false)


        val list = list[position]
        val brandName = list.brandName
        val count = list.count
        val textView = convertView?.findViewById<TextView>(R.id.brandName)
        val imageView = convertView?.findViewById<TextView>(R.id.count)
        if (textView != null) {
            textView.text = brandName
        }
        if (imageView != null) {
            imageView.text = count.toString()
        }
        return convertView
    }
}