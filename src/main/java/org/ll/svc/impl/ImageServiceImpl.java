package org.ll.svc.impl;

import lombok.extern.slf4j.Slf4j;
import org.ll.svc.ImageService;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.opencv.core.Point;

@Service @Slf4j
public class ImageServiceImpl implements ImageService {

    @Autowired private RestTemplate restTemplate;
    @Autowired private Robot robot;

    @Override
    public String classify(Resource resource){
        log.info("restTemplate: {}", restTemplate);
        return null;
    }

    @Override
    public void screenCapture(String targetImgToSave){
//        if(Files.notExists(Paths.get(targetImgToSave))){
//            try {
//                Files.createDirectories(Paths.get(targetImgToSave));
//            } catch (IOException e) {
//                log.error(null, e);
//            }
//        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        BufferedImage screenImg = robot.createScreenCapture(new Rectangle(screenSize));
        try{
            ImageIO.write(screenImg, "png", new File(targetImgToSave));
        } catch (IOException e) {
            log.error(null, e);
        }
    }

    @Override
    public int[] locateOnDesktop(String templImg){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String srcImg = "image/screenImg.png";
        screenCapture(srcImg);
        return locate(srcImg, templImg);
    }

    @Override
    public int[] locate(String srcImg, String templImg){

        Mat img = Imgcodecs.imread(srcImg, Imgcodecs.IMREAD_COLOR);
        Mat templ = Imgcodecs.imread(templImg, Imgcodecs.IMREAD_COLOR);
        Mat mask = new Mat();
        boolean use_mask = false;

        if (img.empty() || templ.empty() || (use_mask && mask.empty())) {
            log.info("Can't read one of the images");
        }

        int match_method = Imgproc.TM_CCORR_NORMED;

        // template matching
        Mat result = new Mat();
        Mat img_display = new Mat();
        img.copyTo(img_display);
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_32FC1);
        Boolean method_accepts_mask = (Imgproc.TM_SQDIFF == match_method || match_method == Imgproc.TM_CCORR_NORMED);
        if (use_mask && method_accepts_mask) {
            Imgproc.matchTemplate(img, templ, result, match_method, mask);
        } else {
            Imgproc.matchTemplate(img, templ, result, match_method);
        }
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        org.opencv.core.Point matchLoc;
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }
        int x = new Double(matchLoc.x).intValue() + (templ.cols() >> 1);
        int y = new Double(matchLoc.y).intValue() + (templ.rows() >> 1);
        log.info("matchLoc: {}, {},  x: {}, y: {}", matchLoc.x, matchLoc.y, x, y);
        Imgproc.rectangle(img_display, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                new Scalar(0, 0, 0), 2, 8, 0);
        Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                new Scalar(0, 0, 0), 2, 8, 0);
//        Image tmpImg = HighGui.toBufferedImage(img_display);
//        ImageIcon icon = new ImageIcon(tmpImg);
//        imgDisplay.setIcon(icon);
        result.convertTo(result, CvType.CV_8UC1, 255.0);
//        tmpImg = HighGui.toBufferedImage(result);
//        icon = new ImageIcon(tmpImg);
//        resultDisplay.setIcon(icon);

        log.info("{} location: {}, {}", templImg, x, y);
        return new int[]{ x, y};
    }

    public void input(String text, int waitSeconds){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);

        waitFor(waitSeconds);
    }
    @Override
    public void input(String text){
        input(text, 1);
    }

    public void scroll(int times){
        robot.mouseWheel(times);
    }
    public void scroll(){
        scroll(1);
    }

    @Override
    public void locateAndClick(String srcImg, String tmplIng){
        locateAndClick(srcImg, tmplIng, 7);
    }


    public void locateAndClickOnDesktop(String tmplIng){
        locateAndClickOnDesktop(tmplIng, 0);
    }

    @Override
    public void locateAndClickOnDesktop(String tmplIng, int waitSeconds){

        int[] location = locateOnDesktop(tmplIng);
        moveMouse(location[0], location[1]);
        waitFor(waitSeconds);
    }
    @Override
    public void locateAndClick(String srcImg, String tmplIng, int waitSeconds){
        int[] location = locate(srcImg, tmplIng);
        moveMouse(location[0], location[1]);

        waitFor(waitSeconds);
    }

    @Override
    public void waitFor(int waitSeconds){
        try {
            TimeUnit.SECONDS.sleep(waitSeconds);
        } catch (InterruptedException e) {
            log.error(null, e);
        }
    }

    @Override
    public void typeKeyboard(int keyCode){
        if (keyCode == 58){ // colon
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_SEMICOLON);
            robot.keyRelease(KeyEvent.VK_SEMICOLON);
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }else{
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        }

    }

    @Override
    public void enter(){
        enter(0);
    }

    @Override
    public void enter(int waitSecond){
        typeKeyboard(KeyEvent.VK_ENTER);
        waitFor(waitSecond);
    }

    public void tab(){
        typeKeyboard(KeyEvent.VK_TAB);
    }

    @Override
    public void ctrlA(int waitSecond){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_A);
        waitFor(waitSecond);
    }

    @Override
    public void moveMouse(int x, int y){
        robot.mouseMove(x, y);
        click();
    }

    public void click(){
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); // mouse left click
    }

    public void doubleCLick(){
        click();
        click();
    }

    public void rightClick(){
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }

    public void inputOneByOne(String text){
        for(char c : text.toCharArray()){
            robot.keyPress((int)c);
            robot.keyRelease((int)c);
        }
    }

}
