package Created_by_TA.line_follower;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;




public class Start_activity extends ActionBarActivity {

	public static final String MyPREFERENCES = "MyPrefereces" ;
    public static final String test_premiere_execution  ="test"; 
    public static final String  old_derivativ="Key_old_derivativ"; 
	public static final String  old_proportional="Key_old_proportional"; 
	public static final String  old_integral="Key_old_integral";
	public static final String USB_STATUS  ="key_USB_STATUS";
    public static final String SENDING_STATUS  ="key_SENDING_STATUS";
    public static final String  black_background="Key_background"; 
    public static final String  test_torch="Key_torch"; 

	SharedPreferences sharedpreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_activity);
		getSupportActionBar().hide();
			final ImageButton Bouton_start= (ImageButton)findViewById(R.id.Bstart);
		final ImageButton Bouton_settings= (ImageButton)findViewById(R.id.Bsettings);
		final ImageButton Bouton_exit= (ImageButton)findViewById(R.id.Bexit);
		
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		if (sharedpreferences.getBoolean(test_premiere_execution,true)){
			  Editor editor = sharedpreferences.edit();
		      editor.putBoolean(test_premiere_execution, false);
		      editor.putInt( old_derivativ,2);
		      editor.putInt( old_proportional,1);
		      editor.putInt(old_integral,0);
		      editor.putInt(USB_STATUS,1);
		      editor.putInt(SENDING_STATUS,0);
		      editor.putBoolean(black_background,true);
		      editor.putBoolean(test_torch,true);
              editor.commit();
		}
		Bouton_start.setOnClickListener(			 
	    		new View.OnClickListener()

	    		{
	                    public void onClick(View aView)
	                    {Intent toAnotherActivity = new Intent(aView.getContext(),LineTracker.class);
	                     startActivityForResult(toAnotherActivity, 0);}
	             }
	    		                        );
		
		Bouton_settings.setOnClickListener(			 
	    		new View.OnClickListener()

	    		{
	                    public void onClick(View aView)
	                    {Intent toAnotherActivity = new Intent(aView.getContext(),Settings_activity.class);
	                     startActivityForResult(toAnotherActivity, 0);}
	             }
	    		                        );
		
		Bouton_exit.setOnClickListener(			 
	    		new View.OnClickListener()

	    		{
	                    public void onClick(View aView)
	                    {Intent intent = new Intent(Intent.ACTION_MAIN);
	                    intent.addCategory(Intent.CATEGORY_HOME);
	                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                    startActivity(intent);
	                    }
	    		}
	    		);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_activity, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
