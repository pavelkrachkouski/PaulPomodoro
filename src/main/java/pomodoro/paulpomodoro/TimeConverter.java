package pomodoro.paulpomodoro;

public class TimeConverter {

    //перевод секунд в формат минуты:секунды
    public static String secondsToMinAndSec(int seconds) {
        String min = String.valueOf(seconds / 60);
        String sec = String.valueOf(seconds % 60);

        min = min.length() < 2 ? "0" + min : min;
        sec = sec.length() < 2 ? "0" + sec : sec;

        return min + ":" + sec;
    }


    //перевод минут в секунды
    public static int minToSec(int min) {
        return min * 60;
    }


}
