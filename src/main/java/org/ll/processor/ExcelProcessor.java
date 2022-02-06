package org.ll.processor;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sun.reflect.generics.tree.Tree;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@SupportedAnnotationTypes({"org.ll.annotation.Excel", "org.ll.annotation.Field"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@Slf4j
public class ExcelProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for ( TypeElement annotation : annotations ) {
            log.info("annotation: {}", annotation);
            if(annotation instanceof org.ll.annotation.Excel) {

            }
            for ( Element element : roundEnv.getElementsAnnotatedWith(annotation) ) {

            }
        }

//        for (Element element : roundEnv.getRootElements()) {
//
//            try {
//                Method cloneMethod = tree.getClass().getMethod("clone");
//                Object cloneTree = cloneMethod.invoke(tree);
//                this.tree = (Tree) cloneTree;
//            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        }
        return true;
    }

    private void readFile(File f){
        try(FileInputStream fis = new FileInputStream(f);) {
            XSSFWorkbook sheets = new XSSFWorkbook(fis);
            Sheet sheet1 = sheets.getSheetAt(0);
            for (int i = 1; i < sheet1.getLastRowNum() + 1; i++) {
                Row r = sheet1.getRow(i);
                r.getCell(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
