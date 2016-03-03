package fr.argus.sim.service.domain;

/**
 * Created by hongjie on 03/03/16.
 */
public class Foo {

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Foo(int value) {
        this.value = value;
    }
    public Foo() {}

    int value;

}
