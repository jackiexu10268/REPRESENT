package com.jacquelinejxu.represent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.internal.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RepresentativeProfile extends AppCompatActivity {
    JSONObject jsonPerson;
    Person person = new Person();
    ArrayList<JSONObject> committeesList = new ArrayList<>();
    ArrayList<String> committeesName = new ArrayList<>();
    TextView committee;
    ArrayList<String> billList = new ArrayList<>();
    String concatBill;
    TextView billsSponsored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative_profile);
        ScrollView scrollLayout = (ScrollView) findViewById(R.id.scrollView2);
        ConstraintLayout background = (ConstraintLayout) findViewById(R.id.constraintLayout);

        //if the representative is a republican
        //representativeLayout.setBackgroundColor(Color.RED);
        //else if the representative is a democrat
        //RepresentativeProfile.setBackgroundColor(Color.BLUE);

        ImageView img = findViewById(R.id.imageView6);
        TextView repName = findViewById(R.id.textView13);
        TextView party = findViewById(R.id.textView14);
        committee = findViewById(R.id.textView5);
        TextView email = findViewById(R.id.textView17);
        TextView website = findViewById(R.id.textView18);
        TextView bills = findViewById(R.id.textView6);
        TextView repType = findViewById(R.id.textView8);
        Button home = findViewById(R.id.button3);
        billsSponsored = findViewById(R.id.textView7);



        try {
            jsonPerson = new JSONObject(getIntent().getStringExtra("rep"));
            JSONObject bio = jsonPerson.getJSONObject("bio");

            person.repType = jsonPerson.getString("type");
            person.repType = person.repType.substring(0,1).toUpperCase() + person.repType.substring(1);

            person.firstName = bio.getString("first_name");
            person.lastName = bio.getString("last_name");
            person.name = person.firstName + " " + person.lastName;
            person.party = bio.getString("party");

            JSONObject contact1 = jsonPerson.getJSONObject("contact");
            person.email = contact1.getString("contact_form");
            person.website = contact1.getString("url");

            JSONObject ref = jsonPerson.getJSONObject("references");
            person.memberID = ref.getString("bioguide_id");

            getBills();
            getBillsNames();

            //add bills sponsored
            //add committees

        } catch (JSONException e) {
            e.printStackTrace();
        }

            String repString = "<b>" + person.repType + "</b>";
            repType.setText(Html.fromHtml(repString));
            repType.setPadding(0,8,0,0);
            //repType.setText(person.repType);

            repName.setText(person.name);
            repName.setPadding(0,16,0,0);

            String partyString = "<b>" + person.party + "</b>";
            party.setText(Html.fromHtml(partyString));
            party.setPadding(0,8,0,0);
            //party.setText(person.party);

            if (person.email == null) {
                email.setText("Contact Unavailable");
            } else {
                email.setClickable(true);
                email.setMovementMethod(LinkMovementMethod.getInstance());
                String contact1 = "<a href='"+ person.email +"'> Contact</a>";
                email.setText(Html.fromHtml(contact1));
            }

            website.setClickable(true);
            website.setMovementMethod(LinkMovementMethod.getInstance());
            String web = "<a href='"+ person.website +"'> Website</a>";
            website.setText(Html.fromHtml(web));


            String websiteLine = "Website:" + " " + person.website;
            if (person.sponsored == null) {
                person.sponsored = "None";
            }
            String billsLine = "<b>Number of Bills Sponsored:</b> " + person.sponsored;
            bills.setText(Html.fromHtml(billsLine));
            bills.setPadding(0,8,0,0);
