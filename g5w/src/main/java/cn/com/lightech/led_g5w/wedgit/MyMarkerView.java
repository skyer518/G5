package cn.com.lightech.led_g5w.wedgit;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import cn.com.lightech.led_g5w.R;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

   // private TextView tvContent;
    private TextView tvDate;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        //tvContent = (TextView) findViewById(R.id.tvContent);
        tvDate = (TextView) findViewById(R.id.tv_date);
    }


    @Override
    public int getXOffset() {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset() {
        return -getHeight();
    }

    @Override
    public void refreshContent(List<Entry> entries, Highlight highlight) {
        StringBuffer sb = new StringBuffer();
        for (Entry e : entries) {
            if (e instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) e;
                sb.append("" + Utils.formatNumber(ce.getHigh(), 0, true));
            } else {
                sb.append("" + Utils.formatNumber(e.getVal(), 0, true));
            }
            sb.append("</br>");
        }
        //tvContent.setText(Html.fromHtml(sb.toString()));
    }


    @Override
    public void refreshContent(List<Entry> entries, Highlight highlight, String xLable) {
        DecimalFormat df = new DecimalFormat("###,##0.00");
        tvDate.setText(xLable);
        StringBuffer lastValueStr = new StringBuffer("<html><body><p>");

        for (Entry e : entries) {
            if (entries instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) entries;

                lastValueStr.append(df.format(new BigDecimal(ce.getHigh())));
                //tvContent.setText(+lastValueStr);
            } else {
                lastValueStr.append(df.format(new BigDecimal(e.getVal())));
            }
            lastValueStr.append("<span style=\"color:red\">/</span>");
        }
        lastValueStr.append("</p></body></html>");
       // tvContent.setText(Html.fromHtml(lastValueStr.toString()));

    }


    /**
     * TODO (描述该方法的实现功能)
     *
     * @see com.github.mikephil.charting.components.MarkerView#refreshContent(int, int, List)
     */
    @Override
    public void refreshContent(int highlightXIndex, int highlightDataSetIndex, List<? extends DataSet<? extends Entry>> e) {

        // TODO Auto-generated method stub

    }
}