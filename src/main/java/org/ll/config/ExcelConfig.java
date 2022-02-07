package org.ll.config;

import org.ll.annotation.Field;

@org.ll.annotation.Excel("/home/ll/documents/1.xlsx")
public class ExcelConfig {
    @Field(column=1)
    public String name;
}
