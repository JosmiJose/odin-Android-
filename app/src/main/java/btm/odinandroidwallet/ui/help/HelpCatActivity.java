package btm.odinandroidwallet.ui.help;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.QuesHelpAdapter;
import btm.odinandroidwallet.objects.questionEntity;

public class HelpCatActivity extends AppCompatActivity {
    ListView list;
    ArrayList<questionEntity> dataModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        TextView title=findViewById(R.id.title);
        title.setText(getResources().getString(R.string.help_center));
        list=findViewById(R.id.list);
        dataModels= new ArrayList<>();

        dataModels.add(new questionEntity("I am new  what is this ?","This is odin investment wallet , where you can easily invest your spare change while you spend , Track your investment progress here and withdraw anytime you wish !"));
        dataModels.add(new questionEntity("How do I create a new wallet ?","This is odin investment wallet , where you can easily invest your spare change while you spend , Track your investment progress here and withdraw anytime you wish !"));
        dataModels.add(new questionEntity("How do I back up my wallet?","This is odin investment wallet , where you can easily invest your spare change while you spend , Track your investment progress here and withdraw anytime you wish !"));
        dataModels.add(new questionEntity("How do I access my wallet?","This is odin investment wallet , where you can easily invest your spare change while you spend , Track your investment progress here and withdraw anytime you wish !"));

        QuesHelpAdapter adapter= new QuesHelpAdapter(dataModels,HelpCatActivity.this);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
