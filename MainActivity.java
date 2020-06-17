package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static int n = 14;
    private static int N = 256;
    private static double W = 2000;
    private double[] signal = new double[N];
    private double[] anothersignal = new double[N];
    double RxxTime;
    double RxyTime;
    long ctime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(generateSignal(signal));
        ctime = System.currentTimeMillis();
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(autoCorrelate(signal));
        RxxTime = (double) (System.currentTimeMillis() - ctime);
        generateSignal(anothersignal);
        ctime = System.currentTimeMillis();
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(Correlate(signal, anothersignal));
        RxyTime = (double) (System.currentTimeMillis() - ctime);
        GraphView graph = findViewById(R.id.graph1);
        customizationGraph(graph, series1, -14, 14);
        graph = findViewById(R.id.graph2);
        customizationGraph(graph, series2, -1, 2);
        graph = findViewById(R.id.graph3);
        customizationGraph(graph, series2, -1, 2);
        Toast.makeText(getApplicationContext(), String.format(
                "Dispersion = %s; MathExpectation = %s; T(Rxx) = %s ms; T(Rxy) = %s ms",
                Dispersion(signal),
                MathExpectation(signal),
                Math.round(RxxTime),
                Math.round(RxyTime)
        ), Toast.LENGTH_LONG).show();
    }

    private DataPoint[] generateSignal(double[] res) {
        float fi, A, x;
        DataPoint[] data = new DataPoint[N];
        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            A = rand.nextFloat();
            fi = rand.nextFloat();
            x = 0;
            for (int j = 0; j < n; j++) {
                x += A * Math.sin(W * i + fi);
            }
            res[i] = x;
            data[i] = new DataPoint(i, x);
        }
        return data;
    }

    public DataPoint[] autoCorrelate(double[] res) {

        double sum;
        double M = MathExpectation(res);
        DataPoint[] data = new DataPoint[N];

        for (int i = 0; i < N; i++) {
            sum = 0;

            for (int tau = 0; tau < N / 2; tau++) {
                sum += (res[i / 2]- M) * (res[(i / 2 + tau)] - M) / (N - 1);
            }

            data[i] = new DataPoint(i, sum);
        }
        return  data;
    }

    public DataPoint[] Correlate(double[] resX, double[] resY) {

        double MX = MathExpectation(resX);
        double MY = MathExpectation(resY);
        DataPoint[] data = new DataPoint[N];
        double sum;

        for (int i = 0; i < N; i++) {
            sum = 0;

            for (int tau = 0; tau < N/2; tau++) {
                sum += (resX[i / 2] - MX) * (resY[i / 2 + tau] - MY)/(N - 1);
            }
            data[i] = new DataPoint(i, sum);
        }
        return data;
    }

    private double MathExpectation(double[] res) {
        double sum = 0;
        for (int t = 0; t < N; t++) {
            sum += res[t];
        }
        return sum/N;
    }

    private double Dispersion(double[] res) {
        double sum = 0;
        double M = MathExpectation(res);

        for (int t = 0; t < N; t++) {
            sum += (res[t] - M)*(res[t] - M);
        }

        return sum/(N - 1);
    }

    private void customizationGraph(GraphView graph, LineGraphSeries line, int miny, int maxy) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(20);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(maxy);
        graph.getViewport().setMinY(miny);
        graph.getViewport().setScrollable(true);
        graph.addSeries(line);
    }

}
