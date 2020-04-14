package com.standard.framework.designpattern.singleton;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhy
 * Date: 2020/4/14
 * Time: 14:46
 * Description: 单例：饿汉式——线程安全，调用效率高，但是不支持延迟加载。
 * 这种方式基于classloder机制避免了多线程的同步问题，instance在类装载时就实例化。目前java单例是指一个虚拟机的范围，
 * 因为装载类的功能是虚拟机的，所以一个虚拟机在通过自己的ClassLoader装载饿汉式实现单例类的时候就会创建一个类的实例。
 * 这就意味着一个虚拟机里面有很多ClassLoader，而这些classloader都能装载某个类的话，就算这个类是单例，也能产生很多实例。
 * 当然如果一台机器上有很多虚拟机，那么每个虚拟机中都有至少一个这个类的实例的话，那这样 就更不会是单例了。
 * (这里讨论的单例不适合集群！)
 */
public class Singleton2 {
    private static Singleton2 instance = new Singleton2();

    private Singleton2() {
    }

    public static Singleton2 getInstance() {
        return instance;
    }
}
