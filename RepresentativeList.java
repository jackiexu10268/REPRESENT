package com.jacquelinejxu.represent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.graphics.Color.WHITE;

public class RepresentativeList extends AppCompatActivity {
    Person rep1;
    Person rep2;
    Person rep3;
    JSONObject person1;
    JSONObject person2;
    JSONObject person3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative_list);

        TextView name1 = findViewById(R.id.repname1);
        TextView name2 = findViewById(R.id.repname2);
        TextView name3 = findViewById(R.id.repname3);
        TextView party1 = findViewById(R.id.party);
        TextView party2 = findViewById(R.id.party2);
        TextView party3 = findViewById(R.id.party3);
        TextView house1 = findViewById(R.id.type1);
        TextView house2 = findViewById(R.id.type2);
        TextView house3 = findViewById(R.id.type3);
        TextView email1 = findViewById(R.id.contact);
        TextView email2 = findViewById(R.id.contact2);
        TextView email3 = findViewById(R.id.contact3);
        TextView web1 = findViewById(R.id.web1);
        TextView web2 = findViewById(R.id.web2);
        TextView web3 = findViewById(R.id.web3);
        TextView zipDisplay = findViewById(R.id.textView4);

        String ziptext = "For location: " + getIntent().getStringExtra("zipcode");
        zipDisplay.setText(ziptext);


        Button more1 = findViewById(R.id.button6);
        Button more2 = findViewById(R.id.button7);
        Button more3 = findViewById(R.id.button8);
        Button home = findViewById(R.id.button4);

        ImageView image1 = findViewById(R.id.imageView);
        ImageView image2 = findViewById(R.id.imageView4);
        ImageView image3 = findViewById(R.id.imageView3);

        rep1 = new Person();
        rep2 = new Person();
        rep3 = new Person();

        final ArrayList<JSONObject> peopleArray = new ArrayList<>();

        try {
            int num_people = getIntent().getIntExtra("num_people", 0);
            for (int i = 0; i < num_people; i++) {
                JSONObject currPerson = new JSONObject(getIntent().getStringExtra(Integer.toString(i)));
                peopleArray.add(currPerson);
            }

            person1 = new JSONObject(getIntent().getStringExtra(Integer.toString(0)));
            person2 = new JSONObject(getIntent().getStringExtra(Integer.toString(1)));
            person3 = new JSONObject(getIntent().getStringExtra(Integer.toString(2)));
            JSONObject currBio1 = person1.getJSONObject("bio");
            JSONObject currBio2 = person2.getJSONObject("bio");
            JSONObject currBio3 = person3.getJSONObject("bio");

            Log.d("person1", person1.getString("type"));
            Log.d("bio1", person1.getString("bio"));

            rep1.repType = person1.getString("type");
            rep2.repType = person2.getString("type");
            rep3.repType = person3.getString("type");

            rep1.repType = rep1.repType.substring(0,1).toUpperCase() + rep1.repType.substring(1);
            rep2.repType = rep2.repType.substring(0,1).toUpperCase() + rep2.repType.substring(1);
            rep3.repType = rep3.repType.substring(0,1).toUpperCase() + rep3.repType.substring(1);

            rep1.firstName = currBio1.getString("first_name");
            rep2.firstName = currBio2.getString("first_name");
            rep3.firstName = currBio3.getString("first_name");

            rep1.lastName = currBio1.getString("last_name");
            rep2.lastName = currBio2.getString("last_name");
            rep3.lastName = currBio3.getString("last_name");

            rep1.name = rep1.firstName + " " + rep1.lastName;
            rep2.name = rep2.firstName + " " + rep2.lastName;
            rep3.name = rep3.firstName + " " + rep3.lastName;

            rep1.party = currBio1.getString("party");
            rep2.party = currBio2.getString("party");
            rep3.party = currBio3.getString("party");

            JSONObject contact1 = person1.getJSONObject("contact");
            JSONObject contact2 = person2.getJSONObject("contact");
            JSONObject contact3 = person3.getJSONObject("contact");

            rep1.email = contact1.getString("contact_form");
            rep2.email = contact2.getString("contact_form");
            rep3.email = contact3.getString("contact_form");

            rep1.website = contact1.getString("url");
            rep2.website = contact2.getString("url");
            rep3.website = contact3.getString("url");

            JSONObject ref1 = person1.getJSONObject("references");
            JSONObject ref2 = person2.getJSONObject("references");
            JSONObject ref3 = person3.getJSONObject("references");

            rep1.memberID = ref1.getString("bioguide_id");
            rep2.memberID = ref2.getString("bioguide_id");
            rep3.memberID = ref3.getString("bioguide_id");

            name1.setText(rep1.name);
            name2.setText(rep2.name);
            name3.setText(rep3.name);

            party1.setText(rep1.party);
            party2.setText(rep2.party);
            party3.setText(rep3.party);

            house1.setText(rep1.repType);
            house2.setText(rep2.repType);
            house3.setText(rep3.repType);

            if (rep1.email == null) {
                String url1 = "<a href='"+ rep1.email +"'> Contact Unavailable</a>";
                email1.setText(Html.fromHtml(url1));
                email1.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                email1.setClickable(true);
                email1.setMovementMethod(LinkMovementMethod.getInstance());
                String url1 = "<a href='"+ rep1.email +"'> Contact</a>";
                email1.setText(Html.fromHtml(url1));
                email1.setTextColor(Color.parseColor("#FFFFFF"));
            }

            if (rep2.email == null ){
                String url2 = "<a href='"+ rep2.email +"'> Contact Unavailable</a>";
                email2.setText(Html.fromHtml(url2));
                email2.setTextColor(Color.parseColor("#FFFFFF"));

            } else {
                email2.setClickable(true);
                email2.setMovementMethod(LinkMovementMethod.getInstance());
                String url2 = "<a href='"+ rep2.email +"'> Contact</a>";
                email2.setText(Html.fromHtml(url2));
                email2.setTextColor(Color.parseColor("#FFFFFF"));
            }

            if (rep3.email == null) {
                String url3 = "<a href='"+ rep3.email +"'> Contact Unavailable</a>";
                email3.setText(Html.fromHtml(url3));
                email3.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                email3.setClickable(true);
                email3.setMovementMethod(LinkMovementMethod.getInstance());
                String url3 = "<a href='"+ rep3.email +"'> Contact</a>";
                email3.setText(Html.fromHtml(url3));
                email3.setTextColor(Color.parseColor("#FFFFFF"));
            }

            web1.setClickable(true);
            web1.setMovementMethod(LinkMovementMethod.getInstance());
            String text1 = "<a href='"+ rep1.website +"'> Website</a>";
            web1.setText(Html.fromHtml(text1));
            web1.setTextColor(Color.parseColor("#FFFFFF"));

            web2.setClickable(true);
            web2.setMovementMethod(LinkMovementMethod.getInstance());
            String text2 = "<a href='"+ rep2.website +"'> Website</a>";
            web2.setText(Html.fromHtml(text2));
            web2.setTextColor(Color.parseColor("#FFFFFF"));

            web3.setClickable(true);
            web3.setMovementMethod(LinkMovementMethod.getInstance());
            String text3 = "<a href='"+ rep3.website +"'> Website</a>";
            web3.setText(Html.fromHtml(text3));
            web3.setTextColor(Color.parseColor("#FFFFFF"));

            String rep1ImgUrl = getImgUrl(rep1.memberID);
            String rep2ImgUrl = getImgUrl(rep2.memberID);
            String rep3ImgUrl = getImgUrl(rep3.memberID);

            Picasso.with(this).load(rep1ImgUrl).into(image1);
            Picasso.with(this).load(rep2ImgUrl).into(image2);
            Picasso.with(this).load(rep3ImgUrl).into(image3);


//            for (int i = 0; i < peopleArray.size(); i++) {
//                LinearLayout linear = findViewById(R.id.linear);
//                TextView name = new TextView(this);
//                TextView party = new TextView(this);
//                TextView contact = new TextView(this);
//                TextView website = new TextView(this);
//                ImageView img = new ImageView(this);
//                Button more = new Button(this);
//                TextView type = new TextView(this);
//
//                name.setLayoutParams(
//                        new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
//                                ConstraintLayout.LayoutParams.WRAP_CONTENT));
//
//                linear.addView(img);
//                linear.addView(name);
//                linear.addView(type);
//                linear.addView(party);
//                linear.addView(contact);
//                linear.addView(website);
//                linear.addView(more);
//
//                more.setBackgroundColor(Color.parseColor("#585BA6"));
//                more.setText("More Info");
//                more.setLayoutParams(new LinearLayout.LayoutParams(200, 75));
//                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) more.getLayoutParams();
//                ll.gravity = Gravity.CENTER_HORIZONTAL;
//                more.setLayoutParams(ll);
//
//                final JSONObject currPerson = peopleArray.get(i);
//                JSONObject currBio = currPerson.getJSONObject("bio");
//                JSONObject contactInfo = currPerson.getJSONObject("contact");
//                JSONObject references = currPerson.getJSONObject("references");
//
//                String member_id = references.getString("bioguide_id");
//                String member_id_url = getImgUrl(member_id);
//                String full_name = currBio.getString("first_name") + " " + currBio.getString("last_name");
//
//                name.setText(full_name);
//                type.setText(currPerson.getString("type"));
//                party.setText(currBio.getString("party"));
//                contact.setText(contactInfo.getString("contact_form"));
//                website.setText(contactInfo.getString("url"));
//                Picasso.with(this).load(member_id_url).into(img);
//
//                name.setPadding(32, 4, 32, 0);
//                type.setPadding(32, 4, 32, 0);
//                party.setPadding(32, 4, 32, 0);
//                contact.setPadding(32, 4, 32, 0);
//                website.setPadding(32, 4, 32, 0);
//                img.setPadding(32, 16, 32, 0);
//
//                name.setTextColor(Color.parseColor("#FFFFFF"));
//                type.setTextColor(Color.parseColor("#FFFFFF"));
//                party.setTextColor(Color.parseColor("#FFFFFF"));
//                contact.setTextColor(Color.parseColor("#FFFFFF"));
//                website.setTextColor(Color.parseColor("#FFFFFF"));
//
//                name.setTextSize(18);
//                type.setTextSize(14);
//                party.setTextSize(14);
//                contact.setTextSize(14);
//                website.setTextSize(14);
//
//                contact.setClickable(true);
//                contact.setMovementMethod(LinkMovementMethod.getInstance());
//                String contact1 = "<a href='"+ contactInfo.getString("contact_form") +"'> Contact</a>";
//                contact.setText(Html.fromHtml(contact1));
//                contact.setLinkTextColor(Color.parseColor("#585BA6"));
//
//                website.setClickable(true);
//                website.setMovementMethod(LinkMovementMethod.getInstance());
//                String website1 = "<a href='"+ contactInfo.getString("url") +"'> Website</a>";
//                website.setText(Html.fromHtml(website1));
//                website.setLinkTextColor(Color.parseColor("#585BA6"));
//
//                more.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(RepresentativeList.this, RepresentativeProfile.class);
//                        intent.putExtra(Integer.toString(i), peopleArray.get(i).toString());
//
//                            startActivity(intent);
//                        }
//                    });
//
//
//
//            }


        } catch (JSONException e) {
            e.printStackTrace();
        }






        more1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RepresentativeList.this, RepresentativeProfile.class);
                i.putExtra("rep", person1.toString());

                startActivity(i);
            }
        });

        more2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RepresentativeList.this, RepresentativeProfile.class);
                i.putExtra("rep", person2.toString());

                startActivity(i);
            }
            });

        more3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RepresentativeList.this, RepresentativeProfile.class);
                i.putExtra("rep", person3.toString());

                startActivity(i);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RepresentativeList.this, HomePage.class);
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

    public class Person {
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


