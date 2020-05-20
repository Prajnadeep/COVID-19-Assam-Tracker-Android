package com.prajnadeep.myappcovid

import org.json.JSONObject

//Prajnadeep Das
//15-05-2020


public class Homefeed () {
    var districtName:String? = null
    var active:Int? = null
    var confirmed:Int? = null
    var recovered:Int? = null
    var deceased:Int? = null

    fun setData(JSONstr:String,dName:String) {
        val reader : JSONObject = JSONObject(JSONstr)
        val state = reader.getJSONObject("Assam")
        val assam = state.getJSONObject("districtData")

        val districtData = assam.getJSONObject(dName)
        this.districtName = dName
        this.active = districtData.get("active").toString().toInt()
        this.confirmed = districtData.get("confirmed").toString().toInt()
        this.recovered = districtData.get("recovered").toString().toInt()
        this.deceased = districtData.get("deceased").toString().toInt()
    }

   fun getDName(): String? { return districtName }
   fun getActiveNo(): Int? { return active}
   fun getConfirmedNo(): Int? { return confirmed }
   fun getRecoveredNo(): Int? { return recovered }
   fun getDeceasedNo(): Int? { return deceased }

}