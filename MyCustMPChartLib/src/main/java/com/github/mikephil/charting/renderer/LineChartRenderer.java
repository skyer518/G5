
package com.github.mikephil.charting.renderer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.CircleBuffer;
import com.github.mikephil.charting.buffer.LineBuffer;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.LineDataProvider;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class LineChartRenderer extends LineScatterCandleRadarRenderer {

    protected LineDataProvider mChart;

    /**
     * paint for the inner circle of the value indicators
     */
    protected Paint mCirclePaintInner;

    /**
     * Bitmap object used for drawing the paths (otherwise they are too long if rendered directly on
     * the canvas)
     */
    protected Bitmap mDrawBitmap;

    /**
     * on this canvas, the paths are rendered, it is initialized with the pathBitmap
     */
    protected Canvas mBitmapCanvas;

    protected Path cubicPath = new Path();
    protected Path cubicFillPath = new Path();

    protected LineBuffer[] mLineBuffers;

    protected CircleBuffer[] mCircleBuffers;

    public LineChartRenderer(LineDataProvider chart, ChartAnimator animator,
                             ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);
    }

    @Override
    public void initBuffers() {

        LineData lineData = mChart.getLineData();
        mLineBuffers = new LineBuffer[lineData.getDataSetCount()];
        mCircleBuffers = new CircleBuffer[lineData.getDataSetCount()];

        for (int i = 0; i < mLineBuffers.length; i++) {
            LineDataSet set = lineData.getDataSetByIndex(i);
            mLineBuffers[i] = new LineBuffer(set.getEntryCount() * 4 - 4);
            mCircleBuffers[i] = new CircleBuffer(set.getEntryCount() * 2);
        }
    }

    @Override
    public void drawData(Canvas c) {

        int width = (int) mViewPortHandler.getChartWidth();
        int height = (int) mViewPortHandler.getChartHeight();

        if (mDrawBitmap == null
                || (mDrawBitmap.getWidth() != width)
                || (mDrawBitmap.getHeight() != height)) {

            if (width > 0 && height > 0) {

                mDrawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                mBitmapCanvas = new Canvas(mDrawBitmap);
            } else
                return;
        }

        mDrawBitmap.eraseColor(Color.TRANSPARENT);

        LineData lineData = mChart.getLineData();

        for (LineDataSet set : lineData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }

        c.drawBitmap(mDrawBitmap, 0, 0, mRenderPaint);
    }

    protected void drawDataSet(Canvas c, LineDataSet dataSet) {

        List<Entry> entries = dataSet.getYVals();

        if (entries.size() < 1)
            return;

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setPathEffect(dataSet.getDashPathEffect());

        // if drawing cubic lines is enabled
        if (dataSet.isDrawCubicEnabled()) {

            drawCubic(c, dataSet, entries);

            // draw normal (straight) lines
        } else {
            drawLinear(c, dataSet, entries);
        }

        mRenderPaint.setPathEffect(null);
    }

    /**
     * Draws a cubic line.
     *
     * @param c
     * @param dataSet
     * @param entries
     */
    protected void drawCubic(Canvas c, LineDataSet dataSet, List<Entry> entries) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
        Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

        int minx = Math.max(dataSet.getEntryPosition(entryFrom), 0);
        int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, entries.size());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float intensity = dataSet.getCubicIntensity();

        cubicPath.reset();

        int size = (int) Math.ceil((maxx - minx) * phaseX + minx);

        if (size - minx >= 2) {

            float prevDx = 0f;
            float prevDy = 0f;
            float curDx = 0f;
            float curDy = 0f;

            Entry prevPrev = entries.get(minx);
            Entry prev = entries.get(minx);
            Entry cur = entries.get(minx);
            Entry next = entries.get(minx + 1);

            // let the spline start
            cubicPath.moveTo(cur.getXIndex(), cur.getVal() * phaseY);

            prevDx = (cur.getXIndex() - prev.getXIndex()) * intensity;
            prevDy = (cur.getVal() - prev.getVal()) * intensity;

            curDx = (next.getXIndex() - cur.getXIndex()) * intensity;
            curDy = (next.getVal() - cur.getVal()) * intensity;

            // the first cubic
            cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY,
                    cur.getXIndex() - curDx,
                    (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);

            for (int j = minx + 1, count = Math.min(size, entries.size() - 1); j < count; j++) {

                prevPrev = entries.get(j == 1 ? 0 : j - 2);
                prev = entries.get(j - 1);
                cur = entries.get(j);
                next = entries.get(j + 1);

                prevDx = (cur.getXIndex() - prevPrev.getXIndex()) * intensity;
                prevDy = (cur.getVal() - prevPrev.getVal()) * intensity;
                curDx = (next.getXIndex() - prev.getXIndex()) * intensity;
                curDy = (next.getVal() - prev.getVal()) * intensity;

                cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY,
                        cur.getXIndex() - curDx,
                        (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);
            }

            if (size > entries.size() - 1) {

                prevPrev = entries.get((entries.size() >= 3) ? entries.size() - 3
                        : entries.size() - 2);
                prev = entries.get(entries.size() - 2);
                cur = entries.get(entries.size() - 1);
                next = cur;

                prevDx = (cur.getXIndex() - prevPrev.getXIndex()) * intensity;
                prevDy = (cur.getVal() - prevPrev.getVal()) * intensity;
                curDx = (next.getXIndex() - prev.getXIndex()) * intensity;
                curDy = (next.getVal() - prev.getVal()) * intensity;

                // the last cubic
                cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY,
                        cur.getXIndex() - curDx,
                        (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);
            // create a new path, this is bad for performance
            drawCubicFill(dataSet, cubicFillPath, trans,
                    entryFrom.getXIndex(), entryFrom.getXIndex() + size);
        }

        mRenderPaint.setColor(dataSet.getColor());

        mRenderPaint.setStyle(Paint.Style.STROKE);

        trans.pathValueToPixel(cubicPath);

        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicFill(LineDataSet dataSet, Path spline, Transformer trans,
                                 int from, int to) {
        float fillMin;
        /**
         * @description Modify(add :set fill area middle value)
         * @author liuchen
         */
        if (dataSet.isDrawFillMiddleValueEnable()) {

            fillMin = dataSet.getDrawFillMiddleValue();
            if (fillMin == Float.MIN_VALUE) {
                fillMin = mChart.getYChartMin();
            }

        } else {
            fillMin = mChart.getFillFormatter()
                    .getFillLinePosition(dataSet, mChart.getLineData(), mChart.getYChartMax(),
                            mChart.getYChartMin());
        }

        spline.lineTo(to - 1, fillMin);
        spline.lineTo(from, fillMin);
        spline.close();

        /**
         * add background color change
         *
         * @author liuchen
         * @date 2015831
         */

        if (dataSet.isFillColorGradualChange()) {
            // LinearGradient linearGradient = new LinearGradient(0, 800, 0, -100, 0Xff172743,
            // 0XFFFF3B3B,Shader.TileMode.CLAMP);
            // 灰色0Xff172743, 红色0XFFFF3B3B
            float[] pts = new float[]{0, mChart.getYChartMax(), 0, mChart.getYChartMin()};

            mChart.applyTransformer(pts, 0);
            LinearGradient linearGradient = new LinearGradient(0, pts[3], 0,
                    pts[1], dataSet.getFillGradualColors(), null, TileMode.CLAMP);
            mRenderPaint.setShader(linearGradient);
        }

        mRenderPaint.setStyle(Paint.Style.FILL);

        mRenderPaint.setColor(dataSet.getFillColor());
        // filled is drawn with less alpha
        mRenderPaint.setAlpha(dataSet.getFillAlpha());

        trans.pathValueToPixel(spline);
        mBitmapCanvas.drawPath(spline, mRenderPaint);

        mRenderPaint.setAlpha(255);

        if (dataSet.isFillColorGradualChange()) {
            mRenderPaint.setShader(null);
        }
    }

    /**
     * Draws a normal line.
     *
     * @param c
     * @param dataSet
     * @param entries
     */
    protected void drawLinear(Canvas c, LineDataSet dataSet, List<Entry> entries) {

        int dataSetIndex = mChart.getLineData().getIndexOfDataSet(dataSet);

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        mRenderPaint.setStyle(Paint.Style.STROKE);

        Canvas canvas = null;

        // if the data-set is dashed, draw on bitmap-canvas
        if (dataSet.isDashedLineEnabled()) {
            canvas = mBitmapCanvas;
        } else {
            canvas = c;
        }

        Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
        Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

        int minx = Math.max(dataSet.getEntryPosition(entryFrom), 0);
        int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, entries.size());

        int range = (maxx - minx) * 4 - 4;

        LineBuffer buffer = mLineBuffers[dataSetIndex];
        buffer.setPhases(phaseX, phaseY);
        buffer.limitFrom(minx);
        buffer.limitTo(maxx);
        buffer.feed(entries);

        trans.pointValuesToPixel(buffer.buffer);

        // more than 1 color
        if (dataSet.getColors().size() > 1) {

            for (int j = 0; j < range; j += 4) {

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                // make sure the lines don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])
                        || (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1]) && !mViewPortHandler
                        .isInBoundsBottom(buffer.buffer[j + 3]))
                        || (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1]) && !mViewPortHandler
                        .isInBoundsBottom(buffer.buffer[j + 3])))
                    continue;

                // get the color that is set for this line-segment
                mRenderPaint.setColor(dataSet.getColor(j / 4 + minx));

                canvas.drawLine(buffer.buffer[j], buffer.buffer[j + 1],
                        buffer.buffer[j + 2], buffer.buffer[j + 3], mRenderPaint);
            }

        } else { // only one color per dataset

            mRenderPaint.setColor(dataSet.getColor());

            // c.drawLines(buffer.buffer, mRenderPaint);
            canvas.drawLines(buffer.buffer, 0, range,
                    mRenderPaint);
        }

        mRenderPaint.setPathEffect(null);

        // if drawing filled is enabled
        if (dataSet.isDrawFilledEnabled() && entries.size() > 0) {
            drawLinearFill(c, dataSet, entries, minx, maxx, trans);
        }
    }

    protected void drawLinearFill(Canvas c, LineDataSet dataSet, List<Entry> entries, int minx,
                                  int maxx,
                                  Transformer trans) {

        mRenderPaint.setStyle(Paint.Style.FILL);

        mRenderPaint.setColor(dataSet.getFillColor());
        // filled is drawn with less alpha
        mRenderPaint.setAlpha(dataSet.getFillAlpha());

        Path filled = generateFilledPath(
                entries,
                mChart.getFillFormatter().getFillLinePosition(dataSet, mChart.getLineData(),
                        mChart.getYChartMax(), mChart.getYChartMin()), minx, maxx);

        trans.pathValueToPixel(filled);

        c.drawPath(filled, mRenderPaint);

        // restore alpha
        mRenderPaint.setAlpha(255);
    }

    /**
     * Generates the path that is used for filled drawing.
     *
     * @param entries
     * @return
     */
    private Path generateFilledPath(List<Entry> entries, float fillMin, int from, int to) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        Path filled = new Path();
        filled.moveTo(entries.get(from).getXIndex(), fillMin);
        filled.lineTo(entries.get(from).getXIndex(), entries.get(from).getVal() * phaseY);

        // create a new path
        for (int x = from + 1, count = (int) Math.ceil((to - from) * phaseX + from); x < count; x++) {

            Entry e = entries.get(x);
            filled.lineTo(e.getXIndex(), e.getVal() * phaseY);
        }

        // close up
        filled.lineTo(
                entries.get(
                        Math.max(
                                Math.min((int) Math.ceil((to - from) * phaseX + from) - 1,
                                        entries.size() - 1), 0)).getXIndex(), fillMin);

        filled.close();

        return filled;
    }

    @Override
    public void drawValues(Canvas c) {

        if (mChart.getLineData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            List<LineDataSet> dataSets = mChart.getLineData().getDataSets();

            for (int i = 0; i < dataSets.size(); i++) {

                LineDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled())
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                // make sure the values do not interfear with the circles
                int valOffset = (int) (dataSet.getCircleSize() * 1.75f);

                if (!dataSet.isDrawCirclesEnabled())
                    valOffset = valOffset / 2;

                List<Entry> entries = dataSet.getYVals();

                Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
                Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

                int minx = Math.max(dataSet.getEntryPosition(entryFrom), 0);
                int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, entries.size());

                float[] positions = trans.generateTransformedValuesLine(
                        entries, mAnimator.getPhaseX(), mAnimator.getPhaseY(), minx, maxx);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    float val = entries.get(j / 2 + minx).getVal();

                    c.drawText(dataSet.getValueFormatter().getFormattedValue(val), x,
                            y - valOffset,
                            mValuePaint);
                }
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        drawCircles(c);
    }

    @SuppressLint("NewApi")
    protected void drawCircles(Canvas c) {

        mRenderPaint.setStyle(Paint.Style.FILL);

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        List<LineDataSet> dataSets = mChart.getLineData().getDataSets();

        for (int i = 0; i < dataSets.size(); i++) {

            LineDataSet dataSet = dataSets.get(i);

            if (!dataSet.isVisible() || !dataSet.isDrawCirclesEnabled())
                continue;

            mCirclePaintInner.setColor(dataSet.getCircleHoleColor());

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            List<Entry> entries = dataSet.getYVals();

            Entry entryFrom = dataSet.getEntryForXIndex((mMinX < 0) ? 0 : mMinX);
            Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

            int minx = Math.max(dataSet.getEntryPosition(entryFrom), 0);
            int maxx = Math.min(dataSet.getEntryPosition(entryTo) + 1, entries.size());

            CircleBuffer buffer = mCircleBuffers[i];
            buffer.setPhases(phaseX, phaseY);
            buffer.limitFrom(minx);
            buffer.limitTo(maxx);
            buffer.feed(entries);

            trans.pointValuesToPixel(buffer.buffer);

            float halfsize = dataSet.getCircleSize() / 2f;

            for (int j = 0, count = (int) Math.ceil((maxx - minx) * phaseX + minx) * 2; j < count; j += 2) {

                float x = buffer.buffer[j];
                float y = buffer.buffer[j + 1];

                if (!mViewPortHandler.isInBoundsRight(x))
                    break;

                // make sure the circles don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                    continue;

                /**
                 * @description Modify(add :set fill area middle value)
                 * @author liuchen 2015
                 */

                if (dataSet.isSetLastPointIndicator()) {
//                     System.out.println("指示点:"+mChart.getDrawLastIndicator());
                    if (!mChart.getDrawLastIndicator()) {
                        return;
                    }
                    if (j == count - 2) {
                        Entry entry = dataSet.getEntryForXIndex(j / 2 + minx);

                        float lastValue = entry.getVal();

                        DecimalFormat df = new DecimalFormat("###,##0.00");
                        String lastValueStr = dataSet.getLastPointIndecatorUnit()
                                + df.format(new BigDecimal(lastValue));

                        Bitmap indicatorPoint = dataSet.getLastPointIndecator();
                        if (entry.getVal() > 0) {
                            indicatorPoint = dataSet.getRedPoint();
                        } else {
                            indicatorPoint = dataSet.getGreenPoint();
                        }

                        int BHeightP = indicatorPoint.getHeight();
                        int BWidthP = indicatorPoint.getWidth();
                        // BitmapShader bitmapShader = new BitmapShader(indicatorPoint,
                        // TileMode.CLAMP,
                        // TileMode.CLAMP);
                        c.drawBitmap(indicatorPoint, x - BWidthP / 2, y - BHeightP / 2,
                                mRenderPaint);
                        Paint paint = new Paint();
                        paint.setColor(Color.WHITE);
                        paint.setStrokeWidth(Utils.convertDpToPixel(3f));
                        paint.setTypeface(Typeface.DEFAULT);
                        // Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
                        paint.setTextSize(Utils.convertDpToPixel(18f));
                        // paint.setTextSize(20);
                        FontMetrics fontMetrics = paint.getFontMetrics();
                        lastValueStr.length();
                        int txtWidth = (int) paint.measureText(lastValueStr);

                        Bitmap indicatorIcon = dataSet.getLastPointIndecator();
                        int BHeight = indicatorIcon.getHeight();
                        int BWidth = indicatorIcon.getWidth();

                        int txtOffset = (int) Utils.convertDpToPixel(15f);
                        int ABHeight = BHeight;
                        int ABWidth = txtWidth + txtOffset * 2;
                        int iconOffset = ((int) Utils.convertDpToPixel(7f) * ABWidth) / BWidth;
                        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(indicatorIcon
                                , ABWidth
                                , ABHeight
                                , true);

                        c.drawBitmap(createScaledBitmap, x - ABWidth + iconOffset, y - ABHeight,
                                mRenderPaint);
                        c.drawText(lastValueStr + "", x - ABWidth + txtOffset + iconOffset, y
                                - (fontMetrics.bottom - fontMetrics.top), paint);
                        return;
                    } else {
                        continue;
                    }
                }

                int circleColor;

                if (dataSet.isDrawDoubleColor()) {
                    Entry entry = dataSet.getEntryForXIndex(j / 2 + minx);
                    int circleColorH = dataSet.getCircleColor(0);
                    int circleColorL = dataSet.getCircleColor(1);
                    if (entry.getVal() > 0) {
                        mRenderPaint.setColor(circleColorH);
                        circleColor = circleColorH;
                    } else {
                        mRenderPaint.setColor(circleColorL);
                        circleColor = circleColorL;
                    }
                } else {
                    circleColor = dataSet.getCircleColor(j / 2 + minx);
                    mRenderPaint.setColor(circleColor);
                }

                c.drawCircle(x, y, dataSet.getCircleSize(),
                        mRenderPaint);

                if (dataSet.isDrawCircleHoleEnabled()
                        && circleColor != mCirclePaintInner.getColor())
                    c.drawCircle(x, y,
                            halfsize,
                            mCirclePaintInner);
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        for (int i = 0; i < indices.length; i++) {

            LineDataSet set = mChart.getLineData().getDataSetByIndex(indices[i]
                    .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());

            int xIndex = indices[i].getXIndex(); // get the
            // x-position

            if (xIndex > mChart.getXChartMax() * mAnimator.getPhaseX())
                continue;

            final float yVal = set.getYValForXIndex(xIndex);
            if (yVal == Float.NaN)
                continue;

            float y = yVal * mAnimator.getPhaseY(); // get
            // the
            // y-position

            float[] pts = new float[]{
                    xIndex, mChart.getYChartMax(), xIndex, mChart.getYChartMin(),
                    mChart.getXChartMin(), y,
                    mChart.getXChartMax(), y
            };

            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);

//            Log.i("chart_info", "inner图表最大值:"+mChart.getYChartMax()+",inner图表最小值:"+mChart.getYChartMin());
//            Log.i("chart_info", "inner图表最大值----:"+pts[1]+",inner图表最小值:"+pts[3]);
            // draw the lines
            drawHighlightLines(c, pts, set.isHorizontalHighlightIndicatorEnabled(),
                    set.isVerticalHighlightIndicatorEnabled());
        }
    }
}
