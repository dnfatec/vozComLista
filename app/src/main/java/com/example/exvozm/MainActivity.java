package com.example.exvozm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,
        AdapterView.OnItemClickListener {
private EditText edTexto;
private Button btOuvir, btFalar, btVozes;
private ListView lstv;
private TextToSpeech textoFala; //objeto utilizado para trabalhar com voz
private Locale locale; //Objeto que ira determinar qual idioma ira ser o padrao
private ArrayList<String> vozes= new ArrayList<String>(); //Ira armazenar numa lista de string
    //O nome das vozes que há no dispositivo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textoFala = new TextToSpeech(MainActivity.this, MainActivity.this);
        carregaWidgets();
        botoes();
        lstv.setOnItemClickListener(this);
    }
    //Carrega widgets
    private void carregaWidgets()
    {
        edTexto=(EditText)findViewById(R.id.edtTexto);
        lstv=(ListView)findViewById(R.id.lstPalavras);
        btFalar=(Button)findViewById(R.id.btnFalar);
        btOuvir=(Button)findViewById(R.id.btnOuvir);
        btVozes=(Button)findViewById(R.id.btnBuscarVozes);
    }

    private void  botoes()
    {
        btFalar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = edTexto.getText().toString();
                textoFala.speak(texto, TextToSpeech.QUEUE_FLUSH, Bundle.EMPTY, "1");

            }
        });
        btOuvir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(new
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
                if (activities.size() !=0){
                    Intent intent = getRecognizerIntent();
                    startActivityForResult(intent,0);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Sem mic  teste branch", Toast.LENGTH_LONG).show();
                }
            }
        });

        btVozes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarVozes();
            }
        });

    }
    //Coloca as vozes numa lista de String e depois no listview
    private void listarVozes(){
        for (Voice v : textoFala.getVoices()) {
            if (v.getLocale().getLanguage().contains("pt")) {
                vozes.add(v.getName().toString());
                }
            }
        lstv.setAdapter(new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, vozes));

        }

    @Override
    public void onInit(int status) {
        locale = new Locale("pt", "BR");
        textoFala.setLanguage(locale);
    }

    protected Intent getRecognizerIntent(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale aqui");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, "10");
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArrayList<String> palavras =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            lstv.setAdapter(new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, palavras));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String voz = vozes.get(position);//pega o item selecionado
        Toast.makeText(getApplicationContext(), voz, Toast.LENGTH_LONG).show();
        Voice tipodaVoz = new Voice(voz,
                      Locale.getDefault(), 1, 1, false, null);
        //Instancia um objeto do tipo da voz
        textoFala.setVoice(tipodaVoz);
        //Atribui o tipo de voz no objeto textoFala
    }
}
