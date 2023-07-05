import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Lab2 {
    public static void main(String[] args) {
        Common.set_common(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]));
        Lock pen_lock=new ReentrantLock(true);
        Lock bottle_lock=new ReentrantLock(true);
        Lock book_lock=new ReentrantLock(true);
        Condition pen_available =pen_lock.newCondition();
        Condition bottle_available=bottle_lock.newCondition();
        Condition book_available=book_lock.newCondition();
        for(int i=1;i<=Common.getNum_scribe();i++){
            Runnable scribe=new Scribe(i,pen_lock,bottle_lock,book_lock,pen_available,bottle_available,book_available);
            Thread scribe_Thread =new Thread(scribe);
            scribe_Thread.start();
        }
    }
    public class Common {
        private static int num_scribe=0;
        private static int num_pen=0;
        private static int num_bottle=0;
        private static int num_book=0;
        public static void set_common(int num_scribe,int num_pen,int num_bottle,int num_book ){
            Common.num_scribe=num_scribe;
            Common.num_pen=num_pen;
            Common.num_bottle=num_bottle;
            Common.num_book=num_book;
        }
        public static int getNum_scribe() {
            return num_scribe;
        }
        public static int getNum_pen() {
            return num_pen;
        }
        public static void setNum_pen(int num_pen) {
            Common.num_pen = num_pen;
        }
        public static int getNum_bottle() {
            return num_bottle;
        }
        public static void setNum_bottle(int num_bottle) {
            Common.num_bottle = num_bottle;
        }
        public static int getNum_book() {
            return num_book;
        }
        public static void setNum_book(int num_book) {
            Common.num_book = num_book;
        }
    }
    public static class Scribe implements  Runnable{
        private int id;
        private Lock pen_lock;
        private Lock bottle_lock;
        private Lock book_lock;
        private Condition pen_available;
        private Condition bottle_available;
        private Condition book_available;
        public Scribe( int id,Lock pen_lock,Lock bottle_lock,Lock book_lock,Condition pen_available,Condition bottle_available,Condition book_available){
            this.id=id;
            this.pen_lock=pen_lock;
            this.bottle_lock=bottle_lock;
            this.book_lock=book_lock;
            this.pen_available=pen_available;
            this.bottle_available=bottle_available;
            this.book_available=book_available;
        }
        @Override
        public void run() {
            while(true){
                pen_lock.lock();
                try {
                    while(!pen_available()){
                        pen_available.await();
                    }
                    System.out.printf("Scribe %d takes a pen%n", id);
                    use_pen();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    pen_lock.unlock();
                }
                try {
                    Thread.sleep((int)(Math.random() * 10000));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bottle_lock.lock();
                try {
                    while(!bottle_available()){
                        bottle_available.await();
                    }
                    System.out.printf("Scribe %d takes a bottle%n", id);
                    use_bottle();

                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    bottle_lock.unlock();
                }
                try {
                    Thread.sleep((int)(Math.random() * 10000));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                book_lock.lock();
                try {
                    while(!book_available()){
                        book_available.await();
                    }
                    System.out.printf("Scribe %d takes a book%n", id);
                    use_book();

                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    book_lock.unlock();
                }
                try {
                    Thread.sleep((int)(Math.random() * 10000));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("Scribe %d writes a record%n", id);
                try {
                    Thread.sleep((int)(Math.random() * 10000));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                releasePen();
                try {
                    Thread.sleep((int)(Math.random() * 10000));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                releaseInkBottle();
                try {
                    Thread.sleep((int)(Math.random() * 10000));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                releasebook();
                try {
                    Thread.sleep((int)(Math.random() * 10000));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        private void releasebook() {
            book_lock.lock();
            try {
                System.out.printf("Scribe %d puts the book back%n", id);
                Common.setNum_book(Common.getNum_book()+1);
                book_available.signalAll();
            } finally {
                book_lock.unlock();
            }
        }
        private void releaseInkBottle() {
            bottle_lock.lock();
            try {

                System.out.printf("Scribe %d puts the bottle back%n", id);
                Common.setNum_bottle(Common.getNum_bottle()+1);
                bottle_available.signalAll();
            } finally {
                bottle_lock.unlock();
            }
        }
        private void releasePen() {
            pen_lock.lock();
            try {

                System.out.printf("Scribe %d puts the pen back%n", id);
                Common.setNum_pen(Common.getNum_pen()+1);
                pen_available.signalAll();
            } finally {
                pen_lock.unlock();
            }
        }
        private void use_book() {
            Common.setNum_book(Common.getNum_book()-1);
            if (Common.getNum_book() == 0) {
                book_available.signalAll();
            }
        }
        private boolean book_available() {
            book_lock.lock();
            try {
                return Common.getNum_book() > 0;
            } finally {
                book_lock.unlock();
            }
        }
        private void use_bottle() {
            Common.setNum_bottle(Common.getNum_bottle()-1);
            if (Common.getNum_bottle() == 0) {
                bottle_available.signalAll();
            }
        }
        private boolean bottle_available() {
            bottle_lock.lock();
            try {
                return Common.getNum_bottle() > 0;
            } finally {
                bottle_lock.unlock();
            }
        }
        private void use_pen() {
            Common.setNum_pen(Common.getNum_pen()-1);
            if (Common.getNum_pen() == 0) {
                pen_available.signalAll();
            }
        }
        private boolean pen_available() {
            pen_lock.lock();
            try {
                return Common.getNum_pen() > 0;
            } finally {
                pen_lock.unlock();
            }
        }
    }
}