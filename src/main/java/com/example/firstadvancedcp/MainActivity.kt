package com.example.firstadvancedcp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    // JSON object in the form of input stream
 /*   private val listData: String
        get() = ("{ \"users\" :{" +
                "[\"rows\" :{" +
                "{\"name\":\"Ace\" ,\"designation\":\"Engineer\" ,\"location\":\"New York\"}" +
                ",{\"name\":\"Tom\"  ,\"designation\":\"Director\"  ,\"location\":\"Chicago\"}" +
                ",{\"name\":\"Tim\"  ,\"designation\":\"Charted Accountant\"  ,\"location\":\"Sunnyvale\"} } ] } }")

  */


    private val listData: String
        get() = ("{ \"users\" :[" +
                "{\"name\":\"Ace\",\"designation\":\"Engineer\",\"location\":\"New York\"}" +
                ",{\"name\":\"Tom\",\"designation\":\"Director\",\"location\":\"Chicago\"}" +
                ",{\"name\":\"Tim\",\"designation\":\"Charted Accountant\",\"location\":\"Sunnyvale\"}] }")

    // Create a userList string hashmap arraylist
    val userList = ArrayList<HashMap<String, String?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

     /*   val jsonStr = listData

        // Initializing the JSON object and extracting the information
        val jObj = JSONObject(jsonStr)

        val jsonArray = jObj.getJSONObject("users")

        val jsonArr = jsonArray.getJSONArray("rows")

        for (i in 0 until jsonArr.length()) {
            val user = HashMap<String, String?>()
            val obj = jsonArr.getJSONObject(i)
            user["name"] = obj.getString("name")
            user["designation"] = obj.getString("designation")
            user["location"] = obj.getString("location")
            userList.add(user)

            Log.d("TAG", "onCreate: USER LIST SIZE "+userList.size)
            Log.d("TAG", "onCreate: USER LIST  "+userList.get(i).get("name"))
        }*/

        // private string declare in the latter section of the program
        val jsonStr = listData

            // Initializing the JSON object and extracting the information
            val jObj = JSONObject(jsonStr)
            val jsonArry = jObj.getJSONArray("users")
            for (i in 0 until jsonArry.length()) {
                val user = HashMap<String, String?>()
                val obj = jsonArry.getJSONObject(i)
                user["name"] = obj.getString("name")
                user["designation"] = obj.getString("designation")
                user["location"] = obj.getString("location")
                userList.add(user)

                Log.d("TAG", "onCreate: USER LIST SIZE "+userList.size)
                Log.d("TAG", "onCreate: USER LIST  "+userList.get(i).get("name"))
                Log.d("TAG", "onCreate: USER LIST  "+userList.get(i).get("designation"))
                Log.d("TAG", "onCreate: USER LIST  "+userList.get(i).get("location"))
            }

    }
}