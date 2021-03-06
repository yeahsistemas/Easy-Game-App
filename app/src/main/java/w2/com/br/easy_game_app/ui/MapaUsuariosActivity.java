package w2.com.br.easy_game_app.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import w2.com.br.easy_game_app.ListAdapter.ListAdapterJogador;
import w2.com.br.easy_game_app.R;
import w2.com.br.easy_game_app.async.GenericAsyncTask;
import w2.com.br.easy_game_app.entity.Atualizavel;
import w2.com.br.easy_game_app.entity.Equipe;
import w2.com.br.easy_game_app.entity.Usuario;
import w2.com.br.easy_game_app.enuns.Method;
import w2.com.br.easy_game_app.enuns.TipoPosicao;

public class MapaUsuariosActivity extends FragmentActivity implements OnMapReadyCallback, Atualizavel {

    private GoogleMap mMap;
    private Marker marker;
    private Spinner sp;
    ArrayAdapter<TipoPosicao> adapter;
    private final String servicoMapa = "usuario/coordenadas/1";
    private List<Usuario> jogadores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_usuarios);

        SupportMapFragment mapaFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa_usuarios));
        mapaFragment.getMapAsync(this);

        //spinner posição
        adapter = new ArrayAdapter<TipoPosicao>(this, android.support.design.R.layout.support_simple_spinner_dropdown_item, TipoPosicao.values());
        adapter.setDropDownViewResource(android.support.design.R.layout.support_simple_spinner_dropdown_item);

        //chamar coordenadas de jogadores
        new GenericAsyncTask(MapaUsuariosActivity.this, this, Method.GET, String.format("%s", servicoMapa)).execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        LatLng sydney = new LatLng(-25.358840, -49.214756);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("(41)99221257"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));

        LatLng sydney = new LatLng(-25.358840, -49.214756);

//        mMap.addMarker(new MarkerOptions().position(sydney).title("(41)99221257"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));

//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));
//        mMap.setMapType(0);

//        customMarker(new LatLng(-25.358840, -49.214756), "Marcador 1", "title marcador 1");
//        customMarker(new LatLng(-25.358832, -49.214740), "Marcador 2", "title marcador 2");
        //EVENTS
//        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                Log.i("Script", "setOnCameraChangeListner");
//                if(marker!= null){
//                    marker.remove();
//                }
//                customMarker(new LatLng(cameraPosition.target.latitude,cameraPosition.target.longitude),"1: MAracador ALterado","O markador foi reposicionado");
//
//            }
//        });

//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                Log.i("Script", "setOnMapClickListener");
//                if (marker != null) {
//                    marker.remove();
//                }
//                customMarker(new LatLng(latLng.latitude, latLng.longitude), "2: MAracador ALterado", "O markador foi reposicionado");
//
//            }
//        });
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Log.i("Script", "3: Marker: " + marker.getTitle());
//
//                return false;
//            }
//        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i("Script", "4: Marker: " + marker.getTitle());
                LayoutInflater inflater = getLayoutInflater();

                final View dialoglayout = inflater.inflate(R.layout.activity_invite, null);


                sp = (Spinner) dialoglayout.findViewById(R.id.sp_posicao_invite);
                sp.setAdapter(adapter);
                Button btnSendMail = (Button) dialoglayout.findViewById(R.id.btnConvite);
                btnSendMail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText ttNome = (EditText) dialoglayout.findViewById(R.id.nome_usuario);
                        EditText ttApelido = (EditText) dialoglayout.findViewById(R.id.apelido_usuario);
                        TipoPosicao tipoPosicao = (TipoPosicao) sp.getSelectedItem();
                        Usuario usuario = new Usuario();
                        usuario.setTipoPosicao(tipoPosicao);
                        if (ttNome != null) {
                            String nome = ttNome.getText().toString();
                            usuario.setNome(nome);
                        }
                        if (ttApelido != null) {
                            String apelido = ttApelido.getText().toString();
                            usuario.setApelido(apelido);
                        }
                        if (usuario.getTipoPosicao() != null) {
                            Toast.makeText(getApplicationContext(), "Você é fodão! ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                AlertDialog.Builder builder = new AlertDialog.Builder(MapaUsuariosActivity.this);
                builder.setView(dialoglayout);
                builder.show();
            }
        });
    }

    public void customMarker(LatLng latLng, String title, String snippet) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet);

        //criar o marker
        marker = mMap.addMarker(options);

    }
    public void customMarkers(List<Usuario>jds) {
        LatLng sydney = new LatLng(-25.358840, -49.214756);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));
        mMap.setMapType(0);
        for (Usuario usuario : jds) {
            LatLng latLng = new LatLng(usuario.getLatitude(), usuario.getLongitude());
            String dados = String.format("%s %s %s", usuario.getApelido(), usuario.getTelefone(), usuario.getTipoPosicao().getDescricao());
            MarkerOptions options = new MarkerOptions();
            options.position(latLng).title(dados).snippet(dados);

            //criar o marker
            mMap.addMarker(options);
        }


    }

    @Override
    public void atualizar(JSONObject jsonObject) throws JSONException {
        jogadores = new ArrayList<>();
        JSONObject retorno = null;
        if (jsonObject.has("objeto")) {
            retorno = new JSONObject(jsonObject.getString("objeto"));
            try {
                if (retorno.has("objeto")) {
                    JSONObject objeto = retorno.getJSONObject("objeto");
                    JSONArray array = objeto.getJSONArray("coordenadas");
                    for (int i = 0; i < array.length(); i++) {
                        jogadores.add(Usuario.toCoordenadasUsuario(array.getJSONObject(i)));
                    }
                    customMarkers(jogadores);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (jsonObject.has("erro")) {
            //TODO Toast;
            Toast.makeText(this, retorno.get("erro").toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
