package javierorti.ioc.gestioncitascheap_cut;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String nom,idPelu,municipio,direccion,idPeluqueria,nombrePelu,str,id,persona,personaTurn;
    int turnosElegidos,maxiTurnos,turnoToca;
    ImageView patronPelu;
    TextView turno,cita,datosPelu,personaTurno;
    EditText numTurnos;
    ListView peluqueria;
    ArrayList<String> idPel;
    ArrayList<String> listaPelus;
    List<String> nombres;
    List<Integer>turnoSiguiente;
    ArrayAdapter adlista;
    RequestQueue requestQueue;
    int contador=0;
    Handler handler = new Handler();
    private final int TIEMPO = 5000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        patronPelu=(ImageView)findViewById(R.id.imageView2);
        datosPelu=(TextView)findViewById(R.id.nombrePelu);
        turno=(TextView)findViewById(R.id.turno);
        cita=(TextView)findViewById(R.id.cita);
        numTurnos=(EditText)findViewById(R.id.NumTurnos);
        personaTurno=(TextView) findViewById(R.id.personaTurno);personaTurno.setVisibility(View.GONE);
        peluqueria=(ListView)findViewById(R.id.peluqueria);
        str = getIntent().getStringExtra("titulPelu2");
        peluqueria.setVisibility(View.GONE);numTurnos.setVisibility(View.GONE);
        cargaPelus("https://cheapcut.000webhostapp.com/appCheapCut/pelus.php");

        //Todo:actualizar cada 2 segundos:
        handler.postDelayed(new Runnable() {
            public void run() {
                handler.postDelayed(this, TIEMPO);
                actualizar("https://cheapcut.000webhostapp.com/appCheapCut/actualizarTurno.php");
            }
        }, TIEMPO);


        if (str == null ) {
            listaPelus("https://cheapcut.000webhostapp.com/appCheapCut/peluqueria.json");
            peluqueria.setVisibility(View.VISIBLE);numTurnos.setVisibility(View.VISIBLE);
            patronPelu.setVisibility(View.GONE);cita.setVisibility(View.GONE);turno.setVisibility(View.GONE);


        } else {
            peluqueria.setVisibility(View.GONE);
            patronPelu.setVisibility(View.VISIBLE);cita.setVisibility(View.VISIBLE);turno.setVisibility(View.VISIBLE);
            str=getIntent().getExtras().getString("titulPelu2");
            idPeluqueria=getIntent().getExtras().getString("idPelu2");
            turnosElegidos=getIntent().getExtras().getInt("turnosElegidos");
            personaTurno.setVisibility(View.VISIBLE);
            int siguiente=getIntent().getExtras().getInt("turnoSiguiente");

           // personaTurno.setText("Próximo turno : "+siguiente+ " de "  +getIntent().getExtras().getString("personaTurno"));

            Toast.makeText(this, "Gracias por confiar en nosotros.", Toast.LENGTH_SHORT).show();
            datosPelu.setText(str);
        }
        //Scroll en el listado de peluquerias

        idPel=new ArrayList<>();
        listaPelus = new ArrayList<>();
        rotarImagen(cita);rotarImagen(turno);

        turno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent turno=new Intent(MainActivity.this,Turno.class);
                turno.putExtra("idPelu",idPeluqueria);
                turno.putExtra("titulPelu",str);
                turno.putExtra("turnosElegidos",turnosElegidos);
                startActivity(turno);
            }
        });
        cita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patronPelu.setImageDrawable(getResources().getDrawable(R.drawable.codigo));
                handler.postDelayed(new Runnable() {
                    public void run() {
                        handler.postDelayed(this, TIEMPO);
                        patronPelu.setImageDrawable(getResources().getDrawable(R.drawable.logo));
                    }
                }, 10000);


