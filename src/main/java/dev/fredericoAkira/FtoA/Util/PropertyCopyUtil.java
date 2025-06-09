package dev.fredericoAkira.FtoA.Util;

import java.beans.FeatureDescriptor;
import java.util.Arrays;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class PropertyCopyUtil {

    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Arrays.stream(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(name -> wrappedSource.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }
}

