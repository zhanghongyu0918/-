package com.standard.framework.designpattern.singleton;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhy
 * Date: 2020/4/14
 * Time: 14:53
 * Description: 单例：枚举——线程安全，调用效率高，不能延时加载，并且可以天然的防止反射和反序列化漏洞。
 * 这种方式是Effective Java作者Josh Bloch 提倡的方式，它不仅能避免多线程同步问题，而且还能防止反序列化重新创建新的对象，可谓是很坚强的壁垒啊。
 */
public enum Singleton4 {
    /**
     * 枚举
     */
    INSTANCE;

    public void whateverMethod() {
    }
}
