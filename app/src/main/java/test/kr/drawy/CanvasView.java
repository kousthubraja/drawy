package test.kr.drawy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CanvasView extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath, rPath;
    Context context;
    private Paint mPaint, rPaint;
    private float mX, mY;
    private float rX, rY;
    private static final float TOLERANCE = 5;

    ArrayList<Point> points = new ArrayList<Point>();

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        // we set a new Path
        mPath = new Path();
        rPath = new Path();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);

        rPaint = new Paint();
        rPaint.setAntiAlias(true);
        rPaint.setColor(Color.BLUE);
        rPaint.setStyle(Paint.Style.STROKE);
        rPaint.setStrokeJoin(Paint.Join.ROUND);
        rPaint.setStrokeWidth(4f);

        LocalBroadcastManager.getInstance(c).registerReceiver(mMessageReceiver, new IntentFilter("custom-event-name"));
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(rPath,rPaint);
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

        points.add(new Point((int)x, (int)y));
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            points.add(new Point((int)x, (int)y));
        }
    }

    public void clearCanvas() {
        mPath.reset();
        rPath.reset();
        invalidate();

    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPath.lineTo(mX, mY);

        points.add(new Point((int) mX, (int) mY));
        StringBuffer sb = new StringBuffer();

        if(points.isEmpty())
            return;

        //Point prev = points.get(0);
        for(Point point : points){
            float x, y;
            x = point.x;
            y = point.y;

            int nx, ny;
            x = x/getWidth() * 720.f;
            y = y/getHeight() * 1280.f;

            nx = (int)x;
            ny = (int)y;

            sb.append(nx);
            sb.append(",");
            sb.append(ny);
            sb.append("|");
        }

        Log.d("Path", sb.toString());
        new Sender().execute(sb.toString());

        points.clear();
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String message = intent.getStringExtra("message");
            Log.d("receiver", message);
//            new AsyncTask<Void, Void, Void>(){
//                @Override
//                protected Void doInBackground(Void... params) {
//                    drawCurve(message);
//                    return null;
//                }
//            }.execute();
            drawCurve(message);
        }
    };

    void drawCurve(String curve){
        String[] pts = curve.split("\\|");
        String[] p0 = pts[0].split(",");

        float x, y;
        x = Integer.valueOf(p0[0]);
        y = Integer.valueOf(p0[1]);

        int nx, ny;
        x = x/720.f * getWidth();
        y = y/1280.f * getHeight();

        nx = (int)x;
        ny = (int)y;

        rX = x;
        rY = y;
        rPath.moveTo(nx, ny);

        for(String pt : pts){
            String[] point = pt.split(",");
            //Log.d("Point", point[0]+" "+ point[1]);

            x = Integer.valueOf(point[0]);
            y = Integer.valueOf(point[1]);

            x = x/720.f * getWidth();
            y = y/1280.f * getHeight();

            nx = (int)x;
            ny = (int)y;

            //rPath.lineTo(nx, ny);

            rPath.quadTo(rX, rY, (x + rX) / 2, (y + rY) / 2);
            rX = x;
            rY = y;

            invalidate();
        }

//        invalidate();
    }

}

class Sender extends AsyncTask<String, Void, Void>{
    @Override
    protected Void doInBackground(String... params) {

        String str = params[0];
        String url = "http://php-sutest.rhcloud.com/gcm_sendmsg.php";
        try{
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");

            String urlParameters = "msg="+str;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
        }
        catch(Exception e){
            Log.e("Error", e.toString());
        }



        return null;
    }

//    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
////            String newMessage = intent.getExtras().getString("message");
//
////            lblMessage.append(newMessage + "\n");
//            //Toast.makeText(, "New Message: " + newMessage, Toast.LENGTH_LONG).show();
//
//            Log.d("recv", "ts");
//        }
//    };


}
