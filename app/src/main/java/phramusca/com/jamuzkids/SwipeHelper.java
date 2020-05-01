package phramusca.com.jamuzkids;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

//https://stackoverflow.com/questions/44965278/recyclerview-itemtouchhelper-buttons-on-swipe?answertab=active#tab-top
public abstract class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    private static final int BUTTON_WIDTH = 100;
    private RecyclerView recyclerView;
    private List<UnderlayButton> buttons;
    private GestureDetector gestureDetector;
    private int swipedPos = -1;
    private float swipeThreshold = 0.5f;
    private SparseArray<List<UnderlayButton>> buttonsBuffer;
    private Queue<Integer> recoverQueue;

    SwipeHelper(Context context, RecyclerView recyclerView, int swipeDirs) {
        super(0, swipeDirs);
        this.recyclerView = recyclerView;
        this.buttons = new ArrayList<>();
        buttonsBuffer = new SparseArray<>();
        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                for (UnderlayButton button : buttons) {
                    if (button.onClick(e.getX(), e.getY()))
                        break;
                }

                return true;
            }
        };
        this.gestureDetector = new GestureDetector(context, gestureListener);
        View.OnTouchListener onTouchListener;
        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                if (swipedPos < 0) return false;
                Point point = new Point((int) e.getRawX(), (int) e.getRawY());

                RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos);
                if (swipedViewHolder == null) {
                    return false;
                }
                View swipedItem = swipedViewHolder.itemView;
                Rect rect = new Rect();
                swipedItem.getGlobalVisibleRect(rect);

                if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
                    if (rect.top < point.y && rect.bottom > point.y) {
                        gestureDetector.onTouchEvent(e);
                    } else {
                        recoverQueue.add(swipedPos);
                        swipedPos = -1;
                        SwipeHelper.this.recoverSwipedItem();
                    }
                }
                return false;
            }
        };
        if(recyclerView!=null) {
            this.recyclerView.setOnTouchListener(onTouchListener);
        }
        recoverQueue = new LinkedList<Integer>(){
            @Override
            public boolean add(Integer o) {
                return !contains(o) && super.add(o);
            }
        };

        attachSwipe();
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();

        if (swipedPos != pos)
            recoverQueue.add(swipedPos);

        swipedPos = pos;

        if (buttonsBuffer.get(swipedPos)!=null)
            buttons = buttonsBuffer.get(swipedPos);
        else
            buttons.clear();

        buttonsBuffer.clear();
        swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
        recoverSwipedItem();
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;

        if (pos < 0){
            swipedPos = pos;
            return;
        }

        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            List<UnderlayButton> buffer = new ArrayList<>();

            if (buttonsBuffer.get(pos) != null) {
                buffer = buttonsBuffer.get(pos);
            } else {
                instantiateUnderlayButton(viewHolder, buffer);
                buttonsBuffer.put(pos, buffer);
            }

            translationX = dX * buffer.size() * BUTTON_WIDTH / itemView.getWidth();
            drawButtons(c, itemView, buffer, pos, translationX);
        }

        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private synchronized void recoverSwipedItem(){
        while (!recoverQueue.isEmpty()){
            int pos = recoverQueue.poll();
            if (pos > -1) {
                recyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    private void drawButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX){
        float right = itemView.getRight();
        float left = itemView.getLeft();
        float dButtonWidth = (-1) * dX / buffer.size();

        for (UnderlayButton button : buffer) {
            if (dX < 0) {
                left = right - dButtonWidth;
                button.onDraw(
                        c,
                        new RectF(
                                left,
                                itemView.getTop(),
                                right,
                                itemView.getBottom()
                        ),
                        pos, dX
                );
                right = left;
            } else if (dX > 0) {
                right = left - dButtonWidth;
                button.onDraw(c,
                        new RectF(
                                left,
                                itemView.getTop(),
                                right,
                                itemView.getBottom()
                        ), pos, dX
                );
                left=right;
            }
        }
    }

    private void attachSwipe(){
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public abstract void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);

    public enum ButtonInfo {
        PLAY("", R.drawable.ic_slide_queue_play, Color.parseColor("#1e8449")), //NOI18N
        QUEUE("", R.drawable.ic_slide_queue_add, Color.parseColor("#82e0aa")), //NOI18N
        DOWN("", R.drawable.ic_slide_down, Color.parseColor("#f7dc6f")), //NOI18N
        DEL("", R.drawable.ic_slide_remove, Color.parseColor("#c0392b")); //NOI18N

        public String text;
        public int imageResId;
        public int color;

        ButtonInfo(String text, int imageResId, int color) {
            this.text = text;
            this.imageResId = imageResId;
            this.color = color;
        }
    }

    public static class UnderlayButton {

        private int pos;
        private RectF clickRegion;
        private UnderlayButtonClickListener clickListener;
        private Context mContext;
        private ButtonInfo buttonInfo;

        UnderlayButton(ButtonInfo buttonInfo, UnderlayButtonClickListener clickListener, Context mContext) {
            this.buttonInfo = buttonInfo;
            this.clickListener = clickListener;
            this.mContext = mContext;
        }

        public boolean onClick(float x, float y){
            if (clickRegion != null && clickRegion.contains(x, y)){
                clickListener.onClick(pos);
                return true;
            }

            return false;
        }

        void onDraw(Canvas canvas, RectF rect, int pos, float dX){
            Paint p = new Paint();

            // Draw background
            p.setColor(buttonInfo.color);
            canvas.drawRect(rect, p);

            //Draw icon
            Drawable d = mContext.getResources().getDrawable(buttonInfo.imageResId, null);
            int iconSize=70;
            int marginH=20;
            int marginV=15;
            // Top left corner
            /*int left = (int) rect.left+marginH;
            int top = (int) rect.top+marginV;
            int right = left+iconSize<rect.right-marginH?left+iconSize: (int) rect.right-marginH;
            int bottom = top+iconSize;*/
            //Center vertical and horizontal
            float xD = rect.width() / 2f - iconSize / 2f;
            float yD = rect.height() / 2f - iconSize / 2f;

            int left = (int) (rect.left + xD);
            int top = (int) (rect.top+yD);
            int right = left+iconSize<rect.right-marginH?left+iconSize: (int) rect.right-marginH;
            int bottom = top+iconSize;

            d.setBounds(left, top, right, bottom);
            d.draw(canvas);

            // Draw Text
            if(!buttonInfo.text.equals("")) {

                //Center text horizontal and vertical
                /*Rect r = new Rect();
                p.setColor(Color.WHITE);
                p.setTextAlign(Paint.Align.CENTER);
                p.getTextBounds(text, 0, text.length(), r);
                p.setTextSize(30);
                float x = rect.width() / 2f - r.width() / 2f - r.left;
                float y = rect.height() / 2f + r.height() / 2f - r.bottom;
                canvas.drawText(text, rect.left + x, rect.top + y, p);*/

                //For long text on multiple lines (no needed/wanted on small texts)
                TextPaint textPaint = new TextPaint();
                textPaint.setTextSize(30);
                textPaint.setColor(Color.WHITE);
                StaticLayout sl = new StaticLayout(buttonInfo.text, textPaint, (int)rect.width(),
                        Layout.Alignment.ALIGN_CENTER, 1, 1, false);
                canvas.save();
                Rect r = new Rect();
                float y = (rect.height() / 2f) + (r.height() / 2f) - r.bottom - (sl.getHeight() /2);
                canvas.translate(rect.left, rect.top + y);
                sl.draw(canvas);
                canvas.restore();
            }

            clickRegion = rect;
            this.pos = pos;
        }
    }

    public interface UnderlayButtonClickListener {
        void onClick(int pos);
    }
}