package sdk.im.qunar.com.qtalksdkdemo;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.qunar.im.ui.fragment.ConversationFragment;

public class ConversationListActivity extends AppCompatActivity {
    FrameLayout root_container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
        bindViews();
    }

    private void bindViews()
    {
        root_container = (FrameLayout) findViewById(R.id.root_container);
        getSupportFragmentManager().beginTransaction().replace(R.id.root_container, new ConversationFragment())
                .commit();
    }
}
