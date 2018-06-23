package phramusca.com.jamuzkids;

import android.widget.Button;

class ButtonRating {
    private final Button button;
    private final int rating;
    private final int resId;
    private final int resIdSelected;

    public ButtonRating(Button button, int rating, int resId, int resIdSelected) {
        this.button = button;
        this.rating = rating;
        this.resId = resId;
        this.resIdSelected = resIdSelected;
    }

    public int getRating() {
        return rating;
    }

    public Button getButton() {
        return button;
    }

    public int getResId() {
        return resId;
    }

    public int getResIdSelected() {
        return resIdSelected;
    }
}
