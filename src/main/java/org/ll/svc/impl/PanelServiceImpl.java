package org.ll.svc.impl;

import lombok.extern.slf4j.Slf4j;
import org.ll.config.StepConfigProperties;
import org.ll.svc.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class PanelServiceImpl {
    @Autowired private ImageServiceImpl imageService;
    private StepConfigProperties stepConfigProperties;
    @Autowired private Yaml yaml;
    private ExecutorService es;

    private AtomicBoolean pause = new AtomicBoolean(true);
    private AtomicBoolean taskEnd = new AtomicBoolean(false);
    private AtomicReference<String> taskInfo = new AtomicReference<>();

    public boolean isPause(){
        return pause.get();
    }
    public  void setPause(boolean pause){
        this.pause.lazySet(pause);
    }

    public void startJob(){
        setPause(false);
        setTaskEnd(false);
        es = Executors.newFixedThreadPool(1);
        es.execute(r);
    }
    public  boolean getTaskEnd(){
        return taskEnd.get();
    }
    public void setTaskEnd(boolean taskEnd){
        this.taskEnd.lazySet(taskEnd);
    }
    public String getTaskInfo(){
        return taskInfo.get();
    }
    public void setTaskInfo(String taskInfo){
        this.taskInfo.lazySet(taskInfo);
    }

    public List<String> getAllProfiles(){
        return Stream.of(new File("./conf/").listFiles()).map(File::getName)
                .map(n -> n.substring(n.indexOf(0, n.indexOf(".yml"))))
                .collect(Collectors.toList());
    }
    public void reload(String... profiles){
        log.info("reloading {}", profiles);
        stepConfigProperties = Stream.of(profiles).map(profile -> {
            try(InputStream is = new FileInputStream(new File("./conf/"+profile+".yml"));){
                StepConfigProperties obj = yaml.loadAs(is, StepConfigProperties.class);
                log.info("obj: {}", obj);
                return obj;
            }catch (Exception e){
                log.warn(null, e);
            }
            return new StepConfigProperties();
        }).collect(Collectors.toList()).get(0);
        if(es != null){
            es.shutdownNow();
            try{
                es.awaitTermination(3, TimeUnit.SECONDS);
            }catch (InterruptedException e){
                log.error(null, e);
            }
        }
        setTaskEnd(true);
        log.info("stepConfigProperties: {}", stepConfigProperties);
        log.info("reload completed");
    }

    private Runnable r = () -> {
        if(!isPause()){
            log.info("start");
            stepConfigProperties.getSteps().stream().filter(StepConfigProperties.Step::isEnabled).forEach(s -> {
                log.info(">>> {}", s.getDesc());
                setTaskInfo(s.getDesc());
                log.debug("s: {}", s);
                for (StepConfigProperties.Action action : s.getActions()) {
                    if(!isPause() && StringUtils.hasText(action.getLocate())){
                        imageService.locateAndClickOnDesktop(action.getLocate());
                    }
                    if(!isPause() && StringUtils.hasText(action.getInput())){
                        imageService.input(action.getInput());
                    }
                    if(!isPause() && action.isTab()){
                        imageService.tab();
                    }
                    if(!isPause() && action.isDoubleClick()){
                        imageService.doubleCLick();
                    }
                    if(!isPause() && action.isRightClick()){
                        imageService.rightClick();
                    }
                    if(!isPause() && StringUtils.hasText(action.getInputEnglishOneByOne())){
                        imageService.inputOneByOne(action.getInput());
                    }
                    if(!isPause() && action.isEnter()){
                        imageService.enter();
                    }
                    if(!isPause() && action.getScroll() != 0){
                        imageService.scroll(action.getScroll());
                    }
                    if(!isPause() && action.getTimeWait() > 0){
                        imageService.waitFor(action.getTimeWait());
                    }
                }
                log.info("<<< {}", s.getDesc());
            });
            log.info("end");
            setTaskEnd(true);
        }
    };

}
