package com.example.bookapiapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import android.support.v4.content.AsyncTaskLoader;

public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<String>{

    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;

    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Initialize those variable to view in OnCreate

        mBookInput=(EditText)findViewById(R.id.bookInput);
        mTitleText=(TextView) findViewById(R.id.titleText);
        mAuthorText=(TextView) findViewById(R.id.authorText);

        if(getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }
    public void searchBooks(View view){

//      Get the search string from the input field
        String queryString=mBookInput.getText().toString();


//hides the keyboard when the user taps the button.
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null ) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

//       Check the network connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
            new FetchBook(mTitleText, mAuthorText).execute(queryString);
            mAuthorText.setText("");
            mTitleText.setText(R.string.loading);
        }else {
            if (queryString.length() == 0) {
                mAuthorText.setText("");
                mTitleText.setText(R.string.no_search_term);
            } else {
                mAuthorText.setText("");
                mTitleText.setText(R.string.no_network);
            }
        }
        Bundle queryBundle = new Bundle();
        queryBundle.putString("queryString", queryString);
        getSupportLoaderManager().restartLoader(0, queryBundle, this);
        //                launch the background task with the execute() method and the query string.
//        new FetchBook(mTitleText, mAuthorText).execute(queryString);

//        change the title TextView to a loading message and clear the author TextView.
        mAuthorText.setText("");
        mTitleText.setText(R.string.loading);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";

        if (args != null) {
            queryString = args.getString("queryString");
        }

        return new BookLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            //obtain the JSON array of items from the result string.
            JSONObject jsonObject=new JSONObject(data);

            JSONArray itemsArray=jsonObject.getJSONArray("items");
//Iterate through the itemsArray array, checking each book for title and author information
            int i=0;
            String title=null;
            String authors=null;

            while (i<itemsArray.length()&& (authors==null &&title==null)){
//                Get the current item information
                JSONObject book=itemsArray.getJSONObject(i);
                JSONObject volumeInfo=book.getJSONObject("volumeInfo");

//                Try to get the title and author from the current item
//                catch if either field is emptu and move on
                try{
                    title=volumeInfo.getString("title");
                    authors=volumeInfo.getString("authors");
                }catch (Exception e){
                    e.printStackTrace();
                }
//                Move to the next item
                i++;
            }
            if (title != null && authors != null) {
                mTitleText.setText(title);
                mAuthorText.setText(authors);
            }else {
                mTitleText.setText(R.string.no_results);
                mAuthorText.setText("");
            }

        } catch (JSONException e) {
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.
            mTitleText.setText(R.string.no_results);
            mAuthorText.setText("");
            e.printStackTrace();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}