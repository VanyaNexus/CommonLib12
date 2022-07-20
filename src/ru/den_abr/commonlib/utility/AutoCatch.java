package ru.den_abr.commonlib.utility;

public interface AutoCatch
{
    void run() throws Throwable;
    
    static void run(final AutoCatch r) {
        try {
            r.run();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    static void suppress(final AutoCatch r) {
        try {
            r.run();
        }
        catch (Throwable t) {}
    }
}
