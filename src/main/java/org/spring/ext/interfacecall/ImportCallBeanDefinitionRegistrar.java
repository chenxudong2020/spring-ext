package org.spring.ext.interfacecall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.ext.interfacecall.annotation.InterfaceClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ImportCallBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware,BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(ImportCallBeanDefinitionRegistrar.class);
    private ResourceLoader resourceLoader;
    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private String[] basePackage;


    public String[] getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String[] basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * 默认EnableInterfaceCall注解类所在包下的所有类
     * 或者配置basePackage属性的包下面的所有类
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        List<String> basePackages=new ArrayList<>();
        MultiValueMap<String, Object>  map=importingClassMetadata.getAllAnnotationAttributes(EnableInterfaceCall.class.getName());
        if(map!=null){
            List<Object> basePackageList=map.get("basePackage");
            for(Object basePackageObject:basePackageList){
                String[] basePackageString=(String[])basePackageObject;
                for(String base:basePackageString){
                    basePackages.add(base);
                }
            }
        }

        if(basePackage!=null){
            basePackages.addAll(Arrays.asList(basePackage));
        }

        ImportCallBeanDefinitionScanner scanner = new ImportCallBeanDefinitionScanner(registry, classLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(InterfaceClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        String[] packages=basePackages.toArray(new String[basePackages.size()]);
        scanner.doScan(packages);

    }





    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader=classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
       this.beanFactory=beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }
}