//            String billsLine = "Number of Bills Sponsored:" + " " + person.sponsored;
//            bills.setText(billsLine);

            Log.d("personParty", person.party);

        if (person.party.equals("Republican")) {
            scrollLayout.setBackgroundColor(Color.parseColor("#EF9090"));
            background.setBackgroundColor(Color.parseColor("#EF9090"));
        } else if (person.party.equals("Democrat")) {
            scrollLayout.setBackgroundColor(Color.parseColor("#9DC6E3"));
            background.setBackgroundColor(Color.parseColor("#9DC6E3"));
        }

        String imgUrl = getImgUrl(person.memberID);
        Picasso.with(this).load(imgUrl).into(img);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RepresentativeProfile.this, HomePage.class);
                startActivity(i);
            }
        });
    }

    public String getImgUrl(String memID) {
        char memberIdFirstLetter = memID.charAt(0);
        String strUrl = "http://bioguide.congress.gov/bioguide/photo/" +
                memberIdFirstLetter + "/" + memID + ".jpg";
        return strUrl;
    }

    public void getBillsNames() {
        Log.d("getBillsName", "called");
        String url = "https://api.propublica.org/congress/v1/members/" + person.memberID + "/bills/cosponsored.json";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("getBillsName", "Successfully Called and received response");
                        try {
                            JSONArray results = response.getJSONArray("results");
                            JSONObject resultsObj = results.getJSONObject(0);
                            JSONArray bills = resultsObj.getJSONArray("bills");

                            for (int i = 0; i < bills.length(); i++){
                                JSONObject currObj = bills.getJSONObject(i);
                                billList.add(currObj.getString("short_title"));
                            }

                            for (int i = 0; i < billList.size(); i++) {
                                concatBill = billList.get(i) + " ";
                            }


                            if (concatBill == null) {
                                concatBill = "None";
                            }
                            String sourceString = "<b>Cosponsored Bills:</b> " + concatBill;
                            billsSponsored.setText(Html.fromHtml(sourceString));
                            billsSponsored.setPadding(0, 8, 0, 0);
//                            String billText = "Cosponsored Bills: " + concatBill;
//                            billsSponsored.setText(billText);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
        {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("X-API-Key", "PnuR50JUd1cski0dUfJg8B9cybpbzLsvKdjxGdeK");
                return headers;
            }
        };
        queue.add(request);
    }

    public void getBills() {
        String url = "https://api.propublica.org/congress/v1/members/" + person.memberID + ".json";
        RequestQueue billQueue = Volley.newRequestQueue(this);
        JsonObjectRequest billReq = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Success Callback
                        Log.d("getBills", "Successfully Called and received response");
                        try {
                            Log.d("getBills", "successfully received object");
                            JSONArray results = response.getJSONArray("results");
                            JSONObject resultsObject = results.getJSONObject(0);
                            JSONArray roles = resultsObject.getJSONArray("roles");

                            Log.d("getBills", roles.getString(0));

                            JSONObject rolesObj = roles.getJSONObject(0);
                            String bills = rolesObj.getString("bills_sponsored");

                            //String sponsored = roles.getString("bills_sponsored");
                            JSONObject committees = roles.getJSONObject(0);
                            //JSONArray committees = rolesObject.getJSONArray("committees");

                            Log.d("getBills Committees", committees.getString("committees"));
                            Log.d("getBills bills", bills);

                            person.sponsored = bills;
                            JSONArray committeesIter = committees.getJSONArray("committees");

                            for (int i = 0; i < committeesIter.length(); i++) {
                                committeesList.add(committeesIter.getJSONObject(i));
                            }

                            Log.d("committeesList", committeesList.toString());

                            for (int i = 0; i < committeesList.size(); i++) {
                                try {
                                    JSONObject currObj = committeesList.get(i);
                                    String currName = currObj.getString("name");
                                    committeesName.add(currName + " ");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            String arrayToText = committeesName.toString();
                            arrayToText = arrayToText.substring(1, arrayToText.length() - 2);

                            String sourceString = "<b>Participating Committees:</b> " + arrayToText;
                            committee.setText(Html.fromHtml(sourceString));
                            committee.setPadding(0,8,0,0);
//                            String committeeText = "Participating Committees: " + arrayToText;
//                            committee.setText(committeeText);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure Callback
                        Log.d("getBills", "error");
                        error.printStackTrace();

                    }
                })
        {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("X-API-Key", "PnuR50JUd1cski0dUfJg8B9cybpbzLsvKdjxGdeK");
                return headers;
            }
        };
// Adding the request to the queue along with a unique string tag
        billQueue.add(billReq);

    }



    public void getCommittees() {

    }

    public class Person implements Serializable {
        String firstName;
        String lastName;
        String name;
        String party;
        ArrayList<String> committee;
        String email;
        String website;
        String sponsored;
        String memberID;
        String repType;


        public void main(String[] args) {
            Person person = new Person();
            //person.name = person.firstName + " " + person.lastName;
        }
    }
}
