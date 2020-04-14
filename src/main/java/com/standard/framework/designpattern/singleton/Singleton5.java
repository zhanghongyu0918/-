package com.standard.framework.designpattern.singleton;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhy
 * Date: 2020/4/14
 * Time: 14:54
 * Description: 单例：双重校验锁——由于JVM底层内部模型原因，偶尔会出问题，不建议使用。
 * 这样方式实现线程安全地创建实例，而又不会对性能造成太大影响。它只是第一次创建实例的时候同步，以后就不需要同步了。
 * 由于volatile关键字屏蔽了虚拟机中一些必要的代码优化，所以运行效率并不是很高，因此建议没有特别的需要不要使用。
 * 双重检验锁方式的单例不建议大量使用，根据情况决定。
 */
public class Singleton5 {
    private volatile static Singleton5 singleton;

    private Singleton5() {
    }

    public static Singleton5 getSingleton() {
        if (singleton == null) {
            synchronized (Singleton5.class) {
                if (singleton == null) {
                    singleton = new Singleton5();
                }
            }
        }
        return singleton;
    }
}
