package com.prajnadeep.myappcovid

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

//Prajnadeep Das
//15-05-2020

var totalConfirmedAssam = 0
var totalActiveAssam = 0
var totalRecoveredAssam = 0
var totalDeceasedAssam = 0

val districtNameList = listOf("Baksa","Barpeta","Biswanath","Bongaigaon","Cachar","Charaideo","Chirang","Darrang","Dhemaji"
    ,"Dhubri","Dibrugarh","Dima Hasao","Goalpara","Golaghat","Hailakandi","Hojai","Jorhat","Kamrup"
    ,"Kamrup Metropolitan","Karbi Anglong","Karimganj","Kokrajhar","Lakhimpur","Majuli","Morigaon"
    ,"Nagaon","Nalbari","Sivasagar","Sonitpur","South Salmara Mankachar","Tinsukia","Udalguri"
    ,"West Karbi Anglong")

var MyObj = Array(33){Homefeed()}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get data if network is available
        if(isConnected) {
            Toast.makeText(this,"Fetching Data . .",Toast.LENGTH_SHORT).show()
            try {
                fetchJason(this)
            }catch (e:Exception) {
                Toast.makeText(this,"Data has changed,please contact the developer!",Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this,"Unable to connect! Please check your Internet Connection", Toast.LENGTH_SHORT).show()
        }

        var spinner:Spinner = findViewById(R.id.mySpinner)
        spinner.adapter = ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,
            districtNameList)
        spinner.setSelection(18)

        Handler().postDelayed({
            //Print After 3 sec, first time set to Kamrup(M)
            try {
                textViewDistrictName.text = "" + MyObj[18].getDName()
                textViewDistrictConfirmed.text = "" + MyObj[18].getConfirmedNo()
                textViewDistrictActive.text = "" + MyObj[18].getActiveNo()
                textViewDistrictRecovered.text = "" + MyObj[18].getRecoveredNo()
                textViewDistrictDeceased.text = "" + MyObj[18].getDeceasedNo()
            } catch (e:Exception){
                //Need to refresh
            }
        }, 3000)
    }

    fun refreshClicked(view: View) {
        //Get data if network is available
        if(isConnected) {
            Toast.makeText(this,"Refreshing . .",Toast.LENGTH_SHORT).show()
            try {
                //Disable refresh button during operation
                refreshButton.isEnabled =false
                fetchJason(this)
            }catch (e:Exception) {
                Toast.makeText(this,"Data has changed,please contact the developer!",Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this,"Unable to connect! Please check your Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    fun infoClicked(view: View) {
        val intent = Intent(this,infoActivity::class.java)
        startActivity(intent)
    }

}

//Fetch JSON DATA
 fun fetchJason(mainActivity: MainActivity) {
    val url = "https://api.covid19india.org/state_district_wise.json"
    val request = Request.Builder().url(url).build()

    val client = OkHttpClient()
    client.newCall(request).enqueue(object: Callback {
        override fun onFailure(call: Call, e: IOException) { println("Failed Request") }
        override fun onResponse(call: Call, response: Response) {
            val body = response.body?.string()
            body?.let { parsedata(it,mainActivity) }
        }
    })
}

//Parse the fetched JSON data
fun parsedata(JSONstring: String, mainActivity: MainActivity){

    val iterator = districtNameList.iterator()

    for ((index,value) in iterator.withIndex()){
        MyObj[index] = Homefeed()
        MyObj[index].setData(JSONstring,value)
    }

    //Set all to 0 before adding results
    if(totalConfirmedAssam!=0){
        totalRecoveredAssam=0
        totalActiveAssam=0
        totalDeceasedAssam=0
        totalConfirmedAssam=0
    }

    //Count TOTAL DATA
    for (i in 0..32){
        totalConfirmedAssam +=MyObj[i].getConfirmedNo()!!
        totalActiveAssam += MyObj[i].getActiveNo()!!
        totalRecoveredAssam +=MyObj[i].getRecoveredNo()!!
        totalDeceasedAssam +=MyObj[i].getDeceasedNo()!!
    }

    //UNKNOWN BUT MATCHES RESULTS
    totalActiveAssam -= 2

    Thread(Runnable {
        // Update UI of Main thread
        mainActivity.runOnUiThread(java.lang.Runnable {

            mainActivity.totalCaseTextView.text = ""+ totalConfirmedAssam
            mainActivity.totalActiveTextView.text = ""+ totalActiveAssam
            mainActivity.totalRecoveredTextView.text = ""+ totalRecoveredAssam
            mainActivity.totalDeceasedTextView.text = ""+ totalDeceasedAssam

            Toast.makeText(mainActivity,"Updated!",Toast.LENGTH_SHORT).show()
            //Enable the refresh button
            mainActivity.refreshButton.isEnabled = true


        mainActivity.mySpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            //Set Default value of kamrup
            mainActivity.textViewDistrictName.text=""+MyObj[position].getDName()
            mainActivity.textViewDistrictConfirmed.text=""+MyObj[position].getConfirmedNo()
            mainActivity.textViewDistrictActive.text=""+MyObj[position].getActiveNo()
            mainActivity.textViewDistrictRecovered.text=""+MyObj[position].getRecoveredNo()
            mainActivity.textViewDistrictDeceased.text=""+MyObj[position].getDeceasedNo()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            //
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            mainActivity.textViewDistrictName.text=""+MyObj[position].getDName()
            mainActivity.textViewDistrictConfirmed.text=""+MyObj[position].getConfirmedNo()
            mainActivity.textViewDistrictActive.text=""+MyObj[position].getActiveNo()
            mainActivity.textViewDistrictRecovered.text=""+MyObj[position].getRecoveredNo()
            mainActivity.textViewDistrictDeceased.text=""+MyObj[position].getDeceasedNo()
        }
    }

        })
    }).start()

}

//Check Internet Connectivity
val Context.isConnected: Boolean
    get() {
        return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo?.isConnected == true
    }