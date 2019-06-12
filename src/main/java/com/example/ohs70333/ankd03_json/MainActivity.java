package com.example.ohs70333.ankd03_json;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/************************************************************************************
 JSON_Data Access Sample Program  (HttpURLConnection)
 WeatherHacks Access
 Sumida
 ************************************************************************************/
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MyAsyncTask myAsync;               									// Async_Task定義
    ProgressDialog dialog;              								// Progress_Dialog
    EditText edTitle;                                                // 受信Title
    Button btSearch;											// 受信内容
    TextView tvDesc;
    TextView tvTitle;
    String strTitle = null;
    String strDescription = null;

    //*************
    // onCreate処理
    //*************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edTitle = (EditText) findViewById(R.id.edTitle);
        btSearch = (Button) findViewById(R.id.btSearch);
        tvTitle = (TextView)findViewById(R.id.tvtitle);
        tvDesc = (TextView)findViewById(R.id.tvDesc);

        //** Button onClickListener **
        btSearch.setOnClickListener(this);			// ButtonのClick_Listener

    }

    @Override
    public void onClick(View v) {
        String title = edTitle.getText().toString();
        myAsync = new MyAsyncTask();									//AreaCodeを引数にAsync_Taskを起動
        myAsync.execute(title);
    }
    //*********************************
    // Async_Task処理(for WEB_Data Get)
    //*********************************
    //** 引数： AsyncTask<引数型1, 引数型2, 引数型3>
    //   引数型1 = Activity→doInBackground への引数型
    //   引数型2 = onProgressUpdate への引数型
    //   引数型3 = onPostExecute への引数型 = doInBackgroundからの戻り値型 **
    class MyAsyncTask extends AsyncTask<String, Void, String> {
        //** Pre処理 **
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);   	// Set Progress_dialog
            dialog.setTitle("DATA取得中");
            dialog.setMessage("暫くお待ちください...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);		// Spinner_Type = 丸回転
            dialog.setCancelable(true);                       			// Cancel有効
            dialog.show();                                      		// Progress_Dialog表示
        }
        //** Json_Data Access実行処理(HttpURLConnection) **
        @Override
        protected String doInBackground(String... title) {  			// String...は可変個引数, areaCodeはActivityからの引数(配列)
            HttpURLConnection urlCon = null;
            URL url = null;
            String jsonText = null;
            String urlReq = "http://wikipedia.simpleapi.net/api?keyword="+ title + "&output=json";  // URL

            try {
                url = new URL(urlReq);                            		// Adjust URL form
                urlCon = (HttpURLConnection) url.openConnection();    	// URLに接続(引数 url[0]=接続するURL文字列)

                jsonText = IOUtils.toString(urlCon.getInputStream());   // 接続先の画面StreamをGetしStringに変換

                try {
                    Thread.sleep(10000);                             		// 1sec待機(Spinnerが表示されている事を確認の為, 本来は不要)
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {                         // (IOUtils導入要)
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {													// (接続が成功しようがしまいが....)
                if (urlCon != null) {
                    urlCon.disconnect();                                // URL接続断
                }
            }
            try {
                Thread.sleep(100000);                             		// 1sec待機(Spinnerが表示されている事を確認の為, 本来は不要)
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            return jsonText;											// Json_DataをReturn(onPostExecuteの引数になる)
        }

        //** Post処理(URL_Access終了) **
        protected void onPostExecute(String jsonText) {
            JSONObject json_Object;
            try {
                json_Object = new JSONObject(jsonText);                	// jsonTextの全部をJSONObjectに格納
                strTitle = json_Object.getString("title");      		// 構造の1段目にある"title"部を抜き出す
                //json_Object = new JSONObject(json_Object.getString("body")); // 構造の1段目にある"description"部を格納し
                strDescription = json_Object.getString("body"); 		// 2段目にある"text"部を抜き出す
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Log.v("Masa_Json",jsonText);
            dialog.dismiss();                                          	// Progress_Dialog終了
            tvTitle.setText(strTitle);                             	// JSON Dataを TextViewにSet →表示
            tvDesc.setText(strDescription);
        }
        //** Cancel処理 **
        protected void onCancelled() {
            dialog.dismiss();                                         	// Progress_Dialog終了
        }
    }
}
