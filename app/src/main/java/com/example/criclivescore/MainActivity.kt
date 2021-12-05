package com.example.criclivescore

import android.R
import android.app.LauncherActivity.ListItem
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.google.androidgamesdk.gametextinput.Listener
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import javax.xml.transform.ErrorListener

class MainActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var listItems: MutableList<ListItem>? = null
    private var rq: RequestQueue? = null
    var mHandler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setLayoutManager(LinearLayoutManager(this))
        listItems = ArrayList()
        rq = Volley.newRequestQueue(this)
        jsonParse()
        mHandler = Handler()
        m_Runnable.run()
    }

    private val m_Runnable: Runnable = object : Runnable {
        override fun run() {
            mHandler!!.postDelayed(this, 10000)
        }
    }

    private fun jsonParse() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading Current Series...")
        progressDialog.show()
        val request =
            JsonObjectRequest(Request.Method.GET, url, null, object : Listener<JSONObject?>(),
                Response.Listener<JSONObject> {
                override fun onResponse(response: JSONObject) {
                    progressDialog.dismiss()
                    try {
                        val jsonArray = response.getJSONArray("matches")
                        for (i in 0 until jsonArray.length()) {
                            val match = jsonArray.getJSONObject(i)
                            val title = (match.getJSONObject("header").getString("match_desc")
                                    + " "
                                    + match.getJSONObject("team1").getString("name")
                                    + " vs "
                                    + match.getJSONObject("team2").getString("name"))
                            val status = match.getJSONObject("header").getString("status")
                            val matchId = match.getString("match_id")
                            var details = ""
                            if (match.has("batsman")) {
                                val batsmens = match.getJSONArray("batsman")
                                Log.d("MyApp", batsmens.toString())
                                for (j in 0 until batsmens.length()) {
                                    details += """${
                                        batsmens.getJSONObject(j).getString("name")
                                    } Runs: ${
                                        batsmens.getJSONObject(j).getString("r")
                                    } Balls: ${
                                        batsmens.getJSONObject(j).getString("b")
                                    } 4s: ${
                                        batsmens.getJSONObject(j).getString("4s")
                                    } 6s: ${batsmens.getJSONObject(j).getString("6s")}
"""
                                }
                            }
                            if (match.has("bowler")) {
                                val bowler = match.getJSONArray("bowler").getJSONObject(0)
                                details += bowler.getString("name") +
                                        " Overs: " + bowler.getString("o") +
                                        " Maidens: " + bowler.getString("m") +
                                        " Runs: " + bowler.getString("r") +
                                        " Wickets: " + bowler.getString("w")
                            }
                            val item = ListItem(title, status, matchId, details)
                            listItems!!.add(item)
                        }
                        adapter = MyAdapter(listItems, applicationContext, "MainActivity")
                        recyclerView!!.adapter = adapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }, object : ErrorListener() {
                fun onErrorResponse(error: VolleyError) {
                    error.printStackTrace()
                }
            })
        rq.add(request)
    }

    companion object {
        private const val url = "https://mapps.cricbuzz.com/cbzios/match/livematches.json"
    }
}