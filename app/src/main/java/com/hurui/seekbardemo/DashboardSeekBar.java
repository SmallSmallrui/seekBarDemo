package com.hurui.seekbardemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.math.BigDecimal;

/**
 * Created by lenovo on 2017/3/17.
 */

public class DashboardSeekBar extends View {

    public interface OnSeekBarChangeListener {
        void onStartTrackingTouch();

        void onStopTrackingTouch(float persent, int podu);
    }
    private OnSeekBarChangeListener seekBarChangeListener;

    public void setSeekBarChangeListener(OnSeekBarChangeListener seekBarChangeListener) {
        this.seekBarChangeListener = seekBarChangeListener;
    }

    private DashboardViewAttr dashboardViewattr; //大圆的背景颜色
    private int progressStrokeWidth;//进度弧的宽度
    private float maxNum;
    private int mTikeCount;//刻度的个数
    private int tikeGroup = 3;
    private CharSequence[] tikeStrArray = null;
    private int progressColor;
    private int wCricleRadius ;
    private float shuDuSize ;
    private int poDuMax = 15;
    private int currentPoDu ;

    //画笔
    private Paint paintOutCircle;
    private Paint paintProgressBackground;
    private Paint paintProgress;
    private Paint paintShuDuText ;
    private Paint paintNum;
    private Paint paintTikeStr;
    private Paint paintYuanHu ;
    private Paint paintPoDu ;
    private RectF rectF1, rectF2 , rectf3 ;
    private int ShuDuTextSize ;
    private int poDuTextSize ;

    private float maxShudu ; //用来做限制.最大的速度


    private int OFFSET = 5;
    private int START_ARC = 180;
    private int DURING_ARC = 180;


    private Context mContext;
    private int mWidth, mHight;
    float percent;


    private BigDecimal nNum ;


    public void setMaxShudu(float maxShudu) {
        this.maxShudu = maxShudu;
    }

    public void setPoDuMax(int poDuMax) {
        this.poDuMax = poDuMax;
    }

    public DashboardSeekBar(Context context) {
        this(context, null);
    }

    public DashboardSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashboardSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dashboardViewattr = new DashboardViewAttr(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context ;
        initAttr();
        initPaint();
    }

