import tech.squadmc.squadmcor.client.performance.CachedFloatField;

public final class CachedFloatFieldTest {
    private static class Parent { private float second = 5; private float first = 7; }
    private static final class Child extends Parent { private float second = 11; }
    private static final class IntegerField { private int first = 23; }
    private static final class WrongType { private String first = "not a float"; private float second = 99; }
    public static void main(String[] args) {
        CachedFloatField reader = new CachedFloatField("first", "second");
        Parent parent = new Parent(); Child child = new Child();
        if (reader.get(parent)!=7 || reader.get(child)!=11) throw new AssertionError("Alias/hierarchy priority");
        parent.first=31;
        if (reader.get(parent)!=31) throw new AssertionError("Must cache access, not the changing value");
        if (reader.get(new Object())!=0 || reader.get(new WrongType())!=0) throw new AssertionError("Missing/invalid fallback");
        if (reader.get(new IntegerField())!=23) throw new AssertionError("Numeric widening");
        for(int i=0;i<10000;i++) if(reader.get(child)!=11) throw new AssertionError("Repeated access");
        System.out.println("PASS: field aliases, inherited/private fields, changing values, missing/wrong types, repeated access");
    }
}
