package com.example.criclivescore


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
import com.example.criclivescore.ListItem
class MainActivity : AppCompatActivity() {
    //    lateinit var binding: ListItemBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerView.Adapter<*>
    lateinit var listItems: MutableList<ListItem>
    lateinit var rq: RequestQueue
    lateinit var mHandler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding=DataBindingUtil.setContentView(this,R.layout.activity_list_item)
//        binding.textViewHead
//        binding.textViewDescription
//        binding.textViewDetails


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
            JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener<JSONObject> {
                    fun onResponse(response: JSONObject) {
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
                                val item = com.example.criclivescore.ListItem(title, status, matchId, details)
                                listItems!!.add(item)
                            }
                            adapter = MyAdapter(listItems, applicationContext, "MainActivity")
                            recyclerView!!.adapter = adapter
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        error.printStackTrace()
                    }
                })
        rq.add(request)
    }
    companion object {
        private const val url = "https://mapps.cricbuzz.com/cbzios/match/livematches.json"
    }
}