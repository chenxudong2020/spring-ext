package org.spring.ext.interfacecall.paramhandler;

/**参数处理Handler链
 * @author 87260
 */
public abstract class AbstractHandlerChain {
    /**
     * 设置下个handler
     * @param handler
     */
    public abstract void setNext(AbstractHandlerChain handler);

    /**
     * 当前参数处理
     * @param handler
     * @return
     */
    public abstract AbstractHandlerChain handler(HandlerRequest handler);

    /**
     * 参数验证
     * @param handler
     */
    public abstract void validate(HandlerRequest handler);
}
