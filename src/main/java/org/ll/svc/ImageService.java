package org.ll.svc;

import org.springframework.core.io.Resource;

public interface ImageService {
    String classify(Resource resource);

    void screenCapture(String targetImgToSave);

    int[] locateOnDesktop(String templImg);

    int[] locate(String srcImg, String templImg);

    void input(String text);

    void locateAndClick(String srcImg, String tmplIng);

    void locateAndClickOnDesktop(String tmplIng, int waitSeconds);

    void locateAndClick(String srcImg, String tmplIng, int waitSeconds);

    void waitFor(int waitSeconds);

    void typeKeyboard(int keyCode);

    void enter();

    void enter(int waitSecond);

    void ctrlA(int waitSecond);

    void moveMouse(int x, int y);
}
