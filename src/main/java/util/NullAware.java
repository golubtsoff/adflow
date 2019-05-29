package util;

import org.apache.commons.beanutils.BeanUtilsBean;
import java.lang.reflect.InvocationTargetException;

public class NullAware extends BeanUtilsBean{

    private static NullAware nullAware = new NullAware();

    private NullAware(){};

    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if(value == null) return;
        super.copyProperty(dest, name, value);
    }

    public static NullAware getInstance(){
        return nullAware;
    }

}
