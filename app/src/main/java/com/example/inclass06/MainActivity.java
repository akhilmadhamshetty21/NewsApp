package com.example.inclass06;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

//import com.squareup.picasso.Picasso;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String items;
    Button goButton;
    Spinner sl;
    ProgressBar progressBar;
    int index=0;
    ArrayList<News> news = new ArrayList<>();
    ImageView nextImage,prevImage;
    private static final String TAG = "demo";
    String baseURL = "http://newsapi.org/v2/top-headlines";
    ImageView imageView;
    TextView description,createddate,title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //imageView = findViewById(R.id.imageView);
        items=null;
        goButton= findViewById(R.id.gobutton);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        sl=findViewById(R.id.itemspinner);
        nextImage=findViewById(R.id.nxtImage);
        prevImage=findViewById(R.id.prevImage);


        sl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: "+item);
                new GetJSONData().execute(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.d(TAG, "onCreate: "+sl.getPrompt());
        description=findViewById(R.id.description);
        createddate=findViewById(R.id.createDate);
        title=findViewById(R.id.newstitle);
        imageView=findViewById(R.id.imageView);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sl.setAdapter(adapter);
//        goButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//            }
//        });
        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index==news.size()-1){
                    index = 0;
                    Picasso.get().load(news.get(index).imageURL).into(imageView);
                    title.setText(news.get(index).title);
                    description.setText(news.get(index).Description);
                    createddate.setText(news.get(index).publishedAt);
                }
                else {
                    index=index +1;
                    Picasso.get().load(news.get(index).imageURL).into(imageView);
                    title.setText(news.get(index).title);
                    description.setText(news.get(index).Description);
                    createddate.setText(news.get(index).publishedAt);
                }
            }
        });
        prevImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index==0){
                    index=news.size()-1;
                    Picasso.get().load(news.get(index).imageURL).into(imageView);
                    title.setText(news.get(index).title);
                    description.setText(news.get(index).Description);
                    createddate.setText(news.get(index).publishedAt);
                }
                else {
                    index=index-1;
                    Picasso.get().load(news.get(index).imageURL).into(imageView);
                    title.setText(news.get(index).title);
                    description.setText(news.get(index).Description);
                    createddate.setText(news.get(index).publishedAt);
                }
            }
        });


    }

    class GetJSONData extends AsyncTask<String, Void, ArrayList<News>>{

        @Override
        protected ArrayList<News> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            try {
                String url = baseURL + "?"
                        + "apikey=" + getResources().getString(R.string.news_key)
                        + "&" + "category=" + strings[0];
                Log.d(TAG, "doInBackground: ==================================================");
                news.clear();

                URL url1 = new URL(url);
                connection = (HttpURLConnection) url1.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray articles = root.getJSONArray("articles");
                    for (int i=0;i<articles.length();i++) {
                        JSONObject articleJson = articles.getJSONObject(i);

                        News newsItem = new News();

                        newsItem.title = articleJson.getString("title");
                        newsItem.imageURL = articleJson.getString("urlToImage");
                        newsItem.Description = articleJson.getString("description");
                        newsItem.publishedAt = articleJson.getString("publishedAt");
                        Log.d(TAG, "doInBackground: "+newsItem.title);
                        news.add(newsItem);
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return news;
        }

        @Override
        protected void onPostExecute(ArrayList<News> news) {
            super.onPostExecute(news);
            if(news!=null) {
                Picasso.get().load(news.get(0).imageURL).into(imageView);
                title.setText(news.get(0).title);
                description.setText(news.get(0).Description);
                createddate.setText(news.get(0).publishedAt);
            }
            for (News newsItem: news) {
//                Log.d(TAG, "Article: " + newsItem.title);
            }
        }
    }
}