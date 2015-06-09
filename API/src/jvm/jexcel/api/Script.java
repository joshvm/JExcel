package jvm.jexcel.api;

import org.apache.poi.ss.usermodel.Workbook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Script<T extends Workbook> {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Manifest {

        public String name();

        public String desc();

        public String author();

        public double version();
    }

    public static class Args{

        private final String[] array;

        public Args(final String[] array){
            this.array = array;
        }

        public String[] getArray(){
            return array;
        }

        public String get(final int i){
            return array[i];
        }

        public int getInt(final int i){
            return Integer.parseInt(get(i));
        }

        public double getDouble(final int i){
            return Double.parseDouble(get(i));
        }
    }

    public void run(final T workbook, final Args args);
}
