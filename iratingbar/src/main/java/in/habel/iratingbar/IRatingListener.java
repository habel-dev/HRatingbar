package in.habel.iratingbar;

/**
 * Created by instio on 1/25/2017.
 */

public interface IRatingListener {
    void onRatingChanged(IRatingBarLayout iRatingBar, float rating, int maxRating);
}
