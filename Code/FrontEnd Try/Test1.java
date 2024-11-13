import javax.swing.*;
import java.awt.*;
public class Test1 {
    private JFrame frame ;
    private JTextField tf1,tf2;
    private JLabel tl1,tl2;

    Test1(){
        frame = new JFrame();
        frame.setSize(400,400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // frame.doLayout();
        frame.setLayout(new FlowLayout());

        tf1 = new JTextField(20);
        tf2 = new JTextField(20);
        tl1 = new JLabel("Label 1: ");
        tl2 = new JLabel("Label 2: ");

        frame.add(tl1);
        frame.add(tf1);

        frame.add(tl2);
        frame.add(tf2);


        frame.setVisible(true);

    }

    public static void main(String[] args) {
        Test1 test = new Test1();
    }
}
