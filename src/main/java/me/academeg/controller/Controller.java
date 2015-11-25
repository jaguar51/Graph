package me.academeg.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


public class Controller {

    public LineChart<NumberAxis, NumberAxis> graphicsPanel;
    public TextField curveEquation;
    public TextField leftBorderCurve;
    public TextField rightBorderCurve;
    public TextField countInterpolationPoint;
    public TextField countPointForPlot;


    public void clearAll() {
        curveEquation.clear();
        countInterpolationPoint.clear();
        rightBorderCurve.clear();
        leftBorderCurve.clear();
        countPointForPlot.clear();
        graphicsPanel.getData().clear();
    }

    public void clearGraphicPanel() {
        graphicsPanel.getData().clear();
    }

    /**
     * <p>Построение исходного графика</p>
     */
    public void plotStockGraphic() {
        try {
            String curve = curveEquation.getText();
            Expression expr =  new ExpressionBuilder(curve).variable("x").build();

            double leftBorder = Double.parseDouble(leftBorderCurve.getText());
            double rightBorder = Double.parseDouble(rightBorderCurve.getText());
            double addedCoef = (rightBorder - leftBorder) / Double.parseDouble(countPointForPlot.getText());

            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            for (double i = leftBorder; i < rightBorder; i+=addedCoef) {
                data.add(new XYChart.Data<>(i, expr.setVariable("x", i).evaluate()));
            }
           plotGraphic("f(x) = " + curve, data);
        }
        catch (NumberFormatException ex) {
            showError("Enter the left, right border and the number of points to plot graphic");
        }
        catch (Exception ex) {
            showError(ex.getLocalizedMessage());
        }
    }

    /**
     * <p>Построение графика интерполированного на равномерной сетке</p>
     */
    public void plotInterpolateUniformGraphic() {
        try {
            Expression expr =  new ExpressionBuilder(curveEquation.getText()).variable("x").build();
            double leftBorder = Double.parseDouble(leftBorderCurve.getText());
            double rightBorder = Double.parseDouble(rightBorderCurve.getText());

            int countInterpolationPoints = Integer.parseInt(countInterpolationPoint.getText());
            double[] xPoints = new double[countInterpolationPoints];
            double[] yPoints = new double[countInterpolationPoints];

            getNodesUniform(xPoints, yPoints, leftBorder, rightBorder, countInterpolationPoints, expr);
            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            double addedCoef = (rightBorder - leftBorder) / Double.parseDouble(countPointForPlot.getText());
            for (double i = leftBorder; i < rightBorder; i+=addedCoef) {
                data.add(new XYChart.Data<>(i, interpolate(i, xPoints, yPoints)));
            }
            plotGraphic("Интерполяция равномерной сеткой", data);
        }
        catch (NumberFormatException ex) {
            showError("Enter the left, right border, the number of points for the interpolation" +
                    " and the number of points to plot graphic");
        }
        catch (Exception ex) {
            showError(ex.getLocalizedMessage());
        }
    }

    /**
     * <p>Построение графика интерполированного на неравномерной сетке</p>
     */
    public void plotInterpolateUnUniformGraphic() {
        try {
            Expression expr =  new ExpressionBuilder(curveEquation.getText()).variable("x").build();
            double leftBorder = Double.parseDouble(leftBorderCurve.getText());
            double rightBorder = Double.parseDouble(rightBorderCurve.getText());

            int countInterpolationPoints = Integer.parseInt(countInterpolationPoint.getText());
            double[] xPoints = new double[countInterpolationPoints];
            double[] yPoints = new double[countInterpolationPoints];

            getNodesUnUniform(xPoints, yPoints, leftBorder, rightBorder, countInterpolationPoints, expr);
            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            double addedCoef = (rightBorder - leftBorder) / Double.parseDouble(countPointForPlot.getText());
            for (double i = leftBorder; i < rightBorder; i+=addedCoef) {
                data.add(new XYChart.Data<>(i, interpolate(i, xPoints, yPoints)));
            }
            plotGraphic("Интерполяция неравномерной сеткой", data);
        }
        catch (NumberFormatException ex) {
            showError("Enter the left, right border, the number of points for the interpolation" +
                    " and the number of points to plot graphic");
        }
        catch (Exception ex) {
            showError(ex.getLocalizedMessage());
        }
    }

