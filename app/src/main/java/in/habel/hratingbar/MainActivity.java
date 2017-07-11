package in.habel.hratingbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

import in.habel.iratingbar.IRatingBarLayout;
import in.habel.iratingbar.IRatingListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView txtView = (TextView) findViewById(R.id.rated);
        IRatingBarLayout ratingBar = (IRatingBarLayout) findViewById(R.id.iRatingBar);
        ratingBar.addOnRatingChangeListener(new IRatingListener() {
            @Override
            public void onRatingChanged(IRatingBarLayout iRatingBar, float rating, int maxRating) {
                txtView.setText(String.format(Locale.ENGLISH, "Rated : %1$s", (int) rating));
            }
        });

    }
}
