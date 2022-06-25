package org.spring.ext.interfacecall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.ext.interfacecall.annotation.InterfaceClient;
import org.spring.ext.interfacecall.handler.CacheHandler;
import org.spring.ext.interfacecall.handler.GetHandler;
import org.spring.ext.interfacecall.handler.PostHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author 87260
 */
public class ImportCallBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware, BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(ImportCallBeanDefinitionRegistrar.class);

    private ClassLoader classLoader;
    private BeanFactory beanFactory;

    /**
     * 扫描的含有InterfaceClient注解的接口包
     */
    private String[] basePackage;
    /**
     * 调用三方接口使用的定制RestTemplate
     */
    private Class<? extends ApiRestTemplate> restTemplateClass;


    public String[] getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String[] basePackage) {
        this.basePackage = basePackage;
    }

    public Class<? extends ApiRestTemplate> getRestTemplateClass() {
        return restTemplateClass;
    }

    public void setRestTemplateClass(Class<? extends ApiRestTemplate> restTemplateClass) {
        this.restTemplateClass = restTemplateClass;
    }

    /**
     * 默认EnableInterfaceCall注解类所在包下的所有类
     * 或者配置basePackage属性的包下面的所有类
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        List<Object> listResource = new ArrayList<>();
        List<String> basePackages=new ArrayList<>();
        boolean proxyEnable = false;
        MultiValueMap<String, Object>  map=importingClassMetadata.getAllAnnotationAttributes(EnableInterfaceCall.class.getName());
        if(map!=null){
            List<Object> list=map.get("locations");
            if(list!=null){
                for(Object locationsObj:list){
                    String[] locations=(String[])locationsObj;
                    for(Object location:locations){
                        listResource.add(location);
                    }
                }
            }

            List<Object> basePackageList=map.get("basePackage");
            if(basePackageList!=null) {
                for (Object basePackageObject : basePackageList) {
                    String[] basePackageString = (String[]) basePackageObject;
                    for (String base : basePackageString) {
                        basePackages.add(base);
                    }
                }
            }

            List<Object> restTemplateClassList=map.get("restTemplateClass");
            if(basePackageList!=null) {
                for (Object restTemplateClassObj : restTemplateClassList) {
                    restTemplateClass=(Class<? extends ApiRestTemplate>)restTemplateClassObj;
                    break;
                }
            }


            List<Object> proxyEnableList=map.get("proxyEnable");
            if(proxyEnableList!=null) {
                for (Object proxyEnableObj : proxyEnableList) {
                    proxyEnable=(boolean)proxyEnableObj;
                    break;
                }
            }
        }
        if(basePackage!=null){
            basePackages.addAll(Arrays.asList(basePackage));
        }
        this.registerBean(CallProperties.class,registry);
        this.registerBean(PostHandler.class,registry);
        this.registerBean(GetHandler.class,registry);
        this.registerBean(CacheHandler.class,registry);
        this.registerBean(restTemplateClass,registry);



        this.registerCallInterfaceHandler(registry,restTemplateClass);

        if(proxyEnable) {

        }

        ImportCallBeanDefinitionScanner scanner = new ImportCallBeanDefinitionScanner(registry, classLoader, listResource, beanFactory, restTemplateClass);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(InterfaceClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        String[] packages = basePackages.toArray(new String[basePackages.size()]);
        scanner.doScan(packages);
    }

    /**
     * 判断beanFactory是否存在含有指定Class的对象
     *
     * @param classz
     * @return
     */
    private boolean isInBeanFactory(Class classz) {
        return beanFactory.getBeanProvider(classz).stream().count() != 0;
    }


    private void registerBean(Class classz, BeanDefinitionRegistry registry) {
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(classz);
        genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(classz.getName(), genericBeanDefinition);
    }

    private void registerCallInterfaceHandler(BeanDefinitionRegistry registry,Class restTemplateClass){
        GenericBeanDefinition genericBeanDefinition=new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(CallInterfaceHandler.class);
        genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanFactory);
        genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(restTemplateClass.getName());
        genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(CallInterfaceHandler.class.getName(),genericBeanDefinition);
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


}