//                Intent cita=new Intent(MainActivity.this,Cita.class);
//                startActivity(cita);
//                finish();
            }
        });
        peluqueria.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        //Todo:seleccionar peluqueria en el array List
        peluqueria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                str = listaPelus.get(position);
                datosPelu.setText(str);
                peluqueria.setVisibility(view.GONE);numTurnos.setVisibility(View.GONE);
                turnosElegidos=Integer.parseInt(numTurnos.getText().toString());
                Toast.makeText(MainActivity.this, "turnos máximos :"+numTurnos.getText().toString(), Toast.LENGTH_SHORT).show();
                idPeluqueria=idPel.get(position);
                UpdateTurnoPelu("https://cheapcut.000webhostapp.com/appCheapCut/updateTurnoPelu.php");
                patronPelu.setVisibility(View.VISIBLE);cita.setVisibility(View.VISIBLE);turno.setVisibility(View.VISIBLE);
            }
        });
    }

    //Todo: listado de Peluquerias de la base de datos MYSQL
    public void listaPelus(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        nom = jsonObject.getString("nombrePe");
                        idPelu = jsonObject.getString("id_peluqueria");
                        municipio = jsonObject.getString("municipio");
                        direccion = jsonObject.getString("direccion");
                        idPel.add(idPelu);
                       String  textoCompleto  =nom + " " + municipio+ " " +direccion  ;
                            listaPelus.add(textoCompleto);//ArrayLisT
                            adlista = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,listaPelus);
                            peluqueria.setAdapter(adlista);//ListView
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se pudo obtener listado" + nom, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
    //Todo: listado de Peluquerias de la base de datos MYSQL
    public void cargaPelus(String URL){
        StringRequest cargaPelus=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se ha cargado las peluquerías,revise conexión", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> para=new HashMap<String,String>();
                return para;
            }
        };
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(cargaPelus);
    }
    //Todo:Actualiza turno en peluqueria BD

    public void UpdateTurnoPelu(String URL){
        StringRequest registroTurno=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, idPeluqueria, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Turnos no Actulizados!!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> para=new HashMap<String,String>();
                String t=String.valueOf(turnosElegidos);
                para.put("turnoElegido",t);
                para.put("idPelFK",idPeluqueria);
                return para;
            }
        };
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(registroTurno);
    }

//    Todo:actulizar contenido de los turnos:

    public void actualizar(String URL){

        StringRequest registroTurno=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                actualizaTurnoJson("https://cheapcut.000webhostapp.com/appCheapCut/turno.json");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

        };
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(registroTurno);

    }


    private   void actualizaTurnoJson(String URL){

        final JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null ;
                nombres=new ArrayList<>();turnoSiguiente=new ArrayList<>();


                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        int maxiTur = jsonObject.getInt("numTurno");
                        id=jsonObject.getString("id_peluqueria_FK");

                        if(id.equals(idPeluqueria)){

                            maxiTurnos=maxiTur;
                            personaTurn=jsonObject.getString("nombre");
                            turnoToca=jsonObject.getInt("numTurno");
                            turnoSiguiente.add(turnoToca);
                            nombres.add(personaTurn);
                            persona=nombres.get(0);

                            turnoToca=turnoSiguiente.get(0);
                            personaTurno.setVisibility(View.VISIBLE);
                            personaTurno.setText("Turno Nº "+turnoToca+ " - "  +persona);


                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                personaTurno.setText("No existen más turnos.");
            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


    @Override
    public void onBackPressed() {
        if(contador==0){
            Toast.makeText(getApplicationContext(), "pulse para salir", Toast.LENGTH_SHORT).show();
                contador++;
        }else{
            super.onBackPressed();
        }
        new CountDownTimer(3000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
contador=0;
            }
        }.start();
    }

    private void rotarImagen(View view){
//        RotateAnimation animation = new RotateAnimation(0, 65,
//            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//        animation.setDuration(3000);
//        animation.setRepeatCount(Animation.INFINITE);
//        animation.setRepeatMode(Animation.REVERSE);
//        cita.startAnimation(animation);
        turno.setTextColor(Color.parseColor("#B29C14"));
        cita.setTextColor(Color.parseColor("#B29C14"));
        Animation anim = new AlphaAnimation(0.5f, 1.0f);
        anim.setDuration(1000); //You can manage the time of the blink with this parameter
        anim.setStartOffset(1000);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }
}
