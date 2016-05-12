
package com.github.mikephil.charting.listener;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.List;

/**
 * Listener for callbacks when selecting values inside the chart by
 * touch-gesture.
 *
 * @author Philipp Jahoda
 */
public interface OnChartValueSelectedListener {

    /**
     * Called when a value has been selected inside the chart.
     *
     * @param e            The selected Entry.
     * @param dataSetIndex The index in the datasets array of the data object
     *                     the Entrys DataSet is in.
     * @param h            the corresponding highlight object that contains information
     *                     about the highlighted position
     */
    public void onValueSelected(List<Entry> e, int dataSetIndex, Highlight h);

    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    public void onNothingSelected();
}
