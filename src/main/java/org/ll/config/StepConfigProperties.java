package org.ll.config;

import lombok.*;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration @NoArgsConstructor @Data
public class StepConfigProperties {

    private List<Step> steps;

    @Getter @Setter @ToString
    public static class Step{
        private boolean enabled = true;
        private String desc;
        private  List<Action> actions;
    }

    @Getter @Setter @ToString
    public static class Action{
        private boolean enabled = true;
        private String input;
        private String inputEnglishOneByOne;
        private String locate;
        private boolean enter;
        private boolean doubleClick;
        private boolean rightClick;
        private boolean tab;
        private int scroll;
        private int timeWait = 0;
    }

}
