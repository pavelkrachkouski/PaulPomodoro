package pomodoro.paulpomodoro;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundClass {
    public static void playStart() {
        String bip = "./src/main/resources/pomodoro/paulpomodoro/sounds/start.mp3/";
        Media hit =  new Media(new File(bip).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }

    public static void playBreak() {
        String bip = "./src/main/resources/pomodoro/paulpomodoro/sounds/qwe.mp3/";
        Media hit =  new Media(new File(bip).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }
}