    private void initAttr(){
        tikeGroup = 5; // 默认1个长刻度间隔4个短刻度，加起来一组5
        mTikeCount = 6 * tikeGroup + 1;


        progressStrokeWidth = dashboardViewattr.getProgressStrokeWidth();

        maxNum = dashboardViewattr.getMaxNumber();
        maxShudu = maxNum;
        progressColor = dashboardViewattr.getProgressColor();
        if (dashboardViewattr.getPadding() == 0) {
            OFFSET = progressStrokeWidth + 10;
        } else {
            OFFSET = dashboardViewattr.getPadding();
        }


        nNum = BigDecimal.valueOf(maxNum).divide(BigDecimal.valueOf(30));
        shuDuSize = dashboardViewattr.getShuDuStrSize();

        // 开启硬件加速
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        }
    }

    private void initPaint(){
        //初始化画笔
        /**
         * 外圆
         */
        paintOutCircle = new Paint();
        paintOutCircle.setAntiAlias(true);
        paintOutCircle.setStyle(Paint.Style.FILL);
        paintOutCircle.setStrokeWidth(2);
        paintOutCircle.setColor(dashboardViewattr.getBackground());
        paintOutCircle.setDither(true);
        //进度条的背景颜色
        paintProgressBackground = new Paint();
        paintProgressBackground.setAntiAlias(true);
        paintProgressBackground.setStrokeWidth(progressStrokeWidth);
        paintProgressBackground.setStyle(Paint.Style.STROKE);
        paintProgressBackground.setStrokeCap(Paint.Cap.ROUND);
        paintProgressBackground.setColor(dashboardViewattr.getBackground());
        paintProgressBackground.setDither(true);
        //进度条的颜色
        paintProgress = new Paint();
        paintProgress.setAntiAlias(true);
        paintProgress.setStrokeWidth(progressStrokeWidth);
        paintProgress.setStyle(Paint.Style.STROKE);
        paintProgress.setStrokeCap(Paint.Cap.ROUND);
        paintProgress.setColor(progressColor);
        paintProgress.setDither(true);

        paintNum = new Paint();
        paintNum.setAntiAlias(true);
        paintNum.setColor(getResources().getColor(R.color.white));
        paintNum.setStrokeWidth(2);
        paintNum.setStyle(Paint.Style.FILL);
        paintNum.setDither(true);

        paintTikeStr = new Paint();
        paintTikeStr.setAntiAlias(true);
        paintTikeStr.setStyle(Paint.Style.FILL);
        paintTikeStr.setTextAlign(Paint.Align.LEFT);
        paintTikeStr.setColor(dashboardViewattr.getTikeStrColor());
        paintTikeStr.setTextSize(dashboardViewattr.getTikeStrSize());

        //速度显示的Text
        paintShuDuText = new Paint();
        paintShuDuText.setAntiAlias(true);
        paintShuDuText.setStyle(Paint.Style.FILL);
        paintShuDuText.setTextAlign(Paint.Align.LEFT);
        paintShuDuText.setColor(dashboardViewattr.getTikeStrColor());
        paintShuDuText.setTextSize(shuDuSize);

        //相交的那一段圆弧
        paintYuanHu = new Paint();
        paintYuanHu.setAntiAlias(true);
        paintYuanHu.setStyle(Paint.Style.FILL_AND_STROKE);
        paintYuanHu.setColor(dashboardViewattr.getLittebackgroud());

        //坡度
        paintPoDu = new Paint();
        paintPoDu.setAntiAlias(true);
        paintPoDu.setStyle(Paint.Style.FILL);
        paintPoDu.setTextAlign(Paint.Align.LEFT);
        paintPoDu.setColor(getResources().getColor(R.color.white));
        paintPoDu.setTextSize(PxUtils.spToPx(20 , getContext()));
        poDuTextSize = PxUtils.spToPx(20 , getContext());
    }


    //计算控件的高宽
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int realWidth = startMeasure(widthMeasureSpec);
        int realHeight = startMeasure(heightMeasureSpec);

        setMeasuredDimension(realWidth, realHeight);
    }

    //若给的是wrap_context 则为200dp
    private int startMeasure(int msSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(msSpec);
        int size = MeasureSpec.getSize(msSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = PxUtils.dpToPx(200, mContext);
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHight = getHeight();
        wCricleRadius = (mWidth > mHight ? mHight : mWidth) / 2 ;
        rectF2 = new RectF((-mWidth / 2) + OFFSET + getPaddingLeft(), getPaddingTop() - (mWidth / 2) + OFFSET,
                (mWidth / 2) - getPaddingRight() - OFFSET,
                (mWidth / 2) - getPaddingBottom() - OFFSET);
        rectF1 = new RectF(-mWidth /2 , ((float) Math.sqrt(2)-1)*mWidth/2 + 1, mWidth/2 , ((float) Math.sqrt(2)+1)*mWidth/2 + 1);
        rectf3 = new RectF(-mWidth /2 , -mWidth/2 , mWidth/2 , mWidth/2);
    };

    float starteventX ;
    float starteventY ;
    private float mPar = 0 ;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                starteventX = event.getX();
                starteventY = event.getY();
                if(null != seekBarChangeListener){
                    seekBarChangeListener.onStartTrackingTouch();
                }
                break ;

            case MotionEvent.ACTION_MOVE:

                break ;

            case MotionEvent.ACTION_UP:
                float endeventX = event.getX() ;
                if(starteventY < wCricleRadius) {
                    if (endeventX - starteventX > wCricleRadius * 4 / 3) {
                        if (percent < 30) {
                            percent = BigDecimal.valueOf(percent + 3)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        }
                    } else if (endeventX - starteventX > wCricleRadius * 2 / 6) {
                        if (percent < 30) {
                            percent = BigDecimal.valueOf(percent + 1)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        }
                    } else if (endeventX - starteventX < -wCricleRadius * 4 / 3) {
                        if (percent > 0) {
                            percent = BigDecimal.valueOf(percent - 3)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        }
                    } else if (endeventX - starteventX < -wCricleRadius * 2 / 6) {
                        if (percent > 0) {
                            percent = BigDecimal.valueOf(percent - 1)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        }
                    } else {
                        Toast.makeText(mContext, "滑动距离还不够", Toast.LENGTH_SHORT).show();
                    }
                    // setPercent(mPar);
                    if (percent > maxShudu) {
                        percent = maxShudu;
                        Toast.makeText(mContext, "抱歉,最大速度只能为"+maxShudu, Toast.LENGTH_SHORT).show();
                    }
                    if (percent < 0f) {
                        percent = 0f;
                    }
                    setPercent(percent);
                }else{
                    if (endeventX - starteventX > wCricleRadius * 4 / 3) {
                        currentPoDu = currentPoDu + 3 ;
                    } else if (endeventX - starteventX > wCricleRadius * 2 / 6) {
                        currentPoDu = currentPoDu + 1 ;
                    } else if (endeventX - starteventX < -wCricleRadius * 4 / 3) {
                        currentPoDu = currentPoDu - 3 ;
                    } else if (endeventX - starteventX < -wCricleRadius * 2 / 6) {
                       currentPoDu = currentPoDu -1 ;
                    } else {
                        Toast.makeText(mContext, "滑动距离还不够", Toast.LENGTH_SHORT).show();
                    }
                    // setPercent(mPar);
                    if (currentPoDu > poDuMax) {
                        currentPoDu = poDuMax;
                        Toast.makeText(mContext, "抱歉,最大坡度只能为"+poDuMax, Toast.LENGTH_SHORT).show();
                    }
                    if (currentPoDu < 0) {
                        currentPoDu = 0;
                    }
                    setPodu(currentPoDu);
                }
                if(null != seekBarChangeListener){
                    seekBarChangeListener.onStopTrackingTouch(percent , currentPoDu);
                }
                break ;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置百分比
     * @param percent
     */
    public void setPercent(float percent) {
        this.percent = percent ;
        invalidate();
    }
    /**
     * 设置坡度
     */
    public void setPodu(int currentPodu){
        this.currentPoDu = currentPodu ;
        invalidate();
    }
    /**
     * 画板的绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //this.percent = percent / 100f;
        canvas.translate(mWidth / 2, mHight / 2);//移动坐标原点到中心
        //最外层的圆
        canvas.drawCircle(0,0,wCricleRadius, paintOutCircle);
        //绘制刻度
        drawerNum(canvas);
        //绘制指针和进度弧
        drawProgress(canvas, percent);
        //绘制速度显示
        drawShuDuText(canvas , percent);
        //相交的那一段圆
        canvas.drawArc(rectF1 , 225 , 90 ,false , paintYuanHu);
        canvas.drawArc(rectf3 , 45 , 90 , false, paintYuanHu);

        //绘制坡度显示
        drawerPoDu(canvas , currentPoDu);
    }

    private void drawerPoDu(Canvas canvas, int currentPoDu) {
        float textWidth = paintPoDu.measureText("坡度   " + currentPoDu + "   度");
        canvas.drawText("坡度   " + currentPoDu + "   度" ,  - textWidth / 2, (float) (poDuTextSize / 2 + Math.sqrt(2)/4 *mWidth), paintPoDu);
    }

    private void drawShuDuText(Canvas canvas , float percent){
        float textWidth = paintShuDuText.measureText("" + percent);
        canvas.drawText(percent + "",  - textWidth / 2, ShuDuTextSize / 2, paintShuDuText);
    }

    //绘制指针和进度弧
    private void drawProgress(Canvas canvas, float percent) {

        canvas.drawArc(rectF2, START_ARC, DURING_ARC, false, paintProgressBackground);

        canvas.drawArc(rectF2, START_ARC, percent * DURING_ARC / 30, false, paintProgress);
    }

    //绘制刻度
    private void drawerNum(Canvas canvas) {
        canvas.save(); //记录画布状态
        //canvas.rotate(-(180 - START_ARC + 90), 0, 0);
        int numY = -mHight / 2 + OFFSET + progressStrokeWidth ;
        float rAngle = DURING_ARC / ((mTikeCount - 1) * 1.0f); //n根线，只需要n-1个区间
        for (int i = 0; i < mTikeCount; i++) {
            canvas.save(); //记录画布状态
            canvas.rotate(rAngle * i + 270, 0, 0);
            if (i == 0 || i % tikeGroup == 0) {
                canvas.drawLine(0, numY + 5, 0, numY + 25, paintNum);//画长刻度线
                if (6 > (i % tikeGroup)) {
                    String text = nNum.multiply(BigDecimal.valueOf(i)).toString();
                    Paint.FontMetricsInt fontMetrics = paintTikeStr.getFontMetricsInt();
                    int baseline = ((numY + 40) + (fontMetrics.bottom - fontMetrics.top) / 2);
                    canvas.drawText(text, -getTextViewLength(paintTikeStr, text) / 2, baseline, paintTikeStr);
                }
            } else {
                canvas.drawLine(0, numY + 5, 0, numY + 15, paintNum);//画短刻度线
            }

            canvas.restore();
        }
        canvas.restore();

    }

    private float getTextViewLength(Paint paint, String text) {
        if (TextUtils.isEmpty(text)) return 0;
        float textLength = paint.measureText(text);
        return textLength;
    }

}
