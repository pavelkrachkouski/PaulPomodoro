package pomodoro.paulpomodoro;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;


public class MainController {

    private boolean ifTimerRunning = false; //если таймер запущен
    private int tomatoCount = 1; //количество пройденных циклов
    private boolean paused = false; //количество пройденных циклов
    private boolean stop = false; //количество пройденных циклов

    private final String TOMATO_DURATION = "25"; //продолжительность времени работы
    private final String BREAK_DURATION = "5"; //продолжительность времени отдыха
    private final String LONG_BREAK_DURATION = "30"; //продолжительность времени отдыха


    @FXML
    private Label labelTimeLeft;

    @FXML
    private Label informationMessage;

    @FXML
    private PieChart pieChart;
    private PieChart.Data sliceLeftTime;
    private PieChart.Data  slicePassedTime;

    @FXML
    public void initialize() {
        /* инициализация диаграммы */
        sliceLeftTime = new PieChart.Data("Left", 1); //задаем значение оставшегося времени
        slicePassedTime = new PieChart.Data("Passed", 0); //задаем значение прошедшего времени
        pieChart.getData().add(sliceLeftTime); //добавляем значение оставшегося времени на диаграмму
        pieChart.getData().add(slicePassedTime); //добавляем значение прошедшего времени на диаграмму
        pieChart.setStartAngle(90); //устанавливаем стартовый угол для диаграммы

        labelTimeLeft.setText(TimeConverter.secondsToMinAndSec(Integer.parseInt(TOMATO_DURATION) * 60));
    }

    @FXML
    protected void onStartButtonClick() {
        startTimerWithParam(TOMATO_DURATION);
    }


    @FXML
    protected void onStopButtonClick() {
        if (ifTimerRunning) {
            stop = true;
        }
        else {
            System.out.println("Метод помидора не запущен!");
        }
    }


    public void startTimerWithParam(String minutes) {
        if (!ifTimerRunning) {
            ifTimerRunning = true;
            Thread tomatoTimer = new Thread(new TimerThread(), minutes);
            tomatoTimer.start();
        }
        else {
            paused = !paused;
            System.out.println(paused);
        }
    }



    //Вложенный класс потока в котором запускается таймер
    public class TimerThread implements Runnable {

        @Override
        public void run() {
            System.out.println("Start thread: " + Thread.currentThread().getName());

            if (!stop) {
                if (tomatoCount % 2 != 0) {
                    SoundClass.playStart();
                    Platform.runLater(() -> informationMessage.setText("Начинаем работу!"));
                } else {
                    SoundClass.playBreak();
                    Platform.runLater(() -> informationMessage.setText("Время отдохнуть!"));
                }
            }

            int leftSeconds = TimeConverter.minToSec(Integer.parseInt(Thread.currentThread().getName())); //осталось секунд
            int passedSeconds = 0; //прошло секунд



            for(int i = leftSeconds; i >= 0 ; i--) {

                if (paused) {
                    System.out.println(i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                    continue;
                }

                if (stop) {
                    if (tomatoCount == 8) stop = false;
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                leftSeconds -= 1;
                passedSeconds += 1;

                final int finalLeftSeconds = leftSeconds; //В лямбда выражение можно передавать только переменные значение которым присвоено один раз
                final int finalPassedSeconds = passedSeconds; //В лямбда выражение можно передавать только переменные значение которым присвоено один раз

                /* изменение gui в потоке */
                Platform.runLater(() -> {
                    labelTimeLeft.setText(TimeConverter.secondsToMinAndSec(finalLeftSeconds)); //каждую секунду меняем значение лэйбла с таймером
                    sliceLeftTime.setPieValue(finalLeftSeconds); //изменяем на диаграмме количество оставшихся секунд
                    slicePassedTime.setPieValue(finalPassedSeconds); //изменяем на диаграмме количество прошедших секунд
                });
                /* изменение gui в потоке */
            }



            /* блок который выполняется по завершению таймера */
            Platform.runLater(()->{
                sliceLeftTime.setPieValue(1); //возвращаем диаграмму в исходное состояние
                slicePassedTime.setPieValue(0); //возвращаем диаграмму в исходное состояние
                labelTimeLeft.setText("00:00"); //зануляем лэйбл таймера
                ifTimerRunning = false; //переменная определяющая запущен ли поток таймера

                tomatoCount++; //считаем количество выполненных циклов

                //пока количество циклов меньше 8
                if (tomatoCount <= 8)
                {
                    switch (tomatoCount) {
                        case 1, 3, 5, 7 -> startTimerWithParam(TOMATO_DURATION); //вызываем функцию которая снова запускает этот поток
                        case 2, 4, 6 -> startTimerWithParam(BREAK_DURATION); //вызываем функцию которая снова запускает этот поток
                        case 8 -> startTimerWithParam(LONG_BREAK_DURATION); //вызываем функцию которая снова запускает этот поток
                        default -> System.out.println("Упс, что-то пошло не так!");
                    }
                }
                else { //если количество циклов больше 8, сбрасываем счетчик и выходим из потока
                    tomatoCount = 1;
                    informationMessage.setText("Let's start");
                }
            });
            /* блок который выполняется по завершению таймера */

            System.out.println("Stop thread: " + Thread.currentThread().getName());
        }
    }




}