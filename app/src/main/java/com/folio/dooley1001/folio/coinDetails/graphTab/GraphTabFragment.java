package com.folio.dooley1001.folio.coinDetails.graphTab;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.folio.dooley1001.folio.R;
import com.folio.dooley1001.folio.coinDetails.CustomView;
import com.folio.dooley1001.folio.formatters.MonthDayDateFormatter;
import com.folio.dooley1001.folio.formatters.MonthYearFormatter;
import com.folio.dooley1001.folio.formatters.TimeDateFormatter;
import com.folio.dooley1001.folio.models.easyrest.CoinMarketCapCoinData;
import com.folio.dooley1001.folio.models.easyrest.CMCCoinParcelable;
import com.folio.dooley1001.folio.api.CoinMarketCapService;
import com.folio.dooley1001.folio.dataBase.CurrencyFormat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;                            //Inspiration: https://github.com/PhilJay/MPAndroidChart/tree/master/MPChartLib/src/main/java/com/github/mikephil/charting
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import static android.content.Context.MODE_PRIVATE;
import static com.folio.dooley1001.folio.api.CoinMarketCapService.COINMARKETCAP_CHART_URL_ALL_DATA;
import static com.folio.dooley1001.folio.api.CoinMarketCapService.COINMARKETCAP_CHART_URL_WINDOW;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 *
 * placeholder fragment containing a simple view.
 */
public class GraphTabFragment extends Fragment implements OnChartValueSelectedListener {

    public final static String BASE_CMC_SOURCE_URL = "https://coinmarketcap.com/currencies/";
    private String cryptoID; //id of the crypto/coin selected
    private int percentageColour;
    private int chartFillingColour; //int we will used for chart color
    private int chartOutlineColour;
    private LineChart chartLine;  //the chart line
    private View rootView;
    private CustomView viewPager;

    //formaters for the timesframes
    private IAxisValueFormatter XAxisFormatter;
    public final IAxisValueFormatter monthSlashDayXAxisFormatter = new MonthDayDateFormatter();
    public final TimeDateFormatter dayCommaTimeDateFormatter = new TimeDateFormatter();
    public final MonthYearFormatter monthSlashYearFormatter = new MonthYearFormatter();

    private String currentTimeWindow = "";
    private SingleSelectToggleGroup buttonGroup;
    public static String CURRENT_CHART_URL;
    public final static DecimalFormat rawNumberFormat = new DecimalFormat("#,###.##");
    private LockableNestedScrollView nestedScrollView;
    private int displayWidth;
    private ProgressBar chartProgressBar;
    private String currencySymbol;
    private CurrencyFormat currencyFormatter;
    private SharedPreferences sharedPreferences;
    NumberFormat chartUSDPriceFormat = NumberFormat.getInstance();

    SimpleDateFormat fullDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    public static final String SHAREDPREF_SETTINGS = "Folio_settings";
    public static final String CHART_SPINNER_SETTING = "chart_spinner_setting";
    public static final String CRYPTO_SYMBOL = "coin_symbol";
    public static final String ARG_ID = "ID";
    public static final String COIN_OBJECT = "COIN_OBJECT";

