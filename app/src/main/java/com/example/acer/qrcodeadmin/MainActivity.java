package com.example.acer.qrcodeadmin;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnScan;
    Activity activity;
   Spinner stession;
    Spinner type;
    String StationName;
    String bType;
    TextView tvInfo;
    int endingIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       activity=this;
        btnScan=(Button)findViewById(R.id.btnScan);
        stession=(Spinner)findViewById(R.id.spinnerStesison);
        type=(Spinner)findViewById(R.id.spType);
        tvInfo=(TextView)findViewById(R.id.tvInfo);

        btnScan.setOnClickListener(this);
        PopulateSpinner pspinner=new PopulateSpinner();
        pspinner.type(this,type);
        endingIndex=0;
        JSONStation();

    }
    final String URLJSON = Constans.URL+"JsonStation.php";
    private void JSONStation() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLJSON,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        putToSpinner(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.toString(), Toast.LENGTH_LONG ).show();
                    }
                }){

            @Override

            protected Map<String, String> getParams() throws AuthFailureError //ni kita pass parameter dekat html
            {
                Map<String,String> map = new HashMap<String,String>();
                //  map.put("nama post",parameter);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    List<String> spinnerArray =  new ArrayList<String>();
    private void putToSpinner(String response) {
        try {
            JSONObject jsonRootObject = new JSONObject(response);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("result");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int id = Integer.parseInt(jsonObject.optString("ID").toString());
                String Station = jsonObject.optString("StationName").toString();


                spinnerArray.add(Station);
              //  System.out.println(Coursename);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stession.setAdapter(adapter);

        } catch (JSONException e) {e.printStackTrace();}
    }



    @Override
    public void onClick(View v) {
        StationName=(String)stession.getSelectedItem();
        bType=(String)type.getSelectedItem();
        endingIndex=stession.getSelectedItemPosition();
        if(v==btnScan) {

            Toast.makeText(MainActivity.this,""+endingIndex ,Toast.LENGTH_LONG).show();
            IntentIntegrator integrator = new IntentIntegrator(activity);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null){
            if(result.getContents()==null){
                Toast.makeText(MainActivity.this,"Scan Cancel",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this,result.getContents(),Toast.LENGTH_LONG).show();
                if(bType.equalsIgnoreCase("boarding")){
               Boarding(result.getContents());
                }
                else{
                    Ending(result.getContents());

                }
            }
        }
        else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
    final String URL2 = Constans.URL+"Ending.php";
    private void Ending(final String ID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL2,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        tvInfo.setText(response);
                        //  tvInfo.setText("The vault is open  \n The fare is RM"+response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){

            @Override

            protected Map<String, String> getParams() throws AuthFailureError //ni kita pass parameter dekat html
            {
                Map<String,String> map = new HashMap<String,String>();
                    map.put("User_id",ID);
                map.put("EndingStation",StationName);
                map.put("index", String.valueOf(endingIndex));

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    final String URL = Constans.URL+"Boarding.php";
    private void Boarding(final String ID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if(response.equalsIgnoreCase("Insufficient Credit")){
                            tvInfo.setText("Your credit is below RM5 \n The vault is close");
                        }else {
                            tvInfo.setText("The vault is open  \n Your balance is RM" + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){

            @Override

            protected Map<String, String> getParams() throws AuthFailureError //ni kita pass parameter dekat html
            {
                Map<String,String> map = new HashMap<String,String>();
                map.put("User_id",ID);
                map.put("Boarding_Station",StationName);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    };
}
