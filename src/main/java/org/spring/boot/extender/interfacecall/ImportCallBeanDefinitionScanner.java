package org.spring.boot.extender.interfacecall;

import org.spring.boot.extender.interfacecall.annotation.Body;
import org.spring.boot.extender.interfacecall.annotation.InterfaceClient;
import org.spring.boot.extender.interfacecall.annotation.POST;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.MethodMetadata;

import java.util.Objects;
import java.util.Set;


public class ImportCallBeanDefinitionScanner extends ClassPathBeanDefinitionScanner implements ResourceLoaderAware {
    private final ClassLoader classLoader;



    public ImportCallBeanDefinitionScanner(BeanDefinitionRegistry registry, ClassLoader classLoader) {
        super(registry, false);
        this.classLoader = classLoader;

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        super.setResourceLoader(resourceLoader);
    }



    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        CallProperties callProperties=CallProperties.getInstance();
        GenericBeanDefinition genericBeanDefinition;
        for(BeanDefinitionHolder holder:beanDefinitions){
            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) holder.getBeanDefinition();
            AnnotationAttributes annAttr = AnnotationAttributes.fromMap(beanDefinition.getMetadata().getAnnotationAttributes(InterfaceClient.class.getName()));
            String value=annAttr.getString("value");
            Set<MethodMetadata> methodMetadataSet=beanDefinition.getMetadata().getAnnotatedMethods(POST.class.getName());
            for(MethodMetadata methodMetadata:methodMetadataSet){
                String interfaceUrlSuffix=AnnotationAttributes.fromMap(methodMetadata.getAnnotationAttributes(POST.class.getName())).getString("value");
                String interfaceUrl=String.format("%s/%s",value,interfaceUrlSuffix);
                String methodName=methodMetadata.getMethodName();
                String returnName=methodMetadata.getReturnTypeName();
                callProperties.interfaceUrlMap.put(methodName,interfaceUrl);
                callProperties.returnMap.put(methodName,returnName);
            }
            genericBeanDefinition=(GenericBeanDefinition)holder.getBeanDefinition();
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(Objects.requireNonNull(genericBeanDefinition.getBeanClassName()));
            genericBeanDefinition.setBeanClass(CallInterfaceFactoryBean.class);
        }




        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        boolean isCandidate = false;
        if (beanDefinition.getMetadata().isIndependent()) {
            if (!beanDefinition.getMetadata().isAnnotation()) {
                isCandidate = true;
            }
        }
        return isCandidate;
    }

}
