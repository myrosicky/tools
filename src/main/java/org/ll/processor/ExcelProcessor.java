package org.ll.processor;

import com.google.auto.service.AutoService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ll.annotation.Excel;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("org.ll.annotation.Excel")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ExcelProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "found annotations " + annotations);
        for ( TypeElement annotation : annotations ) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "found annotation " + annotation);
//            if(annotation instanceof org.ll.annotation.Excel) {
//
//            }
            for ( Element element : roundEnv.getElementsAnnotatedWith(annotation) ) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "found annotation at " + element);
                Excel excel = element.getAnnotation(Excel.class);
                readFile(excel.value());
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

    private void readFile(String filePath){
        try(FileInputStream fis = new FileInputStream(filePath)) {
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
