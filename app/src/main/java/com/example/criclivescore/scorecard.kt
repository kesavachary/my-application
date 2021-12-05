package com.example.criclivescore

import android.R
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.criclivescore.ListItem
import org.json.JSONArray
import org.json.JSONException
import java.lang.Exception
import java.util.ArrayList

class scorecard : AppCompatActivity() {
    private var matchId: String? = null
    private var url: String? = null
    private var url2: String? = null
    private var rq: RequestQueue? = null
    private var rq2: RequestQueue? = null
    private var team1Name: TextView? = null
    private var team1Score: TextView? = null
    private var team2Name: TextView? = null
    private var team2Score: TextView? = null
    private var team1ScoreMain: TextView? = null
    private var team2ScoreMain: TextView? = null
    private var team1Scorecard: Button? = null
    private var team2Scorecard: Button? = null
    private var team1Scorecard2: Button? = null
    private var team2Scorecard2: Button? = null
    private var recyclerView: RecyclerView? = null
    private var listItems: MutableList<ListItem>? = null
    private var listItems2: MutableList<ListItem>? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var adapter2: RecyclerView.Adapter<*>? = null
    private var listItems3: MutableList<ListItem>? = null
    private var listItems4: MutableList<ListItem>? = null
    private var adapter3: RecyclerView.Adapter<*>? = null
    private var adapter4: RecyclerView.Adapter<*>? = null
    var mHandler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        matchId = intent.getStringExtra("matchId")
        url = "http://mapps.cricbuzz.com/cbzios/match/$matchId/scorecard.json"
        url2 = "https://mapps.cricbuzz.com/cbzios/match/$matchId"
        rq = Volley.newRequestQueue(this)
        rq2 = Volley.newRequestQueue(this)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setLayoutManager(LinearLayoutManager(this))
        listItems = ArrayList()
        listItems2 = ArrayList()
        listItems3 = ArrayList()
        listItems4 = ArrayList()
        recyclerView?.setVisibility(View.INVISIBLE)
        jsonParse()
        mHandler = Handler()
        m_Runnable.run()
    }

    private val m_Runnable: Runnable = object : Runnable {
        override fun run() {
            mHandler!!.postDelayed(this, 5000)
        }
    }

    private fun jsonParse() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading Match Info...")
        progressDialog.show()
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                progressDialog.dismiss()
                try {
                    val innings = response.getJSONArray("Innings")
                    val team1Stuff = innings.getJSONObject(0)
                    val team1Info = team1Stuff.getString("bat_team_name")
                    team1Name!!.text = team1Info
                    team1ScoreMain!!.text = "$team1Info Scorecard: "
                    team1Scorecard!!.text = "Batting"
                    team1Scorecard2!!.text = "Bowling"
                    val team1ScoreInfo =
                        team1Stuff.getString("score") + "/" + team1Stuff.getString("wkts") + " " + team1Stuff.getString(
                            "ovr"
                        ).toFloat() + " ov"
                    team1Score!!.text = team1ScoreInfo
                    val team2Stuff = innings.getJSONObject(1)
                    val team2Info = team2Stuff.getString("bat_team_name")
                    team2Name!!.text = team2Info
                    team2ScoreMain!!.text = "$team2Info Scorecard: "
                    team2Scorecard!!.text = "Batting"
                    team2Scorecard2!!.text = "Bowling"
                    val team2ScoreInfo =
                        team2Stuff.getString("score") + "/" + team2Stuff.getString("wkts") + " " + team2Stuff.getString(
                            "ovr"
                        ).toFloat() + " ov"
                    team2Score!!.text = team2ScoreInfo
                    Log.i("Scorecard", "Making the actual scorecard")
                    players(innings)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error -> error.printStackTrace() }
        rq!!.add(request)
    }

    private fun players(innings: JSONArray) {
        val request = JsonObjectRequest(
            Request.Method.GET, url2, null,
            { response ->
                try {
                    val players = response.getJSONArray("players")
                    var batsmen = innings.getJSONObject(0).getJSONArray("batsmen")
                    for (j in 0 until batsmen.length()) {
                        val currBatsmen = batsmen.getJSONObject(j)
                        for (k in 0 until players.length()) {
                            if (players.getJSONObject(k)
                                    .getString("id") == currBatsmen.getString("id")
                            ) {
                                listItems!!.add(
                                    ListItem(
                                        players.getJSONObject(k).getString("f_name"),
                                        currBatsmen.getString("out_desc"), "",
                                        "Runs: " + currBatsmen.getString("r") + " Balls: " +
                                                currBatsmen.getString("b") + " 4s: " +
                                                currBatsmen.getString("4s") + " 6s: " +
                                                currBatsmen.getString("6s")
                                    )
                                )
                            }
                        }
                    }
                    adapter = MyAdapter(listItems!!, applicationContext, "scorecard")
                    recyclerView!!.adapter = adapter
                    batsmen = innings.getJSONObject(1).getJSONArray("batsmen")
                    for (j in 0 until batsmen.length()) {
                        val currBatsmen = batsmen.getJSONObject(j)
                        for (k in 0 until players.length()) {
                            if (players.getJSONObject(k)
                                    .getString("id") == currBatsmen.getString("id")
                            ) {
                                listItems2!!.add(
                                    ListItem(
                                        players.getJSONObject(k).getString("f_name"),
                                        currBatsmen.getString("out_desc"), "",
                                        "Runs: " + currBatsmen.getString("r") + " Balls: " +
                                                currBatsmen.getString("b") + " 4s: " +
                                                currBatsmen.getString("4s") + " 6s: " +
                                                currBatsmen.getString("6s")
                                    )
                                )
                            }
                        }
                    }
                    adapter2 = MyAdapter(listItems2!!, applicationContext, "scorecard")
                    var bowler = innings.getJSONObject(0).getJSONArray("bowlers")
                    for (j in 0 until bowler.length()) {
                        val currBowler = bowler.getJSONObject(j)
                        for (k in 0 until players.length()) {
                            if (players.getJSONObject(k)
                                    .getString("id") == currBowler.getString("id")
                            ) {
                                listItems3!!.add(
                                    ListItem(
                                        players.getJSONObject(k).getString("f_name"),
                                        "Wickets: " + currBowler.getString("w") + " Runs: " + currBowler.getString(
                                            "r"
                                        ), "",
                                        " Overs: " + currBowler.getString("o") +
                                                " Maidens: " + currBowler.getString("m") +
                                                " Wides: " + currBowler.getString("wd") +
                                                " No Balls: " + currBowler.getString("n")
                                    )
                                )
                            }
                        }
                    }
                    adapter3 = MyAdapter(listItems3!!, applicationContext, "scorecard")
                    bowler = innings.getJSONObject(1).getJSONArray("bowlers")
                    for (j in 0 until bowler.length()) {
                        val currBowler = bowler.getJSONObject(j)
                        for (k in 0 until players.length()) {
                            if (players.getJSONObject(k)
                                    .getString("id") == currBowler.getString("id")
                            ) {
                                listItems4!!.add(
                                    ListItem(
                                        players.getJSONObject(k).getString("f_name"),
                                        "Wickets: " + currBowler.getString("w") + " Runs: " + currBowler.getString(
                                            "r"
                                        ), "",
                                        " Overs: " + currBowler.getString("o") +
                                                " Maidens: " + currBowler.getString("m") +
                                                " Wides: " + currBowler.getString("wd") +
                                                " No Balls: " + currBowler.getString("n")
                                    )
                                )
                            }
                        }
                    }
                    adapter4 = MyAdapter(listItems4!!, applicationContext, "scorecard")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) { error -> error.printStackTrace() }
        rq2!!.add(request)
    }

    fun team1OnClick(v: View?) {
        recyclerView!!.adapter = adapter
        recyclerView!!.visibility = View.VISIBLE
    }

    fun team2OnClick(v: View?) {
        recyclerView!!.adapter = adapter2
        recyclerView!!.visibility = View.VISIBLE
    }

    fun team3OnClick(v: View?) {
        recyclerView!!.adapter = adapter4
        recyclerView!!.visibility = View.VISIBLE
    }

    fun team4OnClick(v: View?) {
        recyclerView!!.adapter = adapter3
        recyclerView!!.visibility = View.VISIBLE
    }
}