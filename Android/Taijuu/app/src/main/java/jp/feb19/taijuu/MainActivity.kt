package jp.feb19.taijuu

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.app.PendingIntent
import java.util.*
import android.app.AlarmManager




class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    val textView: TextView = itemView.findViewById(R.id.textView)
    var weightTextView = itemView.findViewById<TextView>(R.id.weightEditText)
}

class RecyclerAdapter(context: Context, val data: List<BodyMass>) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {
    val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // ここでViewHolderを作る
        return ViewHolder(inflater.inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int {
        // データの要素数を返す
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ViewHolderを通してデータをViewに設定する

        holder.textView.text = data[position].date
        holder.weightTextView.text = "${data[position].bodyMass.toString()}kg"
    }
}

class MainActivity : AppCompatActivity(), GoogleFitListener {

    override fun onSetWeight(success: Boolean) {

    }

    override fun onSetHeight(success: Boolean) {

    }

    override fun onHeightLoaded() {

    }

    override fun onWeightLoaded() {
        Log.i("TaijuuApp", "onWeightLoaded")
        val swipeRefreshView = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshView)
        swipeRefreshView.isRefreshing = false

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = RecyclerAdapter(this, GoogleFit.shared().bodyMasses)

        val bodyMassView = findViewById<BodyMassGraphView>(R.id.bodyMassView)
        bodyMassView.invalidate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { _ ->
            val intent = Intent(application, InputPickerbasedActivity::class.java)
            startActivity(intent)
        }

        GoogleFit.shared().signIn(this, this)

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = RecyclerAdapter(this, GoogleFit.shared().bodyMasses)

        val swipeRefreshView = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshView)
        swipeRefreshView.setOnRefreshListener {
            GoogleFit.shared().getWeight(this)
        }
    }

    var pendingIntent: PendingIntent? = null

    override fun onResume() {
        super.onResume()
        Log.i("TaijuuApp", "onResume")

        GoogleFit.shared().listener = this

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (pendingIntent != null) {
            pendingIntent!!.cancel()
            manager.cancel(pendingIntent!!)
        }

        val triggerTime = Calendar.getInstance()
//        triggerTime.add(Calendar.SECOND, 5)
        triggerTime.add(Calendar.HOUR_OF_DAY, 12)
        Log.i("TaijuuApp", "${triggerTime.time}")

        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            TaijuuApplication.shared.applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        manager.set(AlarmManager.RTC_WAKEUP, triggerTime.timeInMillis, pendingIntent)

    }

    override fun onPause() {
        super.onPause()

        GoogleFit.shared().listener = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.i("TaijuuApp", "onActivityResult")

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GoogleFit.shared().GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                Log.i("TaijuuApp", "accessGoogleFit")
                GoogleFit.shared().accessGoogleFit(this)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu?.getItem(1)

        val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
        val passcode = sharedPreferences.getString("passcode", "")
        if (passcode == "") {
            item?.title = "パスコードを設定"
        } else {
            item?.title = "パスコードを削除"
        }
        return true //super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.itemId == R.id.action_setting_height) {
            val intent = Intent(application, SettingHeightActivity::class.java)
            startActivity(intent)
        }
        if (item.itemId == R.id.action_setting_passcode) {
            val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
            val passcode = sharedPreferences.getString("passcode", "")
            if (passcode == "") {
                val intent = Intent(application, SettingPasscodeActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(application, SettingRemovePasscodeActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}
