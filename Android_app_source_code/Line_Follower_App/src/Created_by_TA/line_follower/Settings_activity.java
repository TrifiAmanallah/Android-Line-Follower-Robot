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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Settings_activity extends ActionBarActivity {
	
	public static final String MyPREFERENCES = "MyPrefereces" ;
	public static final String  old_derivativ="Key_old_derivativ"; 
	public static final String  old_proportional="Key_old_proportional"; 
	public static final String  old_integral="Key_old_integral"; 
	public static final String  old_right_speed="Key_old_right_speed"; 
	public static final String  old_left_speed="Key_old_left_speed"; 
	public static final String  black_background="Key_background"; 
	public static final String  test_torch="Key_torch"; 
	SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_activity);
		final EditText EditText_derivativ;
		EditText_derivativ= (EditText) findViewById(R.id.derivativ);
		final EditText EditText_proportional;
		EditText_proportional= (EditText) findViewById(R.id.proportional);
		final EditText EditText_integral;
		EditText_integral= (EditText) findViewById(R.id.integral);
		final EditText EditText_right_speed;
		EditText_right_speed= (EditText) findViewById(R.id.Max_right_speed);
		final EditText EditText_left_speed;
		EditText_left_speed= (EditText) findViewById(R.id.Max_left_speed);
		
        final RadioGroup radiobackgroundGroup;
		radiobackgroundGroup = (RadioGroup) findViewById(R.id.background);
		final CheckBox Torch;
		Torch   = (CheckBox) findViewById(R.id.torch);
        
		
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		final int ancien_derivativ=sharedpreferences.getInt(old_derivativ,0);
		EditText_derivativ.setText( String.valueOf(ancien_derivativ));
		
		final int ancien_proportional=sharedpreferences.getInt(old_proportional,0);
		EditText_proportional.setText( String.valueOf(ancien_proportional));
		
		final int ancien_integral=sharedpreferences.getInt(old_integral,0);
		EditText_integral.setText( String.valueOf(ancien_integral));
		
		final int ancien_right_speed=sharedpreferences.getInt(old_right_speed,255);
		EditText_right_speed.setText( String.valueOf(ancien_right_speed));
		
		final int ancien_left_speed=sharedpreferences.getInt(old_right_speed,255);
		EditText_left_speed.setText( String.valueOf(ancien_left_speed));
		
		final boolean ancien_background=sharedpreferences.getBoolean(black_background,true);
		if(ancien_background)
		radiobackgroundGroup.check(R.id.black_background);
		else radiobackgroundGroup.check(R.id.white_background);
		
		final boolean ancien_test_torch=sharedpreferences.getBoolean(test_torch,true);
		if(ancien_test_torch)
		Torch.setChecked(true);
		else Torch.setChecked(false);
			
		
		final Button Bouton_Enregistrement = (Button)findViewById(R.id.enregistrer);
		Bouton_Enregistrement.setOnClickListener(			 
	    		new View.OnClickListener()

	    		{

	                    public void onClick(View aView)
	                    {
	                    	int nouveau_derivativ=Integer.parseInt(EditText_derivativ.getText().toString());
	                		int nouveau_proportional=Integer.parseInt(EditText_proportional.getText().toString());
	                		int nouveau_integral=Integer.parseInt(EditText_integral.getText().toString());
	                		int nouveau_right_speed=Integer.parseInt(EditText_right_speed.getText().toString());
	                		int nouveau_left_speed=Integer.parseInt(EditText_left_speed.getText().toString());
                            int selectedId = radiobackgroundGroup.getCheckedRadioButtonId();
	                			                		
	                		Editor editor = sharedpreferences.edit();
              	            editor.putInt(old_derivativ, nouveau_derivativ);
              	            editor.putInt(old_proportional, nouveau_proportional);
              	            editor.putInt(old_integral, nouveau_integral);
              	            editor.putInt(old_right_speed, nouveau_right_speed);
              	            editor.putInt(old_left_speed, nouveau_left_speed);
              	          if(selectedId==R.id.black_background)
              	        	editor.putBoolean(black_background,true);
              	          else
              	        	editor.putBoolean(black_background,false);  
              	           
              	           if(Torch.isChecked())
              	        	 editor.putBoolean(test_torch,true);
              	           else	   
              	        	 editor.putBoolean(test_torch,false); 
              	             
              	          editor.commit(); 
              	          Toast.makeText(getApplicationContext(), "Successful saving" , 
	        				      Toast.LENGTH_LONG).show();   
	                    	      Intent toAnotherActivity = new Intent(aView.getContext(),Start_activity.class);
	                              startActivityForResult(toAnotherActivity, 0);
	                    }
	    		}
	                                             );
	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings_activity, menu);
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
