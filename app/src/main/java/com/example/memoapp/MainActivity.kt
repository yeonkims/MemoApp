package com.example.memoapp

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("StaticFieldLeak")
class MainActivity : AppCompatActivity() {

    // 데이터를 읽고 쓰고 지우는 작업
    lateinit var db : MemoDatabase
    var memoList = listOf<MemoEntity>() // 항상 관리될 메모 리스트


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // onCreate에서 db 변수 지정
        db = MemoDatabase.getInstance(this)!! // 싱글톤 객체를 가져온다

        // Listener. 이 버튼이 계속 클릭을 하는지 아닌지 듣고 있다.
        // 클릭을 하면 안의 코드블럭이 실행됨
        button_add.setOnClickListener {
            // 어떤 메모인지 넘겨준다 (객체를 만들어 줌)
            val memo = MemoEntity(null, edittext_memo.text.toString())
            insertMemo(memo)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    // 1. Insert Data
    // 2. Get Data
    // 3. Delete Data
    // 4. Set RecyclerView

    // Android에서는 Lint를 통해 성능상 문제가 있을 수 있는 코드를 관리해준다.
    // (AsyncTask 때문에 메모리 누수가 일어날 수 있었는데, 해당 어노테이션을 통해 경고를 무시할 수 있음)
    // (최상단 class 차원으로 옮김)

    fun insertMemo(memo : MemoEntity){
        // 1. MainThread vs WorkerThread(Background Thread)
        // 모든 화면(UI)과 관련된 작업들은 MainThread에서 이뤄져야 한다
        // 모든 데이터 통신과 관련된 일들은 WorkerThread에서 이뤄져야 한다 (API 통신, DB CRUD)

        val insertTask = (object : AsyncTask<Unit, Unit, List<MemoEntity>>(){
            override fun doInBackground(vararg p0: Unit?): List<MemoEntity>? {
                db.memoDAO().insert(memo)
                return memoList
            }

            override fun onPostExecute(result: List<MemoEntity>?) {
                super.onPostExecute(result)
                getAllMemos()
            }
        }).execute()
    }

    // AsyncTask : 백그라운드 작업을 도와주는 추상클래스 (오버라이드 되었으니까)

    fun getAllMemos(){
        val getTask = (object : AsyncTask<Unit,Unit,Unit>(){

            override fun doInBackground(vararg p0: Unit?) {
                memoList = db.memoDAO().getAll()
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                setRecyclerView(memoList)
            }
        }).execute()
    }

    fun deleteMemo(){

    }

    fun setRecyclerView(memoList: List<MemoEntity>) {
        recyclerView.adapter = MyAdapter(this, memoList)
    }
}