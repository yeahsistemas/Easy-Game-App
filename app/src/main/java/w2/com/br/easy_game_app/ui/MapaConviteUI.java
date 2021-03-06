package w2.com.br.easy_game_app.ui;

import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import w2.com.br.easy_game_app.R;
import w2.com.br.easy_game_app.async.GenericAsyncTask;
import w2.com.br.easy_game_app.entity.Atualizavel;
import w2.com.br.easy_game_app.entity.Usuario;
import w2.com.br.easy_game_app.enuns.Method;
import w2.com.br.easy_game_app.enuns.TipoPosicao;

public class MapaConviteUI extends FragmentActivity implements OnMapReadyCallback, Atualizavel {

    private GoogleMap mMap;
    private final String servicoMapa = "usuario/coordenadas/1";
    private List<Usuario> jogadores;
    ArrayAdapter<TipoPosicao> adapter;

    AlertDialog.Builder builder;

    private Spinner sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_convite_ui);

        setUpMapIfNeeded();

        //spinner posição
        adapter = new ArrayAdapter<TipoPosicao>(this, android.support.design.R.layout.support_simple_spinner_dropdown_item, TipoPosicao.values());
        adapter.setDropDownViewResource(android.support.design.R.layout.support_simple_spinner_dropdown_item);
        //chamar coordenadas de jogadores
        new GenericAsyncTask(MapaConviteUI.this, this, Method.GET, String.format("%s", servicoMapa)).execute();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void adicionaModalIconeParaConvite() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
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
                        AlertDialog dialog = builder.create();
                        dialog.hide();


                    }
                });


                builder = new AlertDialog.Builder(MapaConviteUI.this);
                builder.setView(dialoglayout);
                builder.show();
            }
        });
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

                    if (objeto.has("coordenadas")) {
                        JSONArray array = objeto.getJSONArray("coordenadas");
                        for (int i = 0; i < array.length(); i++) {
                            jogadores.add(Usuario.toCoordenadasUsuario(array.getJSONObject(i)));
                            MarkerOptions markerOptions = new MarkerOptions();
                            if (jogadores.get(i).getTipoPosicao().ordinal() == 1) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.goleiro));
                            } else {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.jogador_linha));
                            }
                            markerOptions.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .position(new LatLng(jogadores.get(i).getLatitude(), jogadores.get(i).getLongitude()))
                                    .title(jogadores.get(i).getApelido());
                            mMap.addMarker(markerOptions);
                            adicionaModalIconeParaConvite();
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-25.346312, -49.198559), 14.0f));

                    } else if (objeto.has("convite")) {

                    }

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
