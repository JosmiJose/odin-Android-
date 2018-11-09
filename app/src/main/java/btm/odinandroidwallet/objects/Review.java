package btm.odinandroidwallet.objects;

/**
 * Object of Review entity
 *
 */

public class Review {
    public String title;
    public String desc;
    public int rating;

    public Review(String title, String desc, int rating)
    {
        this.title=title;
        this.desc=desc;
        this.rating=rating;
    }
}
