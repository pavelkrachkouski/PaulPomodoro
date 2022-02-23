package pomodoro.paulpomodoro;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class MainController {

    private boolean ifTimerRunning = false; //если таймер запущен
    private int tomatoCount = 1; //количество пройденных циклов

    private boolean paused = false; //количество пройденных циклов
    private boolean stop = false; //количество пройденных циклов
    private boolean skip = false; //количество пройденных циклов

    private final String TOMATO_DURATION = "25"; //продолжительность времени работы
    private final String BREAK_DURATION = "5"; //продолжительность времени отдыха
    private final String LONG_BREAK_DURATION = "30"; //продолжительность времени отдыха


    @FXML
    private Label labelTimeLeft;

    @FXML
    private Label informationMessage;

    @FXML
    private Label labelIfPaused;

    @FXML
    private Button buttonStart;

    @FXML
    private Button buttonStop;

    @FXML
    private Button buttonSkip;

    @FXML
    private PieChart pieChart;
    private PieChart.Data sliceLeftTime;
    private PieChart.Data  slicePassedTime;

    @FXML
    public void initialize() {
        /* инициализация стартового значения диаграммы */
        sliceLeftTime = new PieChart.Data("Left", Integer.parseInt(TOMATO_DURATION)); //задаем значение оставшегося времени
        slicePassedTime = new PieChart.Data("Passed", 0); //задаем значение прошедшего времени
        pieChart.getData().add(sliceLeftTime); //добавляем значение оставшегося времени на диаграмму
        pieChart.getData().add(slicePassedTime); //добавляем значение прошедшего времени на диаграмму
        pieChart.setStartAngle(90); //устанавливаем стартовый угол для диаграммы

        /* стартовое значение таймера */
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

    @FXML
    protected void onSkipButtonClick() {
        if (ifTimerRunning) {
            skip = true;
        }
        else {
            System.out.println("Метод помидора не запущен!");
        }
    }






    //Вложенный класс потока в котором запускается таймер
    public class TimerThread implements Runnable {

        @Override
        public void run() {
            System.out.println("Start thread: " + Thread.currentThread().getName()); //сообщение о том что поток запущен

            int leftSeconds = TimeConverter.minToSec(Integer.parseInt(Thread.currentThread().getName())); //осталось секунд
            int passedSeconds = 0; //прошло секунд

            if (!stop) {
                if (tomatoCount % 2 != 0) {
                    Platform.runLater(() -> {
                        informationMessage.setText("Let's work!");
                        SoundClass.playStart();
                    });
                } else {
                    Platform.runLater(() -> {
                        informationMessage.setText("Let's rest!");
                        SoundClass.playBreak();
                    });
                }
            }



            for(int i = leftSeconds; i >= 0 ; i--) {

                //если нажата кнопка паузы цикл стоит на месте (увеличиваем значение итератора, а потом сбрасываем)
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
                //если нажали стоп, пропускам все "помидоры"
                if (stop) {
                    if (tomatoCount == 8) {
                        stop = false;
                    }
                    break;
                }
                //если нажали пропустить, пропускам один помидор "помидоры"
                if (skip) {
                    skip = false;
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                leftSeconds--;
                passedSeconds++;
                //В лямбда выражение можно передавать только константы
                final int finalLeftSeconds = leftSeconds;
                final int finalPassedSeconds = passedSeconds;

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
                ifTimerRunning = false; //переменная определяющая запущен ли поток таймера
                tomatoCount++; //считаем количество выполненных циклов

                //пока количество циклов меньше 8
                if (tomatoCount <= 8)
                {
                    switch (tomatoCount) {
                        case 1, 3, 5, 7 -> {
                            setTimerStartValue(TOMATO_DURATION);
                            startTimerWithParam(TOMATO_DURATION); //вызываем функцию которая снова запускает этот поток
                        }
                        case 2, 4, 6 -> {
                            setTimerStartValue(BREAK_DURATION);
                            startTimerWithParam(BREAK_DURATION); //вызываем функцию которая снова запускает этот поток
                        }
                        case 8 -> {
                            setTimerStartValue(LONG_BREAK_DURATION);
                            startTimerWithParam(LONG_BREAK_DURATION); //вызываем функцию которая снова запускает этот поток
                        }
                        default -> System.out.println("Oh, some error!");
                    }
                }
                else { //если количество циклов больше 8, сбрасываем счетчик, выходим из потока выставляем таймер в стартовое положение
                    setTimerStartValue(TOMATO_DURATION);
                    informationMessage.setText("Let's start!");
                    tomatoCount = 1;
                    buttonStart.setText("Start");
                    buttonStop.setDisable(true);
                    buttonSkip.setDisable(true);
                }
            });
            /* блок который выполняется по завершению таймера */

            System.out.println("Stop thread: " + Thread.currentThread().getName()); //сообщение о том что поток завершен
        }
    }



    //устанавливаем стартовое значение таймера и Диаграммы
    private void setTimerStartValue(String minutes) {
        int seconds = Integer.parseInt(minutes) * 60;
        labelTimeLeft.setText(TimeConverter.secondsToMinAndSec(seconds)); //устанавливаем таймер в стартовую позицию
        //возвращаем значения диаграммы в исходное состояние
        sliceLeftTime.setPieValue(seconds);
        slicePassedTime.setPieValue(0);
    }



    //запускаем таймер в параметры передаем количество минут
    private void startTimerWithParam(String minutes) {
        //если таймер не запущен - запускаем
        if (!ifTimerRunning) {
            ifTimerRunning = true;
            Thread tomatoTimer = new Thread(new TimerThread(), minutes);
            tomatoTimer.start();
            buttonStart.setText("Pause");
            buttonStop.setDisable(false);
            buttonSkip.setDisable(false);
        }
        else {
            paused = !paused;
            if (paused) {
                labelIfPaused.setText("(Paused)");
                buttonStart.setText("UnPause");
                buttonStop.setDisable(true);
                buttonSkip.setDisable(true);
            }
            else
            {
                labelIfPaused.setText("");
                buttonStart.setText("Pause");
                buttonStop.setDisable(false);
                buttonSkip.setDisable(false);
            }
        }
    }


}