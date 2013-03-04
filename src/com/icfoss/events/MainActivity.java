package com.icfoss.events;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.Inflater; 

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	AlertDialog dialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        new DownloadFileAsync().execute();
        
        Button bRegister = (Button)findViewById(R.id.register);
		bRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
//				dialog.dismiss();
//				Intent email = new Intent(Intent.ACTION_SEND);
//				email.putExtra(Intent.EXTRA_EMAIL, new String[]{"user@gmail.com"});
//		        //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
//		        //email.putExtra(Intent.EXTRA_BCC, new String[]{"nutannivate@gmail.com"});
//		        email.putExtra(Intent.EXTRA_SUBJECT, "Registration for "+topic_list.get(position));
//		        email.putExtra(Intent.EXTRA_TEXT, "Please register me for "+ topic_list.get(position)+"."+ "\n\n"+ "Thank you");
//
//		        //need this to prompts email client only
//		        email.setType("message/rfc822");
//		        
//		        startActivity(Intent.createChooser(email, "Choose an Email client :"));
				
				
			}
		});
    }

   
    
    class DownloadFileAsync extends AsyncTask<String, String, String>{

    	String result = "";
	@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpContext httpcontext = new BasicHttpContext();
			HttpGet httpget = new HttpGet("http://icfoss.org/events.html");
			
			try {
				HttpResponse response = httpclient.execute(httpget, httpcontext);
				
				BufferedReader b_reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				
				String line = null;
				while((line = b_reader.readLine()) != null){
					result += line + "\n";
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
    
	@Override
	protected void onPostExecute(String r) {
		addEventsIndatabase(result);
		
	}
	
    }
    
    
	private void addEventsIndatabase(final String result) {
		final String[] topic = StringUtils.substringsBetween(result, "<strong>", "</strong>");
		final String[] date = StringUtils.substringsBetween(result, "</h3>","<p>");
		final String[] content = StringUtils.substringsBetween(result, "<p>","</p>");
			
		final ArrayList<String> topic_list = new ArrayList<String>();
		
		for (int i=0; i< topic.length; i++){
			String event_title = StringUtils.remove(topic[i], "&ldquo;");
			event_title = StringUtils.remove(event_title, "&rdquo;");
			topic_list.add(event_title.trim());
		}
		
		
		final ListView list = (ListView)findViewById(R.id.list);
		list.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, topic_list));
		
		list.setOnItemClickListener(new OnItemClickListener() {
 
			@Override
			public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
				
				LayoutInflater layout_inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = layout_inflater.inflate(R.layout.event_detail, null);
				AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
				alert.setView(layout);
				alert.setTitle(list.getItemAtPosition(position).toString());
				
				TextView tvContent = (TextView)layout.findViewById(R.id.content);
				tvContent.setText(content[position].trim());
				
				TextView tvdate = (TextView)layout.findViewById(R.id.date);
				tvdate.setText(date[position].trim());
				
				Button bRegister = (Button)layout.findViewById(R.id.register);
				bRegister.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
					dialog.dismiss();
					Intent email = new Intent(Intent.ACTION_SEND);
					email.putExtra(Intent.EXTRA_EMAIL, new String[]{"user@gmail.com"});
				        //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
				        //email.putExtra(Intent.EXTRA_BCC, new String[]{"nutannivate@gmail.com"});
				        email.putExtra(Intent.EXTRA_SUBJECT, "Registration for "+topic_list.get(position));
				        email.putExtra(Intent.EXTRA_TEXT, "Please register me for "+ topic_list.get(position)+"."+ "\n\n"+ "Thank you");

				        //need this to prompts email client only
				        email.setType("message/rfc822");
				        
				        startActivity(Intent.createChooser(email, "Choose an Email client :"));
						
						
					}
				});
				
				dialog = alert.create();
				dialog.show();
				
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = 700;
                
                dialog.getWindow().setAttributes(lp);
			}
			
		});

		
		DatabaseHandler db = new DatabaseHandler(this);
		
		for (int i=0; i< topic.length; i++){
			String event_title = StringUtils.remove(topic[i], "&ldquo;");
			event_title = StringUtils.remove(event_title, "&rdquo;");
			
			String date_title = StringUtils.remove(date[i], "(");
			date_title = StringUtils.remove(date_title, ")");
			
			db.addEvent(event_title, date_title);
    			db.close();
			
		}
		}
    
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
