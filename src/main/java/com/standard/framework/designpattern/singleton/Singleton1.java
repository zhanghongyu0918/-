package com.standard.framework.designpattern.singleton;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhy
 * Date: 2020/4/14
 * Time: 14:43
 * Description: 单例：懒汉式——线程安全，调用效率不高，可以支持延时加载。
 * 这种写法能够在多线程中很好的工作，而且看起来它也具备很好的lazy loading，
 * 但是，遗憾的是，效率很低，99%情况下不需要同步。
 */
public class Singleton1 {
    private static Singleton1 instance;

    private Singleton1() {
    }

    public static synchronized Singleton1 getInstance() {
        if (instance == null) {
            instance = new Singleton1();
        }
        return instance;
    }
}
