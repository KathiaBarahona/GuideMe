package com.example.android.guideme;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import guideMeGraphResources.GraphGuideMe;

public class MainActivity extends AppCompatActivity implements PlaceAdapter.PlaceAdapterOnClickHandler {
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private RecyclerView mRecyclerView;
    private PlaceAdapter mPlaceAdapter;
    private PlaceSpeech mPlaceSpeech;
    private TextView mErrorMessageDisplay;
    private NfcAdapter mNFCAdapter;
    private ProgressBar mLoadingIndicator;
    private GraphGuideMe graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        mNFCAdapter = NfcAdapter.getDefaultAdapter(this);

        //---------------Obtain RecylerView -------------------------------------------------------//
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_place);
        //---------------Obtain Error Message -----------------------------------------------------//
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_message_display);
        //---------------Obtain loader-------------------------------------------------------------//
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        mPlaceSpeech = new PlaceSpeech(this, null);
        if(mNFCAdapter == null){
            mPlaceSpeech.speakPlaceName("Este dispositivo no soporta NFC perroh!");
            finish();
            return;
        }
        if (!mNFCAdapter.isEnabled()) {
            mPlaceSpeech.speakPlaceName("Active el NFC uste");
        
        }
        //to implement a RecyclerView, we need a Layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        //Create a new PlaceAdapter
        mPlaceAdapter = new PlaceAdapter(this);
        //Must set adapter to RecyclerView
        mRecyclerView.setAdapter(mPlaceAdapter);
        loadPlaces();
        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this,mNFCAdapter);

    }

    @Override
    protected void onPause() {

        stopForegroundDispatch(this, mNFCAdapter);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mPlaceSpeech != null){
            mPlaceSpeech.stop();
            mPlaceSpeech.shutdown();

        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    private void handleIntent(Intent intent){
        String action = intent.getAction();
        NFCReader nfcReader = new NFCReader();
        nfcReader.setContext(this);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NFCReader().execute(tag);

            } else {
                System.out.println("Tipo errado");
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NFCReader().execute(tag);
                    break;
                }
            }
        }
    }
    public void loadPlaces() {
        showPlaceDataView();
        new FeatchPlaceTask().execute();
    }

    Toast placeToast;

    @Override
    public void onClick(String placeSelected) {
        //---------------IMPLEMENTATION OF TEXT SPEECH GOES HERE-----------------------//

        mPlaceSpeech.speakPlaceName(placeSelected);
        //----------------------TEMPORAL---------------------------------------------//
        Context context = this;
        if (placeToast != null) {
            placeToast.cancel();
        }
        placeToast = new Toast(this).makeText(context, placeSelected, Toast.LENGTH_SHORT);
        placeToast.show();
    }

    @Override
    public void onDoubleTap(String placeSelected) {
        graph.getShortestPath("CATI", placeSelected);
    }

    public void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public void showPlaceDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    public void buildGraph(String[] places, placeNode[] placeDistances) {
        graph = new GraphGuideMe(places);
        for (int i = 0; i < placeDistances.length; i++) {
            graph.addEdge(placeDistances[i].getOriginPlace(), placeDistances[i].getDestinationPlace(),
                    placeDistances[i].distance, placeDistances[i].direction);
        }

    }

    public class placeNode {
        private String originPlace = "";
        private String destinationPlace = "";
        private int distance = 0;
        private String direction = "";

        public placeNode(String originPlace, String destinationPlace, int distance, String direction) {
            this.originPlace = originPlace;
            this.destinationPlace = destinationPlace;
            this.distance = distance;
            this.direction = direction;
        }

        public String getOriginPlace() {
            return originPlace;
        }

        public String getDestinationPlace() {
            return destinationPlace;
        }

        public int getDistance() {
            return distance;
        }
    }

    public class FeatchPlaceTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
//            if(params.length == 0){
//                return null;
//            }
            //---------------MUST FETCH FROM DB------------//

            //---------------HARDCODED PLACES (TEMPORAL!!!)----------//
            String[] places = {"CATI", "Cafetería",
                    "Edifício 5", "Polideportivo"};
            placeNode[] nodes = new placeNode[9];
            nodes[0] = new placeNode("CATI", "Cafetería", 35, "norte");
            nodes[1] = new placeNode("CATI", "Polideportivo", 69, "noreste");
            nodes[2] = new placeNode("Cafetería", "Polideportivo", 64, "este");
            nodes[3] = new placeNode("Cafetería", "Edifício 5", 40, "norte");
            nodes[4] = new placeNode("Edifício 5", "Cafetería", 40, "sur");
            nodes[5] = new placeNode("Edifício 5", "Polideportivo", 75, "sureste");
            nodes[6] = new placeNode("Polideportivo", "Edifício 5", 75, "noroeste");
            nodes[7] = new placeNode("Polideportivo", "Cafetería", 64, "oeste");
            nodes[8] = new placeNode("Polideportivo", "CATI", 69, "suroeste");
            buildGraph(places, nodes);
            return places;


        }

        @Override
        protected void onPostExecute(String[] strings) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (strings != null) {
                showPlaceDataView();
                mPlaceAdapter.setPlaceData(strings);
            } else {
                showErrorMessage();
            }
        }
    }
}