    /**
     * <p>Построение разницы между исходным графиком и интерполяцией на неравномерной сетке</p>
     */
    public void plotDifferenceUnUniformStock() {
        try {
            Expression expr =  new ExpressionBuilder(curveEquation.getText()).variable("x").build();
            double leftBorder = Double.parseDouble(leftBorderCurve.getText());
            double rightBorder = Double.parseDouble(rightBorderCurve.getText());

            int countInterpolationPoints = Integer.parseInt(countInterpolationPoint.getText());
            double[] xPoints = new double[countInterpolationPoints];
            double[] yPoints = new double[countInterpolationPoints];

            getNodesUnUniform(xPoints, yPoints, leftBorder, rightBorder, countInterpolationPoints, expr);
            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            double addedCoef = (rightBorder - leftBorder) / Double.parseDouble(countPointForPlot.getText());
            for (double i = leftBorder; i < rightBorder; i+=addedCoef) {
                data.add(new XYChart.Data<>(i, expr.setVariable("x", i).evaluate() - interpolate(i, xPoints, yPoints)));
            }
            plotGraphic("Разница между исходным графиком и интерполяцией неравномерной сеткой", data);
        }
        catch (NumberFormatException ex) {
            showError("Enter the left, right border, the number of points for the interpolation" +
                    " and the number of points to plot graphic");
        }
        catch (Exception ex) {
            showError(ex.getLocalizedMessage());
        }
    }

    /**
     * <p>Построение разницы между исходным графиком и интерполяцией на равномерной сетке</p>
     */
    public void plotDifferenceUniformStock() {
        try {
            Expression expr =  new ExpressionBuilder(curveEquation.getText()).variable("x").build();
            double leftBorder = Double.parseDouble(leftBorderCurve.getText());
            double rightBorder = Double.parseDouble(rightBorderCurve.getText());

            int countInterpolationPoints = Integer.parseInt(countInterpolationPoint.getText());
            double[] xPoints = new double[countInterpolationPoints];
            double[] yPoints = new double[countInterpolationPoints];

            getNodesUniform(xPoints, yPoints, leftBorder, rightBorder, countInterpolationPoints, expr);
            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            double addedCoef = (rightBorder - leftBorder) / Double.parseDouble(countPointForPlot.getText());
            for (double i = leftBorder; i < rightBorder; i+=addedCoef) {
                data.add(new XYChart.Data<>(i, expr.setVariable("x", i).evaluate() - interpolate(i, xPoints, yPoints)));
            }
            plotGraphic("Разница между исходным графиком и интерполяцией равномерной сеткой", data);
        }
        catch (NumberFormatException ex) {
            showError("Enter the left, right border, the number of points for the interpolation" +
                    " and the number of points to plot graphic");
        }
        catch (Exception ex) {
            showError(ex.getLocalizedMessage());
        }
    }

    /**
     * <p>Получение узлов интерполяции равномерной сетки</p>
     * @param xPoints координаты по x
     * @param yPoints координаты по y
     * @param leftBorder левая граница графика
     * @param rightBorder правая граница графика
     * @param countInterpolationPoints количество точек для интерполяции
     * @param expression функция
     */
    private void getNodesUniform(double[] xPoints, double[] yPoints,
                                 double leftBorder, double rightBorder, int countInterpolationPoints,
                                 Expression expression) {
        double addedCoef = (rightBorder - leftBorder) / (double) countInterpolationPoints;
        for (int i = 0; i < countInterpolationPoints; i++) {
            xPoints[i] = leftBorder + addedCoef * i;
            yPoints[i] = expression.setVariable("x", xPoints[i]).evaluate();
        }
    }

    /**
     * <p>Получение узлов интерполяции неравномерной сетки</p>
     * @param xPoints координаты по x
     * @param yPoints координаты по y
     * @param leftBorder левая граница графика
     * @param rightBorder правая граница графика
     * @param countInterpolationPoints количество точек для интерполяции
     * @param expression функция
     */
    private void getNodesUnUniform(double[] xPoints, double[] yPoints,
                                 double leftBorder, double rightBorder, int countInterpolationPoints,
                                 Expression expression) {
        for (int i = 0; i < countInterpolationPoints; i++) {
            xPoints[i] = (leftBorder + rightBorder) / 2 + (rightBorder - leftBorder) /
                    2 * Math.cos((2*i+1)* Math.PI / (2*(countInterpolationPoints)));
            yPoints[i] = expression.setVariable("x", xPoints[i]).evaluate();
        }
    }

    /**
     * <p>Построение графиков</p>
     * @param seriesName название серии
     * @param data значения узлов
     */
    private void plotGraphic(String seriesName, ObservableList<XYChart.Data> data) {
        XYChart.Series series = new XYChart.Series();
        series.setName(seriesName);
        series.setData(data);
        graphicsPanel.getData().add(series);
    }

    /**
     * <p>Интерполирует по Лагранжу</p>
     * @param arg точка, в которой вычисляем значение функции
     * @param x узлы интерполяции
     * @param y значение в узле
     * @return значение функции в точке
     */
    private double interpolate(double arg, double[] x, double[] y) {
        double result = 0;
        for (int i = 0; i < x.length; i++) {
            double k = 1;
            for (int j = 0; j < y.length; j++) {
                if (j != i) {
                    k *= (arg - x[j]) / (x[i] - x[j]);
                }
            }
            result += k * y[i];
        }
        return result;
    }

    /**
     * <p>Показывает диалог с ошибками</p>
     * @param msg текст ошибки
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResource("/icon.png").toString()));
        alert.showAndWait();
    }

}
