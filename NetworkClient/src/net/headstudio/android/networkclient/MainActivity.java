package net.headstudio.android.networkclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.Socket;
import java.net.UnknownHostException;
 





import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
 
public class MainActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String server;
    private int port;
    private Socket client;
    private CheckBox check;
    private Button button;
    private AutoCompleteTextView text;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        button = (Button) findViewById(R.id.button1);
        check = (CheckBox) findViewById(R.id.checkBox1);
        text = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
 
        button.setOnClickListener(this);
    }
 
	@Override
	public void onClick(View v) {
		// detect the view that was "clicked"
        switch (v.getId()) {
        case R.id.button1:
        	Log.e("NetworkClient", "++onClick");
        	new ATask().execute("");
            break;
        }		
	}
	
	// Main activity cannot execute network tasks like threads.
    
    private class ATask extends AsyncTask<String, Void, String> {
    	
    	private String msg;

        @Override
        protected String doInBackground(String... params) {
        	server = "192.168.2.146"; // Server
            port = 12345; // Port
            String s = "";
            
            try {
                // Make Socket
                client = new Socket(server, port);
                Log.e("NetworkClient", "++makeSocket");
                // Output Stream
                out = new ObjectOutputStream(client.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());
                Log.e("NetworkClient", "++getOutStream");
                out.writeObject(msg);
                Log.e("NetworkClient", "++writeOut");
            } catch (UnknownHostException e) {
                // Error toast
                Toast.makeText(MainActivity.this, R.string.UnknownHost,
                Toast.LENGTH_LONG).show();
                android.util.Log.w("¡¡¡ Unknown host!!!", e.getMessage());
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, R.string.ConnectionError,
                    Toast.LENGTH_LONG).show();
                android.util.Log.w("¡¡¡ I/O error !!!", e.getMessage());
            }
            try {
				s = (String)in.readObject();
			} catch (OptionalDataException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            return s;
        }

        @Override
        protected void onPostExecute(String result) {
            text.setText(result); 
        }

        @Override
        protected void onPreExecute() {
        	msg = text.getText().toString();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
        
        protected void onDestroy() {
            try {
                out.close();
                client.close();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, R.string.Disconnected,
                    Toast.LENGTH_LONG).show();
                android.util.Log.w("¡¡¡Error!!!", e.getMessage());
            }
            //super.onDestroy();
     
        }
     
        public void onSend(View btn) {
            try {
                if (client != null) {
                    out.writeObject((String)text.getText().toString());
                    out.flush(); // Clear buffer to send
                } else
                    Toast.makeText(MainActivity.this, R.string.ConnectionError,
                        Toast.LENGTH_LONG).show();
     
            // Header
            } catch (UnknownHostException e) {
                Toast.makeText(MainActivity.this, R.string.UnknownHost,
                    Toast.LENGTH_LONG).show();
                android.util.Log.w("¡¡¡Error!!!", e.getMessage());
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, R.string.ConnectionError,
                    Toast.LENGTH_LONG).show();
                android.util.Log.w("¡¡¡Error!!!", e.getMessage());
            }
     
        }
     
        public void onConnectDisconnect(View checkBox) {
            if (!check.isChecked()) {
                button.setEnabled(false);
                text.setEnabled(false);
            } else {
                button.setEnabled(true);
                text.setEnabled(true);
            }
     
        }
  
    }

}