    public GraphTabFragment() {
    }
    /**
     * Returns a new instance of this fragment for the given coin selection
     */
    public static GraphTabFragment newInstance(String symbol, String id) {
        GraphTabFragment fragment = new GraphTabFragment();
        Bundle args = new Bundle();
        args.putString(CRYPTO_SYMBOL, symbol);
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    //colors of the chart based on the precentage change
    public void setColors(float percentChange) {
        if (percentChange >= 0) {

            chartFillingColour = ResourcesCompat.getColor(getActivity().getResources(), R.color.materialLightGreen, null);
            chartOutlineColour = ResourcesCompat.getColor(getActivity().getResources(), R.color.darkGreen, null);
            percentageColour = ResourcesCompat.getColor(getActivity().getResources(), R.color.percentPositiveGreen, null);
        }

        else {
            chartFillingColour = ResourcesCompat.getColor(getActivity().getResources(), R.color.materialLightRed, null);
            chartOutlineColour = ResourcesCompat.getColor(getActivity().getResources(), R.color.darkRed, null);
            percentageColour = ResourcesCompat.getColor(getActivity().getResources(), R.color.percentNegativeRed, null);
        }
    }
    //setting up the chart for display
    public void setUpChart() {

        XAxis xAxis = chartLine.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setAvoidFirstLastClipping(true);

        chartLine.getAxisLeft().setEnabled(true);
        chartLine.getAxisLeft().setDrawGridLines(false);
        chartLine.getXAxis().setDrawGridLines(false);
        chartLine.getAxisRight().setEnabled(false);
        chartLine.getLegend().setEnabled(false);
        chartLine.setDoubleTapToZoomEnabled(false);
        chartLine.setScaleEnabled(false);
        chartLine.getDescription().setEnabled(false);
        chartLine.setContentDescription("");
        chartLine.setNoDataText(getString(R.string.noChartDataString));
        chartLine.setNoDataTextColor(R.color.darkRed);
        chartLine.setOnChartValueSelectedListener(this);
        chartLine.setOnChartGestureListener(new OnChartGestureListener() {

            /*chart methods
             *
             * When chart it tapped lock the scrollview so the chart does not move with a touch
             */
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                YAxis yAxis = chartLine.getAxisLeft();
                // Allow scrolling in the right and left margins
                if (me.getX() > yAxis.getLongestLabel().length() * yAxis.getTextSize() &&
                        me.getX() < displayWidth - chartLine.getViewPortHandler().offsetRight()) {
                    viewPager.setPagingEnabled(false);
                    nestedScrollView.setScrollingEnabled(false);
                }
            }
            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                viewPager.setPagingEnabled(true);
                nestedScrollView.setScrollingEnabled(true);
            }
            //empty methods that come with the chart libary
            @Override
            public void onChartLongPressed(MotionEvent me) {
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
            }
            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            }
            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            }
            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
            }
        });
    }
    public LineDataSet setUpLineDataSet(List<Entry> entries) {

        LineDataSet dataSet = new LineDataSet(entries, "Price");
        dataSet.setColor(chartOutlineColour);
        dataSet.setFillColor(chartFillingColour);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(chartOutlineColour);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);
        dataSet.setCircleRadius(1);
        dataSet.setHighlightLineWidth(2);
        dataSet.setHighlightEnabled(true);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setHighLightColor(chartOutlineColour); // color for highlight indicator
        return dataSet;
    }

    public void getCMCChart() {

        final TextView percentChangeText = rootView.findViewById(R.id.percent_change);
        final TextView currPriceText = rootView.findViewById(R.id.current_price);

        chartLine.setEnabled(true);
        chartLine.clear();

        chartProgressBar.setVisibility(View.VISIBLE);
        CoinMarketCapService.getCMCChartData(getActivity(), cryptoID, new afterTaskCompletion<CoinMarketCapCoinData>() {
            @Override
            public void onTaskCompleted(CoinMarketCapCoinData cmcChartData) {
                List<Entry> closePrices = new ArrayList<>();
                if (currencySymbol.equals("USD")) {
                    for (List<Float> priceTimeUnit : cmcChartData.getPriceUSD()) {
                        closePrices.add(new Entry(priceTimeUnit.get(0), priceTimeUnit.get(1)));
                    }
                } else {
                    for (List<Float> priceTimeUnit : cmcChartData.getPriceBTC()) {
                        closePrices.add(new Entry(priceTimeUnit.get(0), priceTimeUnit.get(1)));
                    }
                }
                if (closePrices.size() == 0) {
                    chartLine.setData(null);
                    chartLine.setEnabled(false);
                    chartLine.invalidate();
                    percentChangeText.setText("");
                    currPriceText.setText("");
                    chartLine.setNoDataText(getString(R.string.noChartDataString));
                    chartProgressBar.setVisibility(View.GONE);
                    return;
                }
                XAxis xAxis = chartLine.getXAxis();
                xAxis.setValueFormatter(XAxisFormatter);
                TextView currentPriceTextView = rootView.findViewById(R.id.current_price);
                float currentPrice = closePrices.get(closePrices.size() - 1).getY();
                TextView chartDateTextView = rootView.findViewById(R.id.graphFragmentDateTextView);
                chartDateTextView.setText(getFormattedFullDate(closePrices.get(closePrices.size() - 1).getX()));
                if (currencySymbol.equals("USD")) {
                    currentPriceTextView.setText(String.format(getString(R.string.unrounded_usd_chart_price_format), String.valueOf(currentPrice)));
                } else {
                    currentPriceTextView.setText(currencyFormatter.format(currentPrice, "BTC"));
                }
                currentPriceTextView.setTextColor(Color.BLACK);
                float firstPrice = closePrices.get(0).getY();
                // handle the graph where user selects 3months for example but we have only data for the past month
                for (Entry e: closePrices) {
                    if (firstPrice != 0) {
                        break;
                    } else {
                        firstPrice = e.getY();
                    }
                }
                float difference = (currentPrice - firstPrice);
                float percentChange = (difference / firstPrice) * 100;
                if (percentChange < 0) {
                    if (currencySymbol.equals("USD")) {
                        percentChangeText.setText(String.format(getString(R.string.negative_variable_pct_change_with_dollars_format), currentTimeWindow, percentChange, Math.abs(difference)));
                    } else {
                        percentChangeText.setText(String.format(getString(R.string.negative_variable_pct_change_without_dollars_format), currentTimeWindow, percentChange));
                    }
                } else {
                    if (currencySymbol.equals("USD")) {
                        percentChangeText.setText(String.format(getString(R.string.positive_variable_pct_change_with_dollars_format), currentTimeWindow, percentChange, Math.abs(difference)));
                    } else {
                        percentChangeText.setText(String.format(getString(R.string.positive_variable_pct_change_without_dollars_format), currentTimeWindow, percentChange));
                    }
                }

                setColors(percentChange);
                percentChangeText.setTextColor(percentageColour);
                LineDataSet dataSet = setUpLineDataSet(closePrices);
                LineData lineData = new LineData(dataSet);
                chartLine.setData(lineData);
                chartLine.animateX(600);
                chartProgressBar.setVisibility(View.GONE);
            }
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
                chartLine.setNoDataText(getString(R.string.noChartDataString));
                chartProgressBar.setVisibility(View.GONE);
            }
        }, true);
    }
    //Methods for timeframes selected along the top as part of the toggle buttons
    public void setDayChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COINMARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = getString(R.string.oneDay);
        XAxisFormatter = dayCommaTimeDateFormatter;
    }

    public void setWeekChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COINMARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = getString(R.string.Week);
        XAxisFormatter = monthSlashDayXAxisFormatter;
    }

    public void setMonthChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COINMARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = getString(R.string.Month);
        XAxisFormatter = monthSlashDayXAxisFormatter;
    }

    public void setThreeMonthChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -3);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COINMARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = getString(R.string.threeMonth);
        XAxisFormatter = monthSlashDayXAxisFormatter;
    }

    public void setYearChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COINMARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = getString(R.string.Year);
        XAxisFormatter = monthSlashYearFormatter;
    }

    public void setAllTimeChecked() {
        currentTimeWindow = getString(R.string.AllTime);
        CURRENT_CHART_URL = String.format(COINMARKETCAP_CHART_URL_ALL_DATA, cryptoID);
        XAxisFormatter = monthSlashYearFormatter;
    }

    // setting up our table with reletive textviews
    public void setTable(CMCCoinParcelable coinObject, View rootVeiw) {
        String usdFormat = getString(R.string.usd_format);
        String negativePctFormat = getString(R.string.negative_pct_format);
        String positivePctFormat = getString(R.string.positive_pct_format);

        int negativeRedColor = getResources().getColor(R.color.percentNegativeRed);
        int positiveGreenColor = getResources().getColor(R.color.percentPositiveGreen);
        TextView nameTextView = rootVeiw.findViewById(R.id.tableNameDataTextView);
        if (coinObject.getName() == null) {
            nameTextView.setText("N/A");
        } else {
            nameTextView.setText(coinObject.getName());
        }

        TextView priceUSDTextView = rootVeiw.findViewById(R.id.tablePriceUSDDataTextView);
        if (coinObject.getPrice_usd() == null) {
            priceUSDTextView.setText("N/A");
        } else {
            priceUSDTextView.setText(String.format(usdFormat, Double.parseDouble(coinObject.getPrice_usd())));
        }

        TextView priceBTCTextView = rootVeiw.findViewById(R.id.tablePriceBTCDataTextView);
        if (coinObject.getPrice_btc() == null) {
            priceBTCTextView.setText("N/A");
        } else {
            priceBTCTextView.setText(String.format(getString(R.string.btc_format), coinObject.getPrice_btc()));
        }

        TextView volumeTextView = rootVeiw.findViewById(R.id.tableVolUSDDataTextView);
        if (coinObject.getVolume_usd_24h() == null) {
            volumeTextView.setText("N/A");
        } else {
            volumeTextView.setText(String.format(usdFormat, Double.parseDouble(coinObject.getVolume_usd_24h())));
        }

        TextView mktCapTextView = rootVeiw.findViewById(R.id.tableMktCapDataTextView);
        if (coinObject.getMarket_cap_usd() == null) {
            mktCapTextView.setText("N/A");
        } else {
            mktCapTextView.setText(String.format(usdFormat, Double.parseDouble(coinObject.getMarket_cap_usd())));
        }

        TextView availSupplyTextView = rootVeiw.findViewById(R.id.tableAvailableSupplyDataTextView);
        if (coinObject.getAvailable_supply() == null) {
            availSupplyTextView.setText("N/A");
        } else {
            availSupplyTextView.setText(rawNumberFormat.format(Double.parseDouble(coinObject.getAvailable_supply())));
        }

        TextView totalSupplyTextView = rootVeiw.findViewById(R.id.tableTotalSupplyDataTextView);
        if (coinObject.getTotal_supply() == null) {
            totalSupplyTextView.setText("N/A");
        } else {
            totalSupplyTextView.setText(rawNumberFormat.format(Double.parseDouble(coinObject.getTotal_supply())));
        }

        TextView maxSupplyTextView = rootVeiw.findViewById(R.id.tableMaxSupplyDataTextView);
        if (coinObject.getMax_supply() == null) {
            maxSupplyTextView.setText("N/A");
        } else {
            maxSupplyTextView.setText(rawNumberFormat.format(Double.parseDouble(coinObject.getMax_supply())));
        }

        TextView oneHrChangeTextView = rootVeiw.findViewById(R.id.table1hrChangeDataTextView);
        if (coinObject.getPercent_change_1h() == null) {
            oneHrChangeTextView.setText("N/A");
        } else {
            double amount = Double.parseDouble(coinObject.getPercent_change_1h());
            if (amount >= 0) {
                oneHrChangeTextView.setText(String.format(positivePctFormat, amount));
                oneHrChangeTextView.setTextColor(positiveGreenColor);
            } else {
                oneHrChangeTextView.setText(String.format(negativePctFormat, amount));
                oneHrChangeTextView.setTextColor(negativeRedColor);
            }
        }

        TextView dayChangeTextView = rootVeiw.findViewById(R.id.table24hrChangeDataTextView);
        if (coinObject.getPercent_change_24h() == null) {
            dayChangeTextView.setText("N/A");
        } else {
            double amount = Double.parseDouble(coinObject.getPercent_change_24h());
            if (amount >= 0) {
                dayChangeTextView.setText(String.format(positivePctFormat, amount));
                dayChangeTextView.setTextColor(positiveGreenColor);
            } else {
                dayChangeTextView.setText(String.format(negativePctFormat, amount));
                dayChangeTextView.setTextColor(negativeRedColor);
            }
        }

        TextView weekChangeTextView = rootVeiw.findViewById(R.id.tableWeekChangeDataTextView);
        if (coinObject.getPercent_change_7d() == null) {
            weekChangeTextView.setText("N/A");
        } else {
            double amount = Double.parseDouble(coinObject.getPercent_change_7d());
            if (amount >= 0) {
                weekChangeTextView.setText(String.format(positivePctFormat, amount));
                weekChangeTextView.setTextColor(positiveGreenColor);
            } else {
                weekChangeTextView.setText(String.format(negativePctFormat, amount));
                weekChangeTextView.setTextColor(negativeRedColor);
            }
        }
    }
    // END OF TABLE

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        chartLine = rootView.findViewById(R.id.chart);

        chartUSDPriceFormat = NumberFormat.getInstance();
        chartUSDPriceFormat.setMaximumFractionDigits(10);
        setUpChart();

        currencyFormatter = CurrencyFormat.getInstance(getContext());
        WindowManager mWinMgr = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        displayWidth = mWinMgr.getDefaultDisplay().getWidth();
        chartProgressBar = rootView.findViewById(R.id.chartProgressSpinner);

        //button which directs to coinmarketcap
        Button redirectButton = rootView.findViewById(R.id.sourceButton);
        redirectButton.setPaintFlags(redirectButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        sharedPreferences = getContext().getSharedPreferences(SHAREDPREF_SETTINGS, MODE_PRIVATE);
        Spinner chartCurrencySelector = rootView.findViewById(R.id.chartCurrencySelectSpinnr);

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.chart_spinner_options));

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        currencySymbol = sharedPreferences.getString(CHART_SPINNER_SETTING, "USD");
        chartCurrencySelector.setAdapter(spinnerArrayAdapter);

        if (currencySymbol.equals("USD")) {
            chartCurrencySelector.setSelection(0);
        } else {
            chartCurrencySelector.setSelection(1);
        }


        //method for our dropdown selection
        chartCurrencySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currencySymbol = spinnerArrayAdapter.getItem(position);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CHART_SPINNER_SETTING, currencySymbol);
                editor.apply();
                getCMCChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewPager = (CustomView) container;
        nestedScrollView = rootView.findViewById(R.id.graphFragmentNestedScrollView);
        buttonGroup = rootView.findViewById(R.id.chart_interval_button_grp);
        cryptoID = getArguments().getString(ARG_ID);

        setDayChecked(Calendar.getInstance());
        buttonGroup.check(R.id.dayButton);
        currentTimeWindow = getString(R.string.oneDay);

        //click listerner for our refirect button to cmc
        redirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                //launch web intent
                customTabsIntent.launchUrl(getActivity(), Uri.parse(BASE_CMC_SOURCE_URL + cryptoID));
            }
        });
        // cases to see which option on calender is selected
        buttonGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                Calendar.getInstance();
                switch (checkedId) {
                    case R.id.dayButton:
                        setDayChecked(Calendar.getInstance());
                        getCMCChart();
                        break;
                    case R.id.weekButton:
                        setWeekChecked(Calendar.getInstance());
                        getCMCChart();
                        break;
                    case R.id.monthButton:
                        setMonthChecked(Calendar.getInstance());
                        getCMCChart();
                        break;
                    case R.id.threeMonthButton:
                        setThreeMonthChecked(Calendar.getInstance());
                        getCMCChart();
                        break;
                    case R.id.yearButton:
                        setYearChecked(Calendar.getInstance());
                        getCMCChart();
                        break;
                    case R.id.allTimeButton:
                        setAllTimeChecked();
                        getCMCChart();
                        break;
                }
            }
        });
        CMCCoinParcelable coinObject = getActivity().getIntent().getParcelableExtra(GraphTabFragment.COIN_OBJECT);
        setTable(coinObject, rootView);
        return rootView;
    }
    //value selection for graph dropdown dialog
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        //intialise textviews which will be altered with a selection

        TextView currentPrice = rootView.findViewById(R.id.current_price);
        TextView dateTextView = rootView.findViewById(R.id.graphFragmentDateTextView);


        if (currencySymbol.equals("USD")) {
            currentPrice.setText(String.format(getString(R.string.unrounded_usd_chart_price_format), String.valueOf(e.getY())));
        } else {
            currentPrice.setText(currencyFormatter.format(e.getY(), "BTC"));
        }
        dateTextView.setText(getFormattedFullDate(e.getX()));
    }
    @Override
    public void onNothingSelected() {

    }
    public String getFormattedFullDate(float unixSeconds) {
        Date date = new Date((long)unixSeconds);
        return fullDateFormat.format(date);
    }
}
