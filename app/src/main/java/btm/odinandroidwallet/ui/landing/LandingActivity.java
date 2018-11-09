package btm.odinandroidwallet.ui.landing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.ui.auth.LoginActivity;
import btm.odinandroidwallet.ui.createwallet.CreateWalletActivity;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LandingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing);
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LandingActivity.this,LoginActivity.class);
                startActivity(i);

            }
        });

        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LandingActivity.this,CreateWalletActivity.class);
                startActivity(i);

            }
        });

    }


}
