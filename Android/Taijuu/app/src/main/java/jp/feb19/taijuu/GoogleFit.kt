package jp.feb19.taijuu

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.OnFailureListener
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

interface GoogleFitListener: EventListener {
    fun onWeightLoaded()
    fun onHeightLoaded()
    fun onSetWeight(success: Boolean)
    fun onSetHeight(success: Boolean)
}

class GoogleFit {

    val fitnessOptions: FitnessOptions? = null
    val TAG = "GoogleFit"
    val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1024
    var listener: GoogleFitListener? = null
    var bodyMasses = ArrayList<BodyMass>()
    var height = 0.0
    var gsa: GoogleSignInAccount? = null

    companion object {
        private var instance: GoogleFit? = null

        fun shared() = instance ?: synchronized(this) {
            instance ?: GoogleFit().also { instance = it }
        }
    }

    fun signIn(activity: Activity, context: Context) {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_WEIGHT)
            .addDataType(DataType.TYPE_HEIGHT)
            // Google Fit には BMI がない
            .build()

        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                activity,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(context),
                fitnessOptions
            )
        } else {
            accessGoogleFit(activity)
        }
    }

    fun accessGoogleFit(activity: Activity) {
        Log.i("TaijuuApp", "accessGoogleFit")
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_WEIGHT)
            .addDataType(DataType.TYPE_HEIGHT)
            .build()
        gsa = GoogleSignIn.getAccountForExtension(activity, fitnessOptions)

        getWeight(activity)
        getHeight(activity)
    }


    fun getWeight(activity: Activity) {
        val startOfMonth = Calendar.getInstance()
        startOfMonth.set(Calendar.YEAR, 2016)
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0)
        startOfMonth.set(Calendar.MINUTE, 0)
        startOfMonth.set(Calendar.SECOND, 0)
        startOfMonth.set(Calendar.MILLISECOND, 0)
        Log.i("TaijuuApp", "getWeight")

        val now = Date().time
        val request = DataReadRequest.Builder()
            .setTimeRange(startOfMonth.timeInMillis, now, TimeUnit.MILLISECONDS)
            .read(DataType.TYPE_WEIGHT)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()


        Log.i("TaijuuApp", "${gsa!!}")

        Fitness.getHistoryClient(activity, gsa!!)
            .readData(request)
            .addOnFailureListener { e ->
                Log.i("TaijuuApp","failure to read weights ${e}")

            }
            .addOnSuccessListener {
                Log.i("TaijuuApp","success to read weights")
                it.buckets.forEach {

                    val dataSet = it.getDataSet(DataType.TYPE_WEIGHT)
                    Log.i("TaijuuApp", dataSet?.dataPoints?.size.toString())

                    var bodyMass : BodyMass? = null
                    dataSet?.dataPoints?.forEach {
                        val df = SimpleDateFormat("yyyy/MM/dd HH:mm")
                        val start = df.format(it.getStartTime(TimeUnit.MILLISECONDS))
                        val end = df.format(it.getEndTime(TimeUnit.MILLISECONDS))

                        val value = it.getValue(Field.FIELD_WEIGHT)
                        Log.i("TaijuuApp", "\tweight: ${start}-${end}: ${value}kg")

                        val df2 = SimpleDateFormat("M/dd")
                        val date = df2.format(it.getEndTime(TimeUnit.MILLISECONDS))

                        bodyMass = BodyMass(value.toString().toDouble(), date.toString())

                    }

                    if (bodyMass != null) {
                        bodyMasses.add(bodyMass!!)
                    }
                }
                bodyMasses = bodyMasses.reversed() as ArrayList<BodyMass>

                listener?.onWeightLoaded()
            }
    }

    fun getHeight(activity: Activity) {
        val startOfMonth = Calendar.getInstance()
        startOfMonth.set(Calendar.YEAR, 2016)
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0)
        startOfMonth.set(Calendar.MINUTE, 0)
        startOfMonth.set(Calendar.SECOND, 0)
        startOfMonth.set(Calendar.MILLISECOND, 0)

        val now = Date().time
        val request = DataReadRequest.Builder()
            .setTimeRange(startOfMonth.timeInMillis, now, TimeUnit.MILLISECONDS)
            .read(DataType.TYPE_HEIGHT)
            .build()

        Fitness.getHistoryClient(activity, gsa!!)
            .readData(request)
            .addOnFailureListener {
                Log.i("TaijuuApp","failure to read height")
            }
            .addOnSuccessListener {
                Log.i("TaijuuApp", "success to read height!")
                val dataSet = it.getDataSet(DataType.TYPE_HEIGHT)
                Log.i("TaijuuApp", dataSet.dataPoints.size.toString())
                dataSet.dataPoints.forEach {
                    val df = SimpleDateFormat("yyyy/MM/dd HH:mm")
                    val start = df.format(it.getStartTime(TimeUnit.MILLISECONDS))
                    val end = df.format(it.getEndTime(TimeUnit.MILLISECONDS))
                    val value = it.getValue(Field.FIELD_HEIGHT)
                    Log.i("TaijuuApp", "\theight: ${start}-${end}: ${value}m")
                    height = value.toString().toDouble()
                }

                listener?.onHeightLoaded()
            }
    }

    fun setWeight(bodyMass: Float, context: Context) {
        val dataSource = DataSource.Builder()
            .setAppPackageName("jp.feb19.taijuu")
            .setDataType(DataType.TYPE_WEIGHT)
            .setType(DataSource.TYPE_RAW)
            .setStreamName("Taijuu")
            .build()

        val dataPoint = DataPoint.builder(dataSource)
            .setTimeInterval(Date().time, Date().time, TimeUnit.MILLISECONDS)
            .setField(Field.FIELD_WEIGHT, bodyMass)
            .build()

        val dataSet = DataSet.builder(dataSource)
            .add(dataPoint)
            .build()

        Fitness.getHistoryClient(context, gsa!!)
            .insertData(dataSet)
            .addOnCanceledListener {
                Toast.makeText(context, "体重の記録をキャンセルしました", Toast.LENGTH_SHORT).show()
                listener?.onSetWeight(false)
            }
            .addOnFailureListener {
                Toast.makeText(context, "体重の記録が失敗しました", Toast.LENGTH_SHORT).show()
                listener?.onSetWeight(false)
            }
            .addOnSuccessListener {
                Toast.makeText(context, "体重を記録しました", Toast.LENGTH_SHORT).show()
                listener?.onSetWeight(true)
            }
            .addOnCompleteListener {
                Log.i("Taijuu", "complete")
            }
    }

    fun setHeight(heightCM: Float, context: Context) {
        val dataSource = DataSource.Builder()
            .setAppPackageName("jp.feb19.taijuu")
            .setDataType(DataType.TYPE_HEIGHT)
            .setType(DataSource.TYPE_RAW)
            .setStreamName("Taijuu")
            .build()

        val dataPoint = DataPoint.builder(dataSource)
            .setTimeInterval(Date().time, Date().time, TimeUnit.MILLISECONDS)
            .setField(Field.FIELD_HEIGHT, heightCM)
            .build()

        val dataSet = DataSet.builder(dataSource)
            .add(dataPoint)
            .build()

        Fitness.getHistoryClient(context, gsa!!)
            .insertData(dataSet)
            .addOnCanceledListener {
                Log.i("Taijuu", "cancel")

                Toast.makeText(context, "身長の記録をキャンセルしました", Toast.LENGTH_SHORT).show()
                listener?.onSetHeight(false)
            }
            .addOnFailureListener {
                Log.i("Taijuu", "failure")

                Toast.makeText(context, "身長の記録が失敗しました", Toast.LENGTH_SHORT).show()
                listener?.onSetHeight(false)
            }
            .addOnSuccessListener {
                Log.i("Taijuu", "success")

                Toast.makeText(context, "身長を記録しました", Toast.LENGTH_SHORT).show()
                listener?.onSetHeight(true)
            }
            .addOnCompleteListener {
                Log.i("Taijuu", "complete")

            }
    }
}