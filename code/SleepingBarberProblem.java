import java.util.concurrent.Semaphore;

public class SleepingBarberProblem {
  static final int chairCnt = 8;
  static Semaphore chairs = new Semaphore(chairCnt);
  static Semaphore barbers = new Semaphore(0);
  static Semaphore sleepBarbers = new Semaphore(0);

  static class Barber extends Thread {
    String name;
    public Barber(String aname) {
      name = aname;
      sleepBarbers.release();
      System.out.println("[barber] new " + name);
    }
    public void run() {
      try {
        while (true) {
          if (chairs.availablePermits() == chairCnt) {
            if (barbers.tryAcquire()) {
              System.out.println("[barber]" + name + " go to sleep");
              sleepBarbers.release();
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  static class Customer extends Thread {
    String name;
    public Customer(String aname) {
      name = aname;
      System.out.println("[customer] new " + name);
    }
    public void run() {
      try {
        // enter the barbarshop
        if (chairs.tryAcquire()) {
          System.out.println("[customer]" + name + " waiting for barber");
          // try to wake up a barber
          if (sleepBarbers.tryAcquire()) {
            System.out.println("[customer]" + name + " woke up a barber");
            barbers.release();
          }
          barbers.acquire();
          System.out.println("[service]" + name + " begin");
          Thread.sleep(500); // for a while
          System.out.println("[service]" + name + " end");
          barbers.release();
          chairs.release();
          System.out.println("[customer] Customer " + name + " exited");
        } else {
          // all chairs are occupied
          System.out.println("[warning]" + name +
                             " can not find chairs and leaved");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    new Barber("zccz14").start();
    try {
      String prefix = "wuke";
      int cnt = 0;
      while (true) {
        for (int i = 0; i < 10; i++) {
          new Customer(prefix + (++cnt)).start();
        }
        Thread.sleep(6000);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}