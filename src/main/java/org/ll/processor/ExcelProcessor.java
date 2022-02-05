package org.ll.processor;

import com.sun.rowset.internal.Row;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("org.ll.annotation.Excel")
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ExcelProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for ( TypeElement annotation : annotations ) {
            for ( Element element : roundEnv.getElementsAnnotatedWith(annotation) ) {

            }
        }
        return true;
    }

    private void readFile(File f){
        try(FileInputStream fis = new FileInputStream(f);) {
            XSSFWorkbook sheets = new XSSFWorkbook(fis);
            Sheet sheet1 = sheets.getSheetAt(0);
            for (int i = 1; i < sheet1.getLastRowNum() + 1; i++) {
                Row row1 = sheet1.getRow(i);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
