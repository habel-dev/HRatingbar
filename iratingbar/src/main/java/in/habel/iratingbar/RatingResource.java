package in.habel.iratingbar;

/**
 * Created by habel on 22-Jan-17.
 */

public class RatingResource {
    private int ratingEmpty;
    private int ratingFull;

    public RatingResource(int ratingEmpty, int ratingFull) {
        this.ratingEmpty = ratingEmpty;
        this.ratingFull = ratingFull;
    }

    public int getRatingEmpty() {
        return ratingEmpty;
    }

    public void setRatingEmpty(int ratingEmpty) {
        this.ratingEmpty = ratingEmpty;
    }

    public int getRatingFull() {
        return ratingFull;
    }

    public void setRatingFull(int ratingFull) {
        this.ratingFull = ratingFull;
    }
}
