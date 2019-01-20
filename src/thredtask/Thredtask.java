package thredtask;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

class Thredtask extends JFrame {

    static int flag = 0;
    static int f = 0;
    private static int balance = 0;
    private static int b;
    int x = 0;
    private static Lock lock = new ReentrantLock();
    private static Condition newDeposit = lock.newCondition();
    JTextArea textarea = new JTextArea();
    JFrame frame = new JFrame("syncronized thread");
    JLabel h=new JLabel("Enter Intial Value");
    JTextField textareaintial = new JTextField();
    JButton buttonstart = new JButton("Start");
    JButton buttonstop = new JButton("Pause");
    JButton buttonresum = new JButton("Resume");
    JPanel panel = new JPanel();
    JPanel panel1 = new JPanel();

    Thredtask() {
        action a1 = new action();
        buttonstart.addActionListener(a1);
        buttonstop.addActionListener(a1);
        buttonresum.addActionListener(a1);
        Container c = frame.getContentPane();
        textarea.setForeground(Color.WHITE);
        textarea.setBackground(Color.BLACK);
        textareaintial.setBackground(Color.BLACK);
        textareaintial.setForeground(Color.WHITE);
        buttonstart.setBackground(Color.BLACK);
        buttonstart.setForeground(Color.WHITE);
        buttonstop.setBackground(Color.BLACK);
        buttonstop.setForeground(Color.WHITE);
        buttonresum.setBackground(Color.BLACK);
        buttonresum.setForeground(Color.WHITE);
        h.setBackground(Color.BLACK);
        h.setForeground(Color.BLACK);
        frame.setLayout(new BorderLayout());
        c.add(textarea, BorderLayout.WEST);
        panel.setLayout(new GridLayout(4, 1));
        //panel.add(h);
        panel.add(textareaintial);
        panel.add(buttonstart);
        panel.add(buttonstop);
        panel.add(buttonresum);
        c.add(panel, BorderLayout.EAST);
        frame.setSize(400, 850);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    }

    public class action implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == buttonstart) {
                ExecutorService executor = Executors.newFixedThreadPool(2);
                executor.execute(new DepositTask());
                executor.execute(new WithdrawTask());
                executor.shutdown();
                textarea.append("Thread1" + "                   " + "Thread2" + "                    " + "Balance                 ");
                textarea.append("\n");
            } else if (e.getSource() == buttonstop) {
                flag = 1;
            } else if (e.getSource() == buttonresum && flag == 1) {
                ExecutorService executor = Executors.newFixedThreadPool(2);
                executor.execute(new DepositTask());
                executor.execute(new WithdrawTask());
                executor.shutdown();
                flag = 0;
            }
        }
    }

    public class DepositTask implements Runnable {

        public int getBalance() {
            return balance;
        }

        public void run() {
            try {
                String a = textareaintial.getText();
                int amount = Integer.parseInt(a);
                while (true) {
                    Thread.sleep(1000);
                    lock.lock();
                    try {
                        balance += amount;
                        textarea.append("Deposit  " + amount + "                                                   " + getBalance());
                        textarea.append("\n");
                        newDeposit.signalAll();
                    } finally {
                        lock.unlock();
                    }
                    amount = ((int) (Math.random() * 10) + 1);
                    if (flag == 1) {
                        break;
                    }

                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public class WithdrawTask implements Runnable {

        int amount;

        public int getBalance() {
            return balance;
        }

        public void run() {
            String a = textareaintial.getText();
            amount = Integer.parseInt(a);
            while (true) {
                lock.lock();
                try {
                    while (balance < amount) {
                        textarea.append("\n");
                        textarea.append("                   wait for depoist");
                        textarea.append("\n");
                        newDeposit.await();
                    }
                    balance -= amount;
                    textarea.append("                        withdraw " + amount + "                          " + getBalance());
                    textarea.append("\n");

                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    lock.unlock();
                }
                amount = ((int) (Math.random() * 10) + 1);
                if (flag == 1) {
                    b = balance;
                    x = amount;
                    break;
                }

            }
        }
    }
}

class test {

    public static void main(String[] args) {
        Thredtask v = new Thredtask();
    }
}
