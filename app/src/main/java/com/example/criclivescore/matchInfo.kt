package com.example.criclivescore

import android.R
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class matchInfo : AppCompatActivity(), GestureDetector.OnGestureListener {
    var matchId: String? = null
    private var rq: RequestQueue? = null
    private var series_name: TextView? = null
    private var venue: TextView? = null
    private var toss: TextView? = null
    private var team1Name: TextView? = null
    private var team1: ListView? = null
    private var arrayList1: ArrayList<String?>? = null
    private var team2Name: TextView? = null
    private var team2: ListView? = null
    private var arrayList2: ArrayList<String?>? = null
    private var detector: GestureDetectorCompat? = null
    var mHandler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        matchId = intent.getStringExtra("matchId")
        url = "https://mapps.cricbuzz.com/cbzios/match/$matchId"
        rq = Volley.newRequestQueue(this)
        arrayList1 = ArrayList()
        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this, R.layout.simple_list_item_1,
            arrayList1!! as List<Any?>
        )
        team1?.setAdapter(arrayAdapter)
        arrayList2 = ArrayList()
        val arrayAdapter2: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this, R.layout.simple_list_item_1,
            arrayList2!! as List<Any?>
        )
        team2?.setAdapter(arrayAdapter2)
        detector = GestureDetectorCompat(applicationContext, this)
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
        progressDialog.setMessage("Loading Match Info...")
        progressDialog.show()
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                progressDialog.dismiss()
                try {
                    val seriesName = "Series: " + response.getString("series_name")
                    series_name!!.text = seriesName
                    val venueObj = response.getJSONObject("venue")
                    val venueInfo = ("Venue: "
                            + venueObj.getString("name")
                            + ", "
                            + venueObj.getString("location"))
                    venue!!.text = venueInfo
                    val tossInfo = "Toss: " +
                            response.getJSONObject("header").getString("toss")
                    toss!!.text = tossInfo
                    val team1NameInfo = response.getJSONObject("team1").getString("name") +
                            " Line up: "
                    team1Name!!.text = team1NameInfo
                    val players = response.getJSONArray("players")
                    val team1 = response.getJSONObject("team1").getJSONArray("squad")
                    for (i in 0 until team1.length()) {
                        for (j in 0 until players.length()) {
                            val teamPlayerId = team1.getInt(i)
                            val playerId = players.getJSONObject(j).getString("id").toInt()
                            if (teamPlayerId == playerId) {
                                arrayList1!!.add(players.getJSONObject(j).getString("f_name"))
                            }
                        }
                    }
                    val team2NameInfo = response.getJSONObject("team2").getString("name") +
                            " Line up: "
                    team2Name!!.text = team2NameInfo
                    val team2 = response.getJSONObject("team2").getJSONArray("squad")
                    for (i in 0 until team2.length()) {
                        for (j in 0 until players.length()) {
                            val teamPlayerId = team2.getInt(i)
                            val playerId = players.getJSONObject(j).getString("id").toInt()
                            if (teamPlayerId == playerId) {
                                arrayList2!!.add(players.getJSONObject(j).getString("f_name"))
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ) { error -> error.printStackTrace() }
        rq!!.add(request)
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {}
    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e1.x - e2.x > 5 && Math.abs(velocityX) > 5) {
            val intent = Intent(applicationContext, scorecard::class.java)
            intent.putExtra("matchId", matchId)
            startActivity(intent)
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        detector!!.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    fun view_scorecard(v: View?) {
        val intent = Intent(applicationContext, scorecard::class.java)
        intent.putExtra("matchId", matchId)
        startActivity(intent)
    }

    companion object {
        private var url: String? = null
    }
}