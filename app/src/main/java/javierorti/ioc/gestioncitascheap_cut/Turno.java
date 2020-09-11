package javierorti.ioc.gestioncitascheap_cut;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.FieldClassification;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class Turno extends AppCompatActivity {
    TextView informa;
    Button si,no;
    EditText tlf,nom;
    String mensaje,id,n,idPel,titulPelu,idPeluqueria,personaTurno,persona;
    int numTu,turnosElegidos,turnoToca;
    int turnoReal,maxiTurnos;
    RequestQueue requestQueue;
    List<String> nombres = new ArrayList<>();
    List<Integer>turnoSiguiente=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turno);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        informa=(TextView)findViewById(R.id.text_informa);
        informa.setTextColor(Color.parseColor("#B29C14"));
        informa.setText("Desea recibir SMS para notificarle su turno próximamente?");
        no=(Button)findViewById(R.id.btn_no);
        si=(Button)findViewById(R.id.btn_si);
        tlf=(EditText)findViewById(R.id.editTlf);tlf.setVisibility(View.GONE);
        nom=(EditText)findViewById(R.id.editTextNombre);nom.setVisibility(View.GONE);
        idPel=getIntent().getExtras().getString("idPelu");
        titulPelu=getIntent().getExtras().getString("titulPelu");
        titulPelu=getIntent().getExtras().getString("titulPelu");
        turnosElegidos=getIntent().getExtras().getInt("turnosElegidos");

       comprobarUltimoTurno("https://cheapcut.000webhostapp.com/appCheapCut/turno.json");


        //TODO:si dice que SI al envio de SMS:
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informa.setVisibility(View.GONE);
                no.setVisibility(View.GONE);si.setVisibility(View.GONE);
                nom.setVisibility(View.VISIBLE);tlf.setVisibility(View.VISIBLE);
                tlf.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        if(tlf.getText().length() > 8 && tlf.getText().length()<10 ) {
                            no.setVisibility(View.VISIBLE);si.setVisibility(View.VISIBLE);
                            nom.setVisibility(View.GONE);tlf.setVisibility(View.GONE);
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);//oculta teclado
                            imm.hideSoftInputFromWindow(tlf.getWindowToken(), 0);
                            maxiTurnos=maxiTurnos+1;
                            informa.setText( nom.getText() +" su Nª de turno :"+maxiTurnos+".\n "+"Recibirá un SMS para que vuelva a su peluquería al "
                                +tlf.getText()+". \nPor favor compruebe su nombre y teléfono sean correctos.");
                            si.setText("Aceptar");no.setText("Modificar");
                            informa.setVisibility(View.VISIBLE);
                            si.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    no.setVisibility(View.GONE);si.setVisibility(View.GONE);
                                    if(nom.getText().length()==0 || tlf.getText().length()==0){
                                        no.setVisibility(View.VISIBLE);si.setVisibility(View.VISIBLE);
                                        informa.setText("Compruebe nombre y telefono necesarios para el  envio SMS. Gracias");
                                    }else{
                                        comprobarCita("https://cheapcut.000webhostapp.com/appCheapCut/turno.json");
                                        resetActivity("Gracias por su atención!!:)");
                                    }
                                }
                            });
                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    no.setVisibility(View.GONE);si.setVisibility(View.GONE);
                                    informa.setVisibility(View.GONE);
                                    nom.setVisibility(View.VISIBLE);tlf.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                        if(tlf.getText().length()>=10){
                            Toast.makeText(getApplicationContext(),"Teléfono demasiado largo", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

        //TODO:si dice que NO al envio de SMS:
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nom.setVisibility(View.GONE);tlf.setVisibility(View.GONE); informa.setVisibility(View.VISIBLE);
                no.setVisibility(View.GONE);
                informa.setTextColor(Color.parseColor("#B29C14"));
                maxiTurnos=maxiTurnos+1;
                informa.setText("Puede esperar a su turno  con el  Nª: "+maxiTurnos+"\n Muchas gracias por su atención:)");
                mensaje="Su Nª de turno :x  en breve sera atendid@\n Gracias por su atención";
                no.setVisibility(View.GONE);

                si.setText("aceptar");
                si.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comprobarCita("https://cheapcut.000webhostapp.com/appCheapCut/turno.json");
                        resetActivity("Gracias por su atención!!:)");
                    }
                });


            }
        });

    }

//Todo:solicitar turno a BD

public void turno(String URL){
    StringRequest registroTurno=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Toast.makeText(getApplicationContext(), "Turno solicitado!!", Toast.LENGTH_SHORT).show();
            resetActivity("Turno solicitado.");
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Turno no solicitado!!", Toast.LENGTH_SHORT).show();
        }
    }){
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String,String> para=new HashMap<String,String>();
            para.put("nom",nom.getText().toString());
            para.put("tlf",tlf.getText().toString());
            para.put("idPelFK",idPel);
            if(turnoReal==0){turnoReal=+1;}
            String tur= String.valueOf(turnoReal);
            para.put("turno",tur);
            return para;
        }
    };
    requestQueue= Volley.newRequestQueue(this);
    requestQueue.add(registroTurno);
}


//TODO:Comprobar citas:
    int numTurnos=1;
    private   void comprobarCita(String URL){
        final JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null ;
                for (int i = 0; i < response.length(); i++) {
                    try {
                    jsonObject = response.getJSONObject(i);
                    id = jsonObject.getString("id");
                    idPeluqueria=jsonObject.getString("id_peluqueria_FK");
                    numTu=jsonObject.getInt("numTurno");
                    if(idPel.equals(idPeluqueria)){
                        turnoReal=numTu+1;
                        numTurnos++;
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
                Toast.makeText(Turno.this, String.valueOf(numTurnos), Toast.LENGTH_SHORT).show();
                if(numTurnos>=turnosElegidos+1  &&  idPeluqueria.equals(idPel)){
                    Toast.makeText(Turno.this, "No hay más turnos disponibles para hoy.", Toast.LENGTH_SHORT).show();
                    resetActivity("No hay turno para hoy.");
                }else{
                    turno("https://cheapcut.000webhostapp.com/appCheapCut/turno.php");
                }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            turno("https://cheapcut.000webhostapp.com/appCheapCut/turno.php");
        }
    });
    requestQueue= Volley.newRequestQueue(this);
    requestQueue.add(jsonArrayRequest);
}

    //TODO:Comprobar tunos:
    private   void comprobarUltimoTurno(String URL){
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
                        if(id.equals(idPel)){
                            maxiTurnos=maxiTur;
                            personaTurno=jsonObject.getString("nombre");
                            turnoToca=jsonObject.getInt("numTurno");
                            turnoSiguiente.add(turnoToca);
                            nombres.add(personaTurno);
                            persona=nombres.get(0);
                            turnoToca=turnoSiguiente.get(0);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }



    //TODO:resetear activity con tiempo dejando mensaje:
    public void resetActivity(String m){
        int DURACION=3000;
        informa.setText(m);
        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(Turno.this, MainActivity.class);
                intent.putExtra("idPelu2",idPel);
                intent.putExtra("titulPelu2",titulPelu);
                intent.putExtra("turnosElegidos",turnosElegidos);
                intent.putExtra("personaTurno",persona);
                intent.putExtra("turnoSiguiente",turnoToca);
                startActivity(intent);
            };
        }, DURACION);
    }
}
