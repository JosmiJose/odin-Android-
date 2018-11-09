package btm.odinandroidwallet.ui.help;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.adapters.HelpAdapter;


public class HelpActivity extends AppCompatActivity {
    ListView list;
    ArrayList<String> dataModels;
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

        dataModels.add("Category 1");
        dataModels.add("Category 2");
        dataModels.add("Category 3");
        dataModels.add("Category 4");

        HelpAdapter adapter= new HelpAdapter(dataModels,HelpActivity.this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(HelpActivity.this,HelpCatActivity.class);
                startActivity(intent);
            }
        });
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